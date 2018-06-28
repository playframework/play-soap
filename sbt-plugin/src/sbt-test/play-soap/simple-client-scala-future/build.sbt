/*
 * Copyright (C) 2015-2018 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion := sys.props.getOrElse("scala.version", "2.11.11")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  specs2,
  "org.apache.cxf" % "cxf-rt-transports-http" % sys.props("cxf.version") % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % sys.props("cxf.version") % "test"
)

scalaSource in Test := baseDirectory.value / "tests"

TaskKey[Unit]("checkServiceClients") := {
  val path = crossTarget.value / "wsdl" / "main" / "sources" / "play" / "soap" / "testservice" / "client"
  val tests: Seq[(String, String)] = Seq(
    ("HelloWorldService.scala", "createPort[HelloWorld](new QName(\"http://testservice.soap.play/\"), \"HelloWorld\", \"http://localhost:53915/helloWorld\")"),
    ("PrimitivesService.scala", "createPort[Primitives](new QName(\"http://testservice.soap.play/primitives\"), \"Primitives\", \"http://localhost:53916/primitives\")")
  )
  for ((filename, expectedString) <- tests) {
    val f = path / filename
    println(s"Checking $f for $expectedString")
    val contents = IO.read(f)
    if (!contents.contains(expectedString)) sys.error(s"File $f didn't contain: $expectedString")
  }
  ()
}
