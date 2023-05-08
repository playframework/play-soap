/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion       := sys.props("scala.version")
crossScalaVersions := sys.props("scala.crossVersions").split(",").toSeq

libraryDependencies += "com.typesafe.play" %% "play" % play.core.PlayVersion.current
