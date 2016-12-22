/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */

import Common._
import sbtrelease.ReleaseStateTransformations._

lazy val root = (project in file("."))
  .enablePlugins(CrossPerProjectPlugin)
  .enablePlugins(PlayRootProject)
  .aggregate(client, plugin)
  .settings(
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala211)
  )

lazy val client = (project in file("client"))
  .enablePlugins(PlayLibrary)
  .settings(
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala211)
  )

lazy val plugin = (project in file("sbt-plugin"))
  .enablePlugins(PlaySbtPlugin)
  .settings(scriptedSettings: _*)
  .settings(
    scalaVersion := scala210,
    crossScalaVersions := Seq(scala210),
    (resourceGenerators in Compile) += generateVersionFile.taskValue,
    scriptedDependencies := {
      val () = publishLocal.value
      val () = (publishLocal in client).value
    },
    scriptedTask := scripted.toTask("").value
  )

lazy val docs = (project in file("docs"))
  .enablePlugins(SbtTwirl)
  .enablePlugins(SbtWeb)
  .enablePlugins(PlayNoPublish)
  .settings(
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala211),
    WebKeys.pipeline ++= {
      val clientDocs = (mappings in (Compile, packageDoc) in client).value.map {
        case (file, name) => file -> ("api/client/" + name)
      }
      val pluginDocs = (mappings in (Compile, packageDoc) in plugin).value.map {
        case (file, name) => file -> ("api/sbtwsdl/" + name)
      }
      clientDocs ++ pluginDocs
    }
  )

def generateVersionFile = Def.task {
  val clientVersion = (version in client).value
  val pluginVersion = version.value
  val file = (resourceManaged in Compile).value / "play-soap.version.properties"
  val content =
    s"""play-soap-client.version=$clientVersion
       |play-soap-sbt.version=$pluginVersion
     """.stripMargin
  if (!file.exists() || !(IO.read(file) == content)) {
    IO.write(file, content)
  }
  Seq(file)
}

lazy val scriptedTask = TaskKey[Unit]("scripted-task")

playBuildRepoName in ThisBuild := "play-soap"

playBuildExtraTests := {
  (scripted in plugin).toTask("").value
}

playBuildExtraPublish := {
  (publish in plugin).value
}

releaseCrossBuild := false
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publish"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
