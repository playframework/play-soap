import sbt._

object Common {
  val CxfVersion = "3.2.4"
  val PlayVersion = "2.6.13"

  val clientDeps = Seq(
    "com.typesafe.play" %% "play" % PlayVersion % "provided",
    "org.apache.cxf" % "cxf-rt-frontend-jaxws" % CxfVersion,
    "org.apache.cxf" % "cxf-rt-transports-http-hc" % CxfVersion,

    "org.apache.cxf" % "cxf-rt-transports-http" % CxfVersion % "test",
    "org.apache.cxf" % "cxf-rt-transports-http-jetty" % CxfVersion % "test",
    "com.typesafe.play" %% "play-specs2" % PlayVersion % "test"
  )

  val pluginDeps = Seq(
    "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % CxfVersion,
    "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % CxfVersion,
    "org.specs2" %% "specs2-core" % "4.1.0" % "test",
    "commons-codec" % "commons-codec" % "1.10"
  )
}
