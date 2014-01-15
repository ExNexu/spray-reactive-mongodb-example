name := "spray-reactive-mongodb-example"

organization := "us.bleibinha"

version := "0.1"

scalaVersion := "2.10.3"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "1.9.2" % "test"
)

initialCommands := "import us.bleibinha.sprayreactivemongodbexample._"

