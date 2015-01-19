/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
lazy val root = (project in file(".")).enablePlugins(PlayJava)

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

WsdlKeys.futureApi := WsdlKeys.PlayJavaFutureApi

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  "org.apache.tomcat" % "tomcat-servlet-api" % "7.0.57" force(),
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.0.3" % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % "3.0.3" % "test"
)

scalaSource in Test := baseDirectory.value / "tests"