version := "0.0.1"

name := "metricscalc-scala"

scalaVersion := "2.13.6"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint:_")

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "mainargs" % "0.2.1",
  "com.lihaoyi" %% "os-lib" % "0.7.8",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "jp.ne.opt" %% "chronoscala" % "1.0.0",
  "org.typelevel" %% "cats-core" % "2.6.1",
  "org.log4s" %% "log4s" % "1.10.0",
  "org.slf4j" % "slf4j-simple" % "1.7.31",
  "org.scalameta" %% "munit" % "0.7.27" % Test
)

libraryDependencies += "com.google.guava" % "guava" % "30.1.1-jre"

Test / parallelExecution := false

enablePlugins(JavaAppPackaging)

maintainer := "laufer@cs.luc.edu"
