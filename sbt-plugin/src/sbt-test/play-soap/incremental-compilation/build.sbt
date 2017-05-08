/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion := sys.props.getOrElse("scala.version", "2.11.11")

libraryDependencies += "com.typesafe.play" %% "play" % play.core.PlayVersion.current

// turn off cross paths so that expressions don't need to include the scala version
crossPaths := false

// because the scripted newer command is broken:
// https://github.com/sbt/sbt/pull/1419
InputKey[Unit]("newer") := {
  val args = Def.spaceDelimited("<tocheck> <target>").parsed
  val base: File = baseDirectory.value
  val toCheck = args(0)
  val targetFile = args(1)
  if ((base / toCheck).lastModified() <= (base / targetFile).lastModified()) {
    throw new RuntimeException(s"$toCheck is not newer than $targetFile") with FeedbackProvidedException
  }
}

InputKey[Unit]("contains") := {
  val args = Def.spaceDelimited("<file> <text>").parsed
  val base: File = baseDirectory.value
  val file = args(0)
  val check = args.tail.mkString(" ")
  val contents = IO.read(base / file)
  if (!contents.contains(check)) {
    throw new RuntimeException(s"Could not find '$check' in '$contents'") with FeedbackProvidedException
  }
}

InputKey[Unit]("replace") := {
  val args = Def.spaceDelimited("<file> <word> <replacement>").parsed
  val base: File = baseDirectory.value
  val file = args(0)
  val word = args(1)
  val replacement = args(2)
  val contents = IO.read(base / file)
  IO.write(base / file, contents.replaceAll(word, replacement))
}
