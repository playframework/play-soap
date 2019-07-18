/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion := sys.props("scala.version")

libraryDependencies += "com.typesafe.play" %% "play" % play.core.PlayVersion.current

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
