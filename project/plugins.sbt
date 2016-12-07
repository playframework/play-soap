/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.4")
addSbtPlugin("com.eed3si9n" % "sbt-doge" % "0.1.5")
addSbtPlugin("com.typesafe.play" % "interplay" % "1.3.0")

// Used for generating docs
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.2.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

