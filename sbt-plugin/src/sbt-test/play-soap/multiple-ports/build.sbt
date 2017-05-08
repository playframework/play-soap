/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion := sys.props.getOrElse("scala.version", "2.11.11")

libraryDependencies += "com.typesafe.play" %% "play" % play.core.PlayVersion.current
