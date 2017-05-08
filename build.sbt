/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
import interplay.ScalaVersions._

lazy val root = (project in file("."))
  .enablePlugins(PlayRootProject)
  .aggregate(client)
  .settings(
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala211),
    releaseCrossBuild := true
  )

lazy val client = (project in file("client"))
  .enablePlugins(PlayLibrary)
  .settings(
    name := "play-soap-client",
    scalaVersion := scala211,
    crossScalaVersions := Seq(scala211),
    libraryDependencies ++= Common.clientDeps,
    resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

lazy val plugin = (project in file("sbt-plugin"))
  .enablePlugins(PlaySbtPlugin)
  .settings(scriptedSettings: _*)
  .settings(
    name := "play-soap-sbt",
    organization := "com.typesafe.play",
    scalaVersion := scala210,
    crossScalaVersions := Seq(scala210),
    libraryDependencies ++= Common.pluginDeps,
    addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Common.PlayVersion),
    (resourceGenerators in Compile) += generateVersionFile.taskValue,
    scriptedLaunchOpts ++= Seq(
      s"-Dscala.version=${scala211}",
      s"-Dproject.version=${version.value}",
      s"-Dcxf.version=${Common.CxfVersion}",
      s"-Dplay.version=${Common.PlayVersion}"
),
    scriptedDependencies := {
      val () = publishLocal.value
      val () = (publishLocal in client).value
    },
    // A bit hacky here, because we don't want to duplicate stuff everywhere, we change the scripted test directory
    // that's passed to scripted, and prepare it ourselves with shared files copied in
    sbtTestDirectory := target.value / "sbt-test",
    scriptedRun := {

      val oldDir = sourceDirectory.value / "sbt-test"
      val newDir = sbtTestDirectory.value
      val buildDir = (baseDirectory in ThisBuild).value
      val projectDir = buildDir / "project"

      // Shared mappings between all tests
      val shared = Seq(
        projectDir / "build.properties"
      ) pair relativeTo(buildDir)

      // All the test directories
      val tests = (oldDir * "*").get.flatMap(d => (d * "*").get) pair relativeTo(oldDir)

      // All the test files
      val testMappings = oldDir.***.filter(_.isFile) pair relativeTo(oldDir)

      // All mappings are all test files + the shared mappings based on each test directory
      val allMappings = testMappings ++ tests.flatMap {
        case (testDir, _name) => shared.map {
          case (file, mapping) => file -> (_name + "/" + mapping)
        }
      }

      // Sync the mappings to the new directory
      val cache = streams.value.cacheDirectory / "preprocess"
      Sync.apply(cache)(allMappings.map {
        case (file, _name) => file -> (newDir / _name)
      })

      scriptedRun.value
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
       |play-soap-sbt.version=$pluginVersion
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
