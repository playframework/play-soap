/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */

import Dependencies.Versions
import de.heikoseeberger.sbtheader.FileType
import interplay.ScalaVersions._

val commonSettings = Seq(
  scalaVersion := scala212,
  crossScalaVersions := Seq("2.11.12", scala212, scala213),
  headerEmptyLine := false,
  headerLicense := Some(
    HeaderLicense.Custom(
      "Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>"
    )
  )
)

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayRootProject)
  .settings(commonSettings: _*)
  .aggregate(client, plugin, docs)
  .settings(
    name := "play-soap-root",
    releaseCrossBuild := true
  )

lazy val client = project
  .in(file("client"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings: _*)
  .settings(
    name := "play-soap-client",
    Dependencies.`play-client`,
  )

lazy val plugin = project
  .in(file("sbt-plugin"))
  .enablePlugins(PlaySbtPlugin, SbtPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "sbt-play-soap",
    organization := "com.typesafe.sbt",
    Dependencies.plugin,
    crossScalaVersions := Seq(scala212),
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Versions.Play),
    resourceGenerators in Compile += generateVersionFile.taskValue,
    scriptedLaunchOpts ++= Seq(
      s"-Dscala.version=${scalaVersion.value}",
      s"-Dscala.crossVersions=${(crossScalaVersions in client).value.mkString(",")}",
      s"-Dproject.version=${version.value}",
      s"-Dcxf.version=${Versions.CXF}",
    ),
    scriptedBufferLog := false,
    scriptedDependencies := {
      val () = publishLocal.value
      val () = (publishLocal in client).value
    }
  )

lazy val docs = (project in file("docs"))
  .enablePlugins(SbtTwirl)
  .enablePlugins(SbtWeb)
  .enablePlugins(PlayNoPublish)
  .settings(commonSettings: _*)
  .settings(
    crossScalaVersions := Seq(scala212),
    headerMappings := headerMappings.value + (FileType("html") -> HeaderCommentStyle.twirlStyleBlockComment),
    headerSources.in(Compile) ++= sources.in(Compile, TwirlKeys.compileTemplates).value,
    WebKeys.pipeline ++= {
      val clientDocs = (mappings in (Compile, packageDoc) in client).value.map {
        case (file, _name) => file -> ("api/client/" + _name)
      }
      val pluginDocs = (mappings in (Compile, packageDoc) in plugin).value.map {
        case (file, _name) => file -> ("api/sbtwsdl/" + _name)
      }
      clientDocs ++ pluginDocs
    }
  )

def generateVersionFile = Def.task {
  val clientVersion = (version in client).value
  val pluginVersion = version.value
  val file          = (resourceManaged in Compile).value / "play-soap.version.properties"
  val content =
    s"""play-soap-client.version=$clientVersion
       |sbt-play-soap.version=$pluginVersion
     """.stripMargin
  if (!file.exists() || !(IO.read(file) == content)) {
    IO.write(file, content)
  }
  Seq(file)
}

lazy val scriptedTask = TaskKey[Unit]("scripted-task")

playBuildRepoName in ThisBuild := "play-soap"

playBuildExtraPublish := {
  (PgpKeys.publishSigned in plugin).value
}
