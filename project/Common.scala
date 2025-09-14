/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import Dependencies.ScalaVersions._
import sbtheader.FileType
import sbtheader.HeaderPlugin

object Common extends AutoPlugin {

  import HeaderPlugin.autoImport._

  override def trigger = allRequirements

  override def requires = JvmPlugin && HeaderPlugin

  val repoName = "play-soap"

  override def globalSettings =
    Seq(
      // organization
      organization         := "org.playframework",
      organizationName     := "The Play Framework Project",
      organizationHomepage := Some(url("https://playframework.com/")),
      // scala settings
      scalaVersion := scala213,
      scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "UTF-8", "-Xlint:-options"),
      doc / javacOptions := Seq("-encoding", "UTF-8"),
      // legal
      licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
      // on the web
      homepage := Some(url(s"https://github.com/playframework/${repoName}")),
      developers += Developer(
        "playframework",
        "The Play Framework Contributors",
        "contact@playframework.com",
        url("https://github.com/playframework")
      )
    )

  override def projectSettings =
    Seq(
      headerEmptyLine := false,
      headerLicense   := Some(
        HeaderLicense.Custom(
          "Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>"
        )
      ),
      headerMappings ++= Map(
        FileType("wsdl", FileType.xml.firstLinePattern) -> HeaderCommentStyle.xmlStyleBlockComment
      )
    )
}
