import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {
  object Versions {
    val CXF  = "3.3.4"
    val Play = "2.8.0"
  }

  val `play-client` = libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play"                        % Versions.Play % Provided,
    "org.apache.cxf"    % "cxf-rt-frontend-jaxws"        % Versions.CXF,
    "org.apache.cxf"    % "cxf-rt-transports-http-hc"    % Versions.CXF,
    "org.apache.cxf"    % "cxf-rt-transports-http"       % Versions.CXF % Test,
    "org.apache.cxf"    % "cxf-rt-transports-http-jetty" % Versions.CXF % Test,
    "com.typesafe.play" %% "play-specs2"                 % Versions.Play % Test
  )

  val plugin = libraryDependencies ++= Seq(
    "commons-codec"  % "commons-codec"                     % "1.13",
    "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws"   % Versions.CXF,
    "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % Versions.CXF,
    "org.specs2"     %% "specs2-core"                      % "4.8.1" % Test
  )
}
