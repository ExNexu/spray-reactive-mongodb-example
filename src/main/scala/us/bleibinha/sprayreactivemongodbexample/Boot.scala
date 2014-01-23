package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.duration.DurationInt

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

object Boot extends App {
  import Akka.actorSystem

  val service = actorSystem.actorOf(Props[MyServiceActor], "demo-service")

  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
