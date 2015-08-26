/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */

import com.typesafe.sbt.pgp.PgpKeys._
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = releaseSettings ++ Seq(
    organization := "com.typesafe.play",
    resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    publishArtifactsAction := publishSigned.value,
    tagName := (version in ThisBuild).value
  )

  object autoImport {
    val CxfVersion = "3.0.3"
  }
}

object Publish extends AutoPlugin {
  override def requires = JvmPlugin
  override def projectSettings = Seq(
    publishMavenStyle := false,
    publishTo := Some(Resolver.url("typesafe-rp",
      url(s"https://api.bintray.com/content/typesafe/for-subscribers-only/play-soap/${version.value}/DFDB5DD187A28462DDAF7AB39A95A6AE65983B23/")
    )(Resolver.ivyStylePatterns))
  )
}

object NoPublish extends AutoPlugin {
  override def projectSettings = Seq(
    publishTo := Some(Resolver.file("no-publish", crossTarget.value / "no-publish")),
    publish := {},
    publishLocal := {},
    publishSigned := {}
  )
}
