package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.actor.Actor
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError
import spray.http.StatusCodes
import spray.routing.HttpService

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {

  lazy val myRoute =
    path("") {
      put {
        complete {
          val saveResultCode: Future[Option[Int]] = save() map (_.code)
          saveResultCode map {
            _ match {
              case Some(_) ⇒
                StatusCodes.InternalServerError
              case None ⇒
                StatusCodes.OK
            }
          }
        }
      }
    }

  protected val collection = Mongo.testCollection

  protected def save(): Future[LastError] = {
    val bsonDocument = BSONDocument("firstName" -> "Jack")
    collection.insert(bsonDocument)
  }
}
