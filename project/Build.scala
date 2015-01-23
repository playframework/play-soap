/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.typesafe.play",
    resolvers += Resolver.typesafeRepo("releases")
  )

  object autoImport {
    val CxfVersion = "3.0.3"
    val PlayVersion = "2.3.7"
  }
}

object CrossCompile extends AutoPlugin {
  override def trigger = noTrigger

  override def projectSettings = Seq(
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    scalaVersion := "2.10.4"
  )
}
