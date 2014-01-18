package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.MongoDriver

trait Mongo {
  import Akka.actorSystem

  private val driver = new MongoDriver(actorSystem)
  private val connection = driver.connection(List("localhost"))
  private val db = connection("sprayreactivemongodbexample")

  val testCollection = getCollection("testcollection")

  private def getCollection(name: String): BSONCollection =
    db(name)

}
object Mongo extends Mongo
