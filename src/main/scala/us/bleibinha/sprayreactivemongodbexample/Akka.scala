package us.bleibinha.sprayreactivemongodbexample

import akka.actor.ActorSystem

object Akka {
  implicit val actorSystem = ActorSystem("actorsystem")
}
