name := "play-soap-sbt"

sbtPlugin := true

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % CxfVersion,
  "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % CxfVersion,
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % PlayVersion)

