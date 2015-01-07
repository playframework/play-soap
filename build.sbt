name := "play-soap"
version := "1.0.0-SNAPSHOT"
organization := "com.typeasfe.play"

libraryDependencies ++= Seq(
 "org.apache.cxf" % "cxf-rt-frontend-jaxws" % "3.0.3",
 "org.apache.cxf" % "cxf-rt-transports-http" % "3.0.3" % "test",
 "org.apache.cxf" % "cxf-rt-transports-http-jetty" % "3.0.3" % "test"
)
