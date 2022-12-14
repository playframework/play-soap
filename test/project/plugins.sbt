/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import java.util.Properties

lazy val playSoapVersion = {
  val props = new Properties()
  IO.load(props, file("../version.properties"))
  props.getProperty("version")
}

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play-soap-plugin"  % playSoapVersion,
  "net.aichler"       % "jupiter-interface" % "0.11.1" % Test
)

addSbtPlugin("io.paymenthighway.sbt" % "sbt-cxf"               % "1.6")
addSbtPlugin("com.github.sbt"        % "sbt-native-packager"   % "1.9.4")
addSbtPlugin("net.aichler"           % "sbt-jupiter-interface" % "0.11.1")
addSbtPlugin("de.heikoseeberger"     % "sbt-header"            % "5.9.0")
addSbtPlugin("org.scalameta"         % "sbt-scalafmt"          % "2.5.0")
addSbtPlugin("com.lightbend.sbt"     % "sbt-java-formatter"    % "0.8.0")
