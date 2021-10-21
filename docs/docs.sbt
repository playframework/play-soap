/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.webjars"        % "jquery"     % "1.12.4",
  "org.webjars"        % "prettify"   % "4-Mar-2013-1",
  "com.typesafe.play" %% "play-doc"   % "2.1.0"
)

resolvers += Resolver.typesafeRepo("releases")

generateDocs / sources := (baseDirectory.value * "*.md").get
generateDocs / target := WebKeys.webTarget.value / "docs"

lazy val generateDocs = TaskKey[Seq[File]]("generateDocs")

generateDocs := {
  val outdir    = (generateDocs / target).value
  val classpath = (Compile / fullClasspath).value
  val scalaRun  = (run / runner).value
  val log       = streams.value.log
  val baseDir   = baseDirectory.value

  val markdownFiles = (generateDocs / sources).value

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

Assets / resourceGenerators += generateDocs.taskValue
Assets / managedResourceDirectories += (generateDocs / target).value

// This removes a circular dependency
Assets / WebKeys.exportedMappings := Nil

LessKeys.compress := true

pipelineStages := Seq(uglify)

watchSources ++= (generateDocs / sources).value
