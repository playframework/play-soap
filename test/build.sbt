/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.ExecCmd
import de.heikoseeberger.sbtheader.FileType
import Dependencies.ScalaVersions._

lazy val commonSettings = Seq(
  headerEmptyLine := false,
  headerLicense   := Some(HeaderLicense.Custom("Copyright (C) Lightbend Inc. <https://www.lightbend.com>")),
  scalafmtConfig  := file("../.scalafmt.conf"),
  headerMappings ++= Map(
    FileType("wsdl", FileType.xml.firstLinePattern) -> HeaderCommentStyle.xmlStyleBlockComment
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(server, java, scala)
  .settings(commonSettings)
  .settings(
    name               := "test",
    crossScalaVersions := Nil,
    publish / skip     := true,
  )

lazy val java = project
  .in(file("java"))
  .enablePlugins(CxfPlugin)
  .settings(commonSettings)
  .settings(
    name               := "play-soap-java-client",
    description        := "Play SOAP java test client",
    crossScalaVersions := Seq(scala213),
    scalaVersion       := scala213,
    publish / skip     := true,
    Dependencies.java,
    testOptions += Tests.Argument(jupiterTestFramework, "-q", "-v"),
    cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (server / Compile / resourceDirectory).value / "wsdl/primitives.wsdl",
        Seq("-fe", "play", "-p", testPackage, "-xjc-Xplay:lang java")
      )
    )
  )
  .settings((Test / test) := (Test / test).dependsOn(buildDocker).value)

lazy val scala = project
  .in(file("scala"))
  .enablePlugins(CxfPlugin)
  .settings(commonSettings)
  .settings(
    name               := "play-soap-scala-client",
    description        := "Play SOAP scala test client",
    crossScalaVersions := Seq(scala213),
    scalaVersion       := scala213,
    publish / skip     := true,
    Dependencies.scala,
    cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (server / Compile / resourceDirectory).value / "wsdl/primitives.wsdl",
        Seq("-fe", "play", "-p", testPackage, "-xjc-Xplay:lang scala")
      )
    )
  )
  .settings((Test / test) := (Test / test).dependsOn(buildDocker).value)

lazy val server = project
  .in(file("server"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, CxfPlugin)
  .settings(commonSettings)
  .settings(
    ThisBuild / buildDocker := (Docker / publishLocal).value,
    name                    := "play-soap-server",
    description             := "Play SOAP test server",
    publish / skip          := true,
    Compile / doc / sources := Seq.empty,
    crossPaths              := false,
    autoScalaLibrary        := false,
    Dependencies.server
  )
  .settings(
    Docker / packageName := "play/soap-test-server",
    Docker / version     := "0.0.1",
    dockerCommands := Seq(
      Cmd("FROM", "eclipse-temurin:11-jre"),
      Cmd("COPY", "*/opt/docker/lib/*", "/opt/lib/"),
      Cmd("EXPOSE", "8080"),
      ExecCmd("ENTRYPOINT", "java", "-cp", "/opt/lib/*")
    ),
    cxfWSDLs := Seq(
      Wsdl(
        "primitives",
        (Compile / resourceDirectory).value / "wsdl/primitives.wsdl",
        Seq("-server", "-p", testPackage)
      )
    )
  )
lazy val testPackage = "play.soap"

lazy val buildDocker = taskKey[Unit]("Build docker for server")
