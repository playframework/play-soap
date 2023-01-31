/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import Dependencies.ScalaVersions._
import Dependencies.Versions

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
(ThisBuild / dynverVTagPrefix) := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}

lazy val root = project
  .in(file("."))
  .aggregate(client, plugin, mockServer, testJava, testScala)
  .settings(
    name               := "play-soap",
    crossScalaVersions := Nil,
    publish / skip     := true,
    (Compile / headerSources) ++=
      (baseDirectory.value / "project" ** "*.scala" --- (baseDirectory.value ** "target" ** "*")).get
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

lazy val mockServer = project
  .in(file("test/server"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, CxfPlugin)
  .settings(
    name                    := "play-soap-test-server",
    description             := "Play SOAP integration test server",
    crossPaths              := false,
    autoScalaLibrary        := false,
    publish / skip          := true,
    Compile / doc / sources := Seq.empty,
    Dependencies.`mock-server`
  )
  .settings(
    Docker / packageName := "play/soap-test-server",
    Docker / version     := "0.0.0",
    dockerUpdateLatest   := true,
    dockerExposedPorts   := Seq(8080),
    dockerBaseImage      := "eclipse-temurin:11-jre",
  )
  .settings(
    CXF / version := Versions.CXF,
    cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (Compile / resourceDirectory).value / "wsdl" / "primitives.wsdl",
        Seq("-server", "-p", "play.soap.test.primitives")
      ),
      Wsdl(
        "helloworld",
        (Compile / resourceDirectory).value / "wsdl" / "helloworld.wsdl",
        Seq("-server", "-impl", "-p", "play.soap.test.helloworld")
      )
    )
  )

lazy val testJava = project
  .in(file("test/java"))
  .enablePlugins(CxfPlugin)
  .dependsOn(client % Test)
  .settings(
    name                    := "play-soap-test-java",
    description             := "Play SOAP integration tests for Java",
    crossScalaVersions      := Seq(scala213),
    scalaVersion            := scala213,
    publish / skip          := true,
    Compile / doc / sources := Seq.empty,
    Dependencies.`test-java`
  )
  .settings(
    (Test / test) := (Test / test).dependsOn(mockServer / Docker / publishLocal).value,
    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v")
  )
  .settings(
    CXF / version := Versions.CXF,
    CXF / managedClasspath := {
      (CXF / managedClasspath).value ++: (plugin / Compile / exportedProductJars).value
    },
    Test / cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (mockServer / Compile / resourceDirectory).value / "wsdl" / "primitives.wsdl",
        Seq("-fe", "play", "-p", "play.soap.test.primitives", "-xjc-Xplay:lang java")
      )
    ),
    Test / sourceGenerators += (Test / cxfGenerate).taskValue.map { _ =>
      ((Test / cxfGenerate / target).value ** "*.scala").get()
    }
  )

lazy val testScala = project
  .in(file("test/scala"))
  .enablePlugins(CxfPlugin)
  .dependsOn(client % Test)
  .settings(
    name                    := "play-soap-test-scala",
    description             := "Play SOAP integration tests for Scala",
    crossScalaVersions      := Seq(scala213),
    scalaVersion            := scala213,
    publish / skip          := true,
    Compile / doc / sources := Seq.empty,
    Dependencies.`test-scala`
  )
  .settings(
    CXF / version := Versions.CXF,
    CXF / managedClasspath := {
      (CXF / managedClasspath).value ++: (plugin / Compile / exportedProductJars).value
    },
    Test / cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (mockServer / Compile / resourceDirectory).value / "wsdl" / "primitives.wsdl",
        Seq("-fe", "play", "-p", "play.soap.test.primitives", "-xjc-Xplay:lang scala")
      )
    ),
    Test / sourceGenerators += (Test / cxfGenerate).taskValue.map { _ =>
      ((Test / cxfGenerate / target).value ** "*.scala").get()
    }
  )
  .settings(
    (Test / test) := (Test / test).dependsOn(mockServer / Docker / publishLocal).value
  )
