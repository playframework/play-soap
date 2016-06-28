/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    scalaVersion := "2.11.8",
    organization := "com.typesafe.play",
    version := "1.0.0-SNAPSHOT",
    resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

  object autoImport {
    val CxfVersion = "3.1.6"
    val PlayVersion = "2.5.4"
  }
}

object NoPublish extends AutoPlugin {
  override def projectSettings = Seq(
    publishTo := Some(Resolver.file("no-publish", crossTarget.value / "no-publish")),
    publish := {},
    publishLocal := {}
  )
}
