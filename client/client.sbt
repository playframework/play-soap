/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */

name := "play-soap-client"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % "provided",
  "org.apache.cxf" % "cxf-rt-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-rt-transports-http-hc" % CxfVersion,

  "org.apache.cxf" % "cxf-rt-transports-http" % CxfVersion % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % CxfVersion % "test",
  "com.typesafe.play" %% "play-specs2" % PlayVersion % "test"
)

fork in Test := true

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
