name := "play-soap-client"


libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % "provided",
  "org.apache.cxf" % "cxf-rt-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-rt-transports-http" % CxfVersion % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % CxfVersion % "test"
)