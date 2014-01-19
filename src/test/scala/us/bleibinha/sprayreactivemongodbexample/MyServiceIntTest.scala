package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

import models.Person
import models.Person.personJsonFormat
import Mongo.Persons
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
    with ScalatestRouteTest
    with MyService {

  def actorRefFactory = system

  describe("MyService - person") {

    val personName = "Peter"
    val personAge = 23
    val person = Person(personName, personAge)

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

    describe("GET") {

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
