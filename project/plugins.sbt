/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.9.0")

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"       % "2.4.6")
addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.7.0")

// Releasing
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
