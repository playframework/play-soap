/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import Dependencies.ScalaVersions._

lazy val root = project
  .in(file("."))
  .aggregate(client, plugin)
  .settings(
    name               := "play-soap",
    crossScalaVersions := Nil,
    publish / skip     := true
  )

lazy val client = project
  .in(file("client"))
  .settings(
    name               := "play-soap-client",
    description        := "Play SOAP client",
    crossScalaVersions := Seq(scala213),
    Dependencies.`play-client`,
  )

lazy val plugin = project
  .in(file("plugin"))
  .settings(
    name        := "play-soap-plugin",
    description := "Play SOAP plugin for wsdl2java",
    Dependencies.plugin,
    crossPaths       := false,
    autoScalaLibrary := false
  )

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
(ThisBuild / dynverVTagPrefix) := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}

addCommandAlias(
  "validateCode",
  List(
    "headerCheckAll",
    "scalafmtSbtCheck",
    "scalafmtCheckAll",
    "javafmtCheckAll",
  ).mkString(";")
)
