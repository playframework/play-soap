/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.9.0")

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"       % "2.5.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.8.0")

// Releasing
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
