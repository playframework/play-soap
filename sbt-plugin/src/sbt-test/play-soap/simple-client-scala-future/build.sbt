/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  specs2,
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.0.3" % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % "3.0.3" % "test"
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
    if (!contents.contains(expectedString)) error(s"File $f didn't contain: $expectedString")
  }
  ()
}
