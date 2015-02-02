/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

// Used for generating docs
libraryDependencies += Defaults.sbtPluginExtra(
  typesafeLibrary("com.typesafe.sbt", "sbt-twirl").value,
  (sbtBinaryVersion in update).value,
  (scalaBinaryVersion in update).value
)

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

resolvers += Resolver.typesafeRepo("releases")