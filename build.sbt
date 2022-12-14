/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import Dependencies.ScalaVersions._
import java.util.Properties
import java.io.StringWriter

lazy val root = project
  .in(file("."))
  .aggregate(client, plugin)
  .settings(
    name               := "play-soap",
    crossScalaVersions := Nil,
    publish / skip     := true,
    publishLocal       := publishLocal.dependsOn(saveCurrentVersion).value
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

lazy val saveCurrentVersion = taskKey[Unit]("save current version")
ThisBuild / saveCurrentVersion := {
  val props  = new Properties()
  val writer = new StringWriter()
  props.setProperty("version", version.value)
  props.setProperty("cxfVersion", Dependencies.Versions.CXF)
  props.setProperty("playVersion", Dependencies.Versions.Play)
  props.setProperty("scala213Version", Dependencies.ScalaVersions.scala213)
  props.store(writer, "")
  IO.write(baseDirectory.value / "version.properties", writer.getBuffer.toString)
}
