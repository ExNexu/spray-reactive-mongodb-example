name := "spray-reactive-mongodb-example"

organization := "us.bleibinha"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val akkaV = "2.2.3"
  val sprayV = "1.2.0"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV,
    "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
    "org.scalatest" %% "scalatest" % "1.9.2" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test"
  )
}

initialCommands := "import us.bleibinha.sprayreactivemongodbexample._"

seq(Revolver.settings: _*)

