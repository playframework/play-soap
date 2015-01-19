/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
name := "play-soap-sbt"

sbtPlugin := true

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % CxfVersion,
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % PlayVersion)

scriptedLaunchOpts ++= Seq(
  "-Dproject.version=" + version.value,
  "-XX:MaxPermSize=256m"
)
