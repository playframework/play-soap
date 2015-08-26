/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
name := "play-soap-sbt"

sbtPlugin := true

publishMavenStyle := false

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % CxfVersion,
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

libraryDependencies += Defaults.sbtPluginExtra(
  TypesafeLibrary.playSbtPlugin.value,
  (sbtBinaryVersion in update).value, 
  (scalaBinaryVersion in update).value
)

scriptedLaunchOpts ++= Seq(
  "-Dproject.version=" + version.value,
  "-XX:MaxPermSize=256m"
)

// A bit hacky here, because we don't want to duplicate stuff everywhere, we change the scripted test directory
// that's passed to scripted, and prepare it ourselves with shared files copied in
sbtTestDirectory := target.value / "sbt-test"
scriptedRun := {

  val oldDir = sourceDirectory.value / "sbt-test"
  val newDir = sbtTestDirectory.value
  val buildDir = (baseDirectory in ThisBuild).value
  val projectDir = buildDir / "project"

  // Shared mappings between all tests
  val shared = Seq(
    projectDir / "build.properties",
    projectDir / "typesafe.properties",
    projectDir / "project" / "typesafe.sbt"
  ) pair relativeTo(buildDir)

  // All the test directories
  val tests = (oldDir * "*").get.flatMap(d => (d * "*").get) pair relativeTo(oldDir)

  // All the test files
  val testMappings = oldDir.***.filter(_.isFile) pair relativeTo(oldDir)

  // All mappings are all test files + the shared mappings based on each test directory
  val allMappings = testMappings ++ tests.flatMap {
    case (testDir, name) => shared.map {
      case (file, mapping) => file -> (name + "/" + mapping)
    }
  }

  // Sync the mappings to the new directory
  val cache = streams.value.cacheDirectory / "preprocess"
  Sync.apply(cache)(allMappings.map {
    case (file, name) => file -> (newDir / name)
  })

  scriptedRun.value
}
