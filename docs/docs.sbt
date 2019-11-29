/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.webjars"        % "jquery"     % "1.12.4",
  "org.webjars"        % "prettify"   % "4-Mar-2013-1",
  "com.typesafe.play"  %% "play-doc"  % "2.1.0"
)

resolvers += Resolver.typesafeRepo("releases")

sources in generateDocs := (baseDirectory.value * "*.md").get
target in generateDocs := WebKeys.webTarget.value / "docs"

lazy val generateDocs = TaskKey[Seq[File]]("generateDocs")

generateDocs := {
  val outdir    = (target in generateDocs).value
  val classpath = (fullClasspath in Compile).value
  val scalaRun  = (runner in run).value
  val log       = streams.value.log
  val baseDir   = baseDirectory.value

  val markdownFiles = (sources in generateDocs).value

  // Clear the output directory first
  IO.delete(outdir)

  scalaRun
    .run(
      "play.soap.docs.Generator",
      Attributed.data(classpath),
      Seq(outdir.getAbsolutePath, baseDir.getAbsolutePath) ++ markdownFiles.map(_.getName.dropRight(3)),
      log
    )
    .failed
    .foreach(t => sys.error(t.getMessage))

  (outdir * "*.html").get
}

resourceGenerators in Assets += generateDocs.taskValue
managedResourceDirectories in Assets += (target in generateDocs).value

// This removes a circular dependency
WebKeys.exportedMappings in Assets := Nil

LessKeys.compress := true

pipelineStages := Seq(uglify)

watchSources ++= (sources in generateDocs).value
