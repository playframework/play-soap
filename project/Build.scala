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
  }
}
