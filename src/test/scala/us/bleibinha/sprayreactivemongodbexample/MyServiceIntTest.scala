package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

import models.Person
import models.Person.personJsonFormat
import Mongo.Persons
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError
import spray.http.HttpEntity.Empty
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.testkit.ScalatestRouteTest

class MyServiceIntTest
    extends FunSpec
    with ShouldMatchers
    with BeforeAndAfter
    with ScalatestRouteTest
    with MyService {

  def actorRefFactory = system

  val personName = "Peter"
  val personAge = 23
  var person = Person(personName, personAge)

  val person2Name = "Mareike"
  val person2Age = 27
  var person2 = Person(person2Name, person2Age)

  describe("MyService - person") {

    describe("PUT") {

      it("should return OK on a put request and perform a databse insert") {

        val resultsBefore = Await.result(Persons.count(), 5 seconds)

        Put("/person", person) ~> myRoute ~> check {
          response.status should be(StatusCodes.OK)
          val responsePerson = responseAs[Person]
          responsePerson.name should be(personName)
          responsePerson.age should be(personAge)
          responsePerson.id should be('defined)
        }

        val resultsAfter = Await.result(Persons.count(), 5 seconds)
        resultsAfter should be(resultsBefore + 1)
      }

      it("should be able to find the put in persons by their Id in the DB") {

        Put("/person", person) ~> myRoute ~> check {
          response.status should be(StatusCodes.OK)
          val responsePerson = responseAs[Person]
          responsePerson.name should be(personName)
          responsePerson.age should be(personAge)
          responsePerson.id should be('defined)
          val responsePersonInDB = Await.result(Persons.findById(responsePerson.id.get), 5 seconds)
          responsePersonInDB should be(Some(responsePerson))
        }
      }
    }

    describe("DELETE") {

      it("should return OK and remove all entities from the collection") {

        Await.result(Persons.add(person), 5 seconds)

        Delete("/person") ~> myRoute ~> check {
          response.status should be(StatusCodes.OK)
          responseAs[String] should be("OK")
        }

        val resultsAfter = Await.result(Persons.count(), 5 seconds)
        resultsAfter should be(0)
      }
    }

    describe("GET") {

      it("should be able to get all persons") {

        val persistedPerson = Await.result(Persons.add(person), 5 seconds)
        persistedPerson.id should be('defined)

        val persistedPerson2 = Await.result(Persons.add(person2), 5 seconds)
        persistedPerson2.id should be('defined)

        val persistedPersons = Set(person, person2)

        Get(s"/person") ~> myRoute ~> check {
          response.status should be(StatusCodes.OK)
          val responsePersons = responseAs[Set[Person]]
          responsePersons should be(persistedPersons)
        }
      }

      describe("find a subset of persons by their Id, name or age") {

        it("should be able to find a person by their name") {
          val persistedPerson = Await.result(Persons.add(person), 5 seconds)
          persistedPerson.id should be('defined)

          val persistedPerson2 = Await.result(Persons.add(person2), 5 seconds)
          persistedPerson2.id should be('defined)

          Get(s"/person?name=$personName") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(1)
            responsePersons(0) should be(persistedPerson)
          }
        }

        it("should be able to find multiple persons by their common name") {
          val persistedPerson = Await.result(Persons.add(person), 5 seconds)
          persistedPerson.id should be('defined)

          val persistedPerson2 = Await.result(Persons.add(person2.copy(name = personName)), 5 seconds)
          persistedPerson2.id should be('defined)

          Get(s"/person?name=$personName") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(2)
            responsePersons should contain(persistedPerson)
            responsePersons should contain(persistedPerson2)
          }
        }

        it("should return a empty list if no person with that name could be found") {
          Get(s"/person?name=$personName") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(0)
          }
        }

        it("should be able to find a person by their age") {
          val persistedPerson = Await.result(Persons.add(person), 5 seconds)
          persistedPerson.id should be('defined)

          val persistedPerson2 = Await.result(Persons.add(person2), 5 seconds)
          persistedPerson2.id should be('defined)

          Get(s"/person?age=$person2Age") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(1)
            responsePersons(0) should be(persistedPerson2)
          }
        }

        it("should be able to find multiple persons by their common age") {
          val persistedPerson = Await.result(Persons.add(person), 5 seconds)
          persistedPerson.id should be('defined)

          val persistedPerson2 = Await.result(Persons.add(person2.copy(age = personAge)), 5 seconds)
          persistedPerson2.id should be('defined)

          Get(s"/person?age=$personAge") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(2)
            responsePersons should contain(persistedPerson)
            responsePersons should contain(persistedPerson2)
          }
        }

        it("should return a empty list if no person with that age could be found") {
          Get(s"/person?age=$personAge") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePersons = responseAs[List[Person]]
            responsePersons.size should be(0)
          }
        }

        it("should be able to find a person by their Id") {

          val persistedPerson = Await.result(Persons.add(person), 5 seconds)
          persistedPerson.id should be('defined)
          val persistId = persistedPerson.id.get

          Get(s"/person?id=$persistId") ~> myRoute ~> check {
            response.status should be(StatusCodes.OK)
            val responsePerson = responseAs[Person]
            responsePerson should be(persistedPerson)
          }
        }

        it("should answer with a BadRequest if the person could not be found") {
          val nonexistentId = "does_not_exist"
          Get(s"/person?id=$nonexistentId") ~> myRoute ~> check {
            response.status should be(StatusCodes.BadRequest)
            response.entity should be(Empty)
          }
        }
      }
    }
  }

  before {
    person = Person(personName, personAge)
    person2 = Person(person2Name, person2Age)
    Await.result(Persons.removeAll(), 5 seconds)
  }
}
