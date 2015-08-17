/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(client)

lazy val client = ProjectRef(file("../../").getCanonicalFile.toURI, "client")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

WsdlKeys.packageName := Some("play.soap.testservice.client")

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.0.3" % "test",
  "org.apache.cxf" % "cxf-rt-transports-http-jetty" % "3.0.3" % "test",
  specs2 % "test"
)
