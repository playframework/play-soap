/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

// Used for generating docs
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

