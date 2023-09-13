/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
import net.aichler.jupiter.sbt.Import.JupiterKeys.jupiterVersion
import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  object ScalaVersions {
    val scala213 = "2.13.12"
  }

  object Versions {
    val CXF  = "4.0.2"
    val Play = "2.9.0-M7"
  }

  val `play-client` = libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play"                         % Versions.Play % Provided,
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"        % Versions.CXF  % Provided,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc"    % Versions.CXF  % Provided,
    "org.apache.cxf"     % "cxf-rt-transports-http"       % Versions.CXF  % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-jetty" % Versions.CXF  % Test,
    "com.typesafe.play" %% "play-specs2"                  % Versions.Play % Test
  )

  val plugin = libraryDependencies ++= Seq(
    "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws"   % Versions.CXF % Provided,
    "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % Versions.CXF % Provided
  )

  val `mock-server` = libraryDependencies ++= Seq(
    "org.apache.cxf" % "cxf-rt-frontend-jaxws"        % Versions.CXF,
    "org.apache.cxf" % "cxf-rt-transports-http-jetty" % Versions.CXF,
  )

  val `test-java` = libraryDependencies ++= Seq(
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"      % Versions.CXF         % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc5" % Versions.CXF         % Test,
    "com.typesafe.play" %% "play"                       % Versions.Play        % Test, // TODO: remove
    "net.aichler"        % "jupiter-interface"          % jupiterVersion.value % Test,
    "org.testcontainers" % "junit-jupiter"              % "1.19.0"             % Test,
    "org.assertj"        % "assertj-core"               % "3.24.2"             % Test
  )

  val `test-scala` = libraryDependencies ++= Seq(
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"      % Versions.CXF  % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc5" % Versions.CXF  % Test,
    "com.typesafe.play" %% "play"                       % Versions.Play % Test, // TODO: remove
    "com.dimafeng"      %% "testcontainers-scala"       % "0.40.17"     % Test,
    "org.scalatest"     %% "scalatest"                  % "3.2.16"      % Test,
  )
}
