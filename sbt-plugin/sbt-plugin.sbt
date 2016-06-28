/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
name := "play-soap-sbt"

sbtPlugin := true

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % CxfVersion,
  "org.specs2" %% "specs2-core" % "2.4.15" % "test",
  "commons-codec" % "commons-codec" % "1.10"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % PlayVersion)

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
    projectDir / "build.properties"
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
