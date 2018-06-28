/*
 * Copyright (C) 2015-2018 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion := sys.props.getOrElse("scala.version", "2.11.11")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

WsdlKeys.futureApi := WsdlKeys.PlayJavaFutureApi

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  "org.apache.tomcat" % "tomcat-servlet-api" % "7.0.57" force(),
  "org.apache.cxf" % "cxf-rt-transports-http" % sys.props("cxf.version") % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % sys.props("cxf.version") % "test"
)

scalaSource in Test := baseDirectory.value / "tests"
