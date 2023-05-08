/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
scalaVersion       := sys.props("scala.version")
crossScalaVersions := sys.props("scala.crossVersions").split(",").toSeq

lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("--release", "11")

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  specs2,
  "org.apache.cxf" % "cxf-rt-transports-http"       % sys.props("cxf.version") % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % sys.props("cxf.version") % "test"
)

Test / scalaSource := baseDirectory.value / "tests"

TaskKey[Unit]("checkServiceClients") := {
  val path = target.value / "wsdl" / "main" / "sources" / "play" / "soap" / "testservice" / "client"
  val tests: Seq[(String, String)] = Seq(
    (
      "HelloWorldService.scala",
      "createPort[HelloWorld](new QName(\"http://testservice.soap.play/\"), \"HelloWorld\", \"http://localhost:53915/helloWorld\")"
    ),
    (
      "PrimitivesService.scala",
      "createPort[Primitives](new QName(\"http://testservice.soap.play/primitives\"), \"Primitives\", \"http://localhost:53916/primitives\")"
    )
  )
  for ((filename, expectedString) <- tests) {
    val f = path / filename
    println(s"Checking $f for $expectedString")
    val contents = IO.read(f)
    if (!contents.contains(expectedString)) sys.error(s"File $f didn't contain: $expectedString")
  }
  ()
}
