/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.typesafe.play",
    version := "1.0",
    resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  ) ++ mimaDefaultSettings

  object autoImport {
    val CxfVersion = "3.0.3"
    val PlayVersion = "2.4.2"
  }
}

object NoPublish extends AutoPlugin {
  override def projectSettings = Seq(
    publishTo := Some(Resolver.file("no-publish", crossTarget.value / "no-publish")),
    publish := {},
    publishLocal := {}
  )
}
