/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import Dependencies.Versions
import de.heikoseeberger.sbtheader.FileType
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
    description        := "play-soap client",
    crossScalaVersions := Seq(scala212, scala213),
    Dependencies.`play-client`,
  )

lazy val plugin = project
  .in(file("sbt-plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    name        := "sbt-play-soap",
    description := "play-soap sbt plugin",
    Dependencies.plugin,
    crossScalaVersions := Seq(scala212),
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Versions.Play),
    (Compile / resourceGenerators) += generateVersionFile.taskValue,
    scriptedLaunchOpts ++= Seq(
      s"-Dscala.version=${scalaVersion.value}",
      s"-Dscala.crossVersions=${(client / crossScalaVersions).value.mkString(",")}",
      s"-Dproject.version=${version.value}",
      s"-Dcxf.version=${Versions.CXF}",
    ),
    scriptedBufferLog    := false,
    scriptedDependencies := (())
  )

def generateVersionFile =
  Def.task {
    val clientVersion = (client / version).value
    val pluginVersion = version.value
    val file          = (Compile / resourceManaged).value / "play-soap.version.properties"
    val content =
      s"""play-soap-client.version=$clientVersion
         |sbt-play-soap.version=$pluginVersion
     """.stripMargin
    if (!file.exists() || !(IO.read(file) == content)) {
      IO.write(file, content)
    }
    Seq(file)
  }

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
