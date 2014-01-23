package us.bleibinha.sprayreactivemongodbexample.models

import spray.json.DefaultJsonProtocol.jsonFormat3
import sprest.models.Model
import sprest.models.ModelCompanion
import sprest.reactivemongo.typemappers.jsObjectBSONDocumentWriter

case class Person(
  name: String,
  age: Int,
  var id: Option[String] = None) extends Model[String]

object Person extends ModelCompanion[Person, String] {
  import sprest.Formats._
  implicit val personJsonFormat = jsonFormat3(Person.apply _)
}
