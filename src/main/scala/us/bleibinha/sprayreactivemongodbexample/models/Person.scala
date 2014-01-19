package us.bleibinha.sprayreactivemongodbexample.models

import spray.json._
import sprest.models._
import sprest.reactivemongo.typemappers._

case class Person(
  name: String,
  age: Int,
  var id: Option[String] = None) extends Model[String]

object Person extends ModelCompanion[Person, String] {
  import sprest.Formats._
  implicit val personJsonFormat = jsonFormat3(Person.apply _)
}
