/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0
)

addSbtPlugin("com.typesafe.play" % "sbt-play-soap" % sys.props("project.version"))
