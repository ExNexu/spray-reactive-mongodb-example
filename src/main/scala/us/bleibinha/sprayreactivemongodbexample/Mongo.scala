package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.MongoDriver

trait Mongo {

  private val driver = new MongoDriver
  private val connection = driver.connection(List("localhost"))
  private val db = connection("sprayreactivemongodbexample")

  val testCollection = getCollection("testcollection")

  private def getCollection(name: String): BSONCollection =
    db(name)

}
object Mongo extends Mongo
