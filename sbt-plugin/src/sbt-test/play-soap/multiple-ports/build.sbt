/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion       := sys.props("scala.version")
crossScalaVersions := sys.props("scala.crossVersions").split(",").toSeq

libraryDependencies += "com.typesafe.play" %% "play" % play.core.PlayVersion.current
