/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.0.3" % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % "3.0.3" % "test"
)

scalaSource in Test := baseDirectory.value / "tests"