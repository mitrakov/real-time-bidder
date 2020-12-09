name := "mitrakov-rtb"
organization := "com.mitrakov.sandbox"
version := "1.0.0"
scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe.akka" %% "akka-stream" % "2.6.8",
  "com.typesafe.akka" %% "akka-http" % "10.2.1",
  "io.circe" %% "circe-generic" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",
  "org.postgresql" % "postgresql" % "42.2.16",
  "org.tpolecat" %% "doobie-core" % "0.9.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,
)
