/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */

 import interplay.ScalaVersions._

// To keep all the Scala versions consistent with the Scala versions
// provided by interplay.
 val commonSettings = Seq(
   scalaVersion := scala212,
   crossScalaVersions := Seq(scala211, scala212)
 )

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayRootProject)
  .aggregate(client)
  .settings(
    name := "play-soap-root",
    releaseCrossBuild := true
  )

lazy val client = project
  .in(file("client"))
  .enablePlugins(PlayLibrary)
  .settings(
    name := "play-soap-client",
    libraryDependencies ++= Common.clientDeps,
    resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

lazy val plugin = project
  .in(file("sbt-plugin"))
  .enablePlugins(PlaySbtPlugin)
  .settings(
    name := "sbt-play-soap",
    organization := "com.typesafe.sbt",
    libraryDependencies ++= Common.pluginDeps,
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Common.PlayVersion),
    resourceGenerators in Compile += generateVersionFile.taskValue,
    scriptedLaunchOpts ++= Seq(
      s"-Dscala.version=${scalaVersion.value}",
      s"-Dproject.version=${version.value}",
      s"-Dcxf.version=${Common.CxfVersion}",
      s"-Dplay.version=${Common.PlayVersion}"
    ),
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
  val file = (resourceManaged in Compile).value / "play-soap.version.properties"
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
