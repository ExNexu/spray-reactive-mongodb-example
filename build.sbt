name := "spray-reactive-mongodb-example"

organization := "us.bleibinha"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.2" % "test"
)

initialCommands := "import us.bleibinha.sprayreactivemongodbexample._"

