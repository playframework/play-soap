/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
import net.aichler.jupiter.sbt.Import.JupiterKeys.jupiterVersion
import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  object ScalaVersions {
    val scala213 = "2.13.16"
    val scala3   = "3.3.5"
  }

  object Versions {
    val CXF  = "4.0.8"
    val Play = "3.0.7"
  }

  val `play-client` = libraryDependencies ++= Seq(
    "org.playframework" %% "play"                         % Versions.Play % Provided,
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"        % Versions.CXF  % Provided,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc"    % Versions.CXF  % Provided,
    "org.apache.cxf"     % "cxf-rt-transports-http"       % Versions.CXF  % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-jetty" % Versions.CXF  % Test,
    "org.playframework" %% "play-specs2"                  % Versions.Play % Test
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
    "net.aichler"        % "jupiter-interface"          % jupiterVersion.value % Test,
    "org.testcontainers" % "junit-jupiter"              % "1.21.1"             % Test,
    "org.assertj"        % "assertj-core"               % "3.27.3"             % Test
  )

  val `test-scala` = libraryDependencies ++= Seq(
    "org.apache.cxf" % "cxf-rt-frontend-jaxws"      % Versions.CXF % Test,
    "org.apache.cxf" % "cxf-rt-transports-http-hc5" % Versions.CXF % Test,
    "com.dimafeng"  %% "testcontainers-scala"       % "0.43.0"     % Test,
    "org.scalatest" %% "scalatest"                  % "3.2.19"     % Test,
  )
}
