/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */

import sbt.Keys.libraryDependencies
import sbt.Keys.version
import sbt._

import java.util.Properties

object Dependencies {

  private lazy val versions: Properties = {
    val props = new Properties()
    IO.load(props, file("../version.properties"))
    props
  }

  object ScalaVersions {
    val scala213: String = versions.getProperty("scala213Version")
  }

  object Versions {
    val CXF: String      = versions.getProperty("cxfVersion")
    val Play: String     = versions.getProperty("playVersion")
    val PlaySoap: String = versions.getProperty("version")
  }

  val java = libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-soap-client"             % Versions.PlaySoap % Test,
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"        % Versions.CXF,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc"    % Versions.CXF,
    "org.apache.cxf"     % "cxf-rt-transports-http"       % Versions.CXF      % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-jetty" % Versions.CXF      % Test,
    "com.typesafe.play" %% "play-specs2"                  % Versions.Play     % Test,
    "org.testcontainers" % "testcontainers"               % "1.17.6"          % Test,
    "org.testcontainers" % "junit-jupiter"                % "1.17.6"          % Test,
    "org.junit.jupiter"  % "junit-jupiter"                % "5.9.2"           % Test,
    "net.aichler"        % "jupiter-interface"            % "0.11.1"          % Test
  )

  val scala = libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-soap-client"             % Versions.PlaySoap % Test,
    "org.apache.cxf"     % "cxf-rt-frontend-jaxws"        % Versions.CXF,
    "org.apache.cxf"     % "cxf-rt-transports-http-hc"    % Versions.CXF,
    "org.apache.cxf"     % "cxf-rt-transports-http"       % Versions.CXF      % Test,
    "org.apache.cxf"     % "cxf-rt-transports-http-jetty" % Versions.CXF      % Test,
    "com.dimafeng"      %% "testcontainers-scala"         % "0.40.12"         % Test,
    "org.scalatest"     %% "scalatest"                    % "3.2.15"          % Test,
  )

  val server = libraryDependencies ++= Seq(
    "org.apache.cxf" % "cxf-rt-frontend-jaxws"        % Versions.CXF,
    "org.apache.cxf" % "cxf-rt-transports-http-hc"    % Versions.CXF,
    "org.apache.cxf" % "cxf-rt-transports-http"       % Versions.CXF,
    "org.apache.cxf" % "cxf-rt-transports-http-jetty" % Versions.CXF,
  )
}
