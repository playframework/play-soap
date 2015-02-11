/*
 * Copyright © 2015 Typesafe, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form or
 * by any means without the express written permission of Typesafe, Inc.
 */
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

// Used for generating docs
libraryDependencies += Defaults.sbtPluginExtra(
  typesafeLibrary("com.typesafe.sbt", "sbt-twirl").value,
  (sbtBinaryVersion in update).value,
  (scalaBinaryVersion in update).value
)

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")

resolvers += Resolver.typesafeRepo("releases")