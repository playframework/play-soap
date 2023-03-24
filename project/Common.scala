/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import Dependencies.ScalaVersions._
import de.heikoseeberger.sbtheader.FileType
import de.heikoseeberger.sbtheader.HeaderPlugin

object Common extends AutoPlugin {

  import HeaderPlugin.autoImport._

  override def trigger = allRequirements

  override def requires = JvmPlugin && HeaderPlugin

  val repoName = "play-soap"

  override def globalSettings =
    Seq(
      // organization
      organization         := "com.typesafe.play",
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
      headerLicense   := Some(HeaderLicense.Custom("Copyright (C) Lightbend Inc. <https://www.lightbend.com>")),
      headerMappings ++= Map(
        // TODO: use `FileType.xml.firstLinePattern` instead after release https://github.com/sbt/sbt-header/issues/310
        FileType("wsdl", Some("(<\\?xml.*\\?>(?:\\s+))([\\S\\s]*)".r)) -> HeaderCommentStyle.xmlStyleBlockComment
      )
    )
}
