package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.api.MongoDriver
import spray.json.RootJsonFormat
import sprest.Formats._
import sprest.models.UniqueSelector
import sprest.models.UUIDStringId
import sprest.reactivemongo.typemappers._
import sprest.reactivemongo.{ ReactiveMongoPersistence, BsonProtocol }

trait Mongo extends ReactiveMongoPersistence {
  import Akka.actorSystem

  private val driver = new MongoDriver(actorSystem)
  private val connection = driver.connection(List("localhost"))
  private val db = connection("sprayreactivemongodbexample")

  // Json mapping to / from BSON - in this case we want "_id" from BSON to be
  // mapped to "id" in JSON in all cases
  implicit object JsonTypeMapper extends SprayJsonTypeMapper with NormalizedIdTransformer

  abstract class UnsecuredDAO[M <: sprest.models.Model[String]](collName: String)(implicit jsformat: RootJsonFormat[M]) extends CollectionDAO[M, String](db(collName)) {

    case class Selector(id: String) extends UniqueSelector[M, String]

    override def generateSelector(id: String) = Selector(id)
    override protected def addImpl(m: M)(implicit ec: ExecutionContext) = doAdd(m)
    override protected def updateImpl(m: M)(implicit ec: ExecutionContext) = doUpdate(m)
    override def remove(selector: Selector)(implicit ec: ExecutionContext) = uncheckedRemoveById(selector.id)

    protected val collection = db(collName)
    def removeAll()(implicit ec: ExecutionContext) = collection.remove(BSONDocument.empty)
    def findAll()(implicit ec: ExecutionContext) = find(BSONDocument.empty)
  }

  // MongoDB collections:
  import models._
  object Persons extends UnsecuredDAO[Person]("persons") with UUIDStringId {
    def findByName(name: String)(implicit ec: ExecutionContext) = find(BSONDocument("name" → name))
    def findByAge(age: Int)(implicit ec: ExecutionContext) = find(BSONDocument("age" → age))
  }

}
object Mongo extends Mongo
