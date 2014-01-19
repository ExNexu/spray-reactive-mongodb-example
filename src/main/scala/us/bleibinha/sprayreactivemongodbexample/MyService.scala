package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.Actor
import models.Person
import models.Person.personJsonFormat
import Mongo.Persons
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError
import spray.http.ContentTypes
import spray.http.HttpEntity
import spray.http.HttpResponse
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.json.pimpAny
import spray.routing.HttpService

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {

  lazy val myRoute =
    path("person") {
      put {
        entity(as[Person]) { person ⇒
          detach() {
            complete {
              Persons.add(person)
            }
          }
        }
      } ~
        parameters('id.as[String]) { id ⇒
          detach() {
            complete {
              val person = Persons.findById(id)
              person map { person ⇒
                person match {
                  case Some(person) ⇒
                    HttpResponse(
                      StatusCodes.OK,
                      HttpEntity(ContentTypes.`application/json`, person.toJson.prettyPrint)
                    )
                  case None ⇒
                    HttpResponse(StatusCodes.BadRequest)
                }
              }
            }
          }
        }
    }
}
