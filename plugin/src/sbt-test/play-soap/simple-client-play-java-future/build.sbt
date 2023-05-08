/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion       := sys.props("scala.version")
crossScalaVersions := sys.props("scala.crossVersions").split(",").toSeq

lazy val root = (project in file(".")).enablePlugins(PlayJava)

javacOptions ++= Seq("--release", "11")

WsdlKeys.futureApi := WsdlKeys.PlayJavaFutureApi

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-rt-transports-http"       % sys.props("cxf.version") % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % sys.props("cxf.version") % "test"
)

Test / scalaSource := baseDirectory.value / "tests"
