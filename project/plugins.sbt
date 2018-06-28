/*
 * Copyright (C) 2015-2018 Lightbend Inc. <https://www.lightbend.com>
 */
addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("1.3.16"))

// Used for generating docs
addSbtPlugin("com.typesafe.sbt" %% "sbt-twirl" % "1.3.15")
addSbtPlugin("com.typesafe.sbt" %% "sbt-web" % "1.4.3")
addSbtPlugin("com.typesafe.sbt" %% "sbt-less" % "1.1.2")
addSbtPlugin("com.typesafe.sbt" %% "sbt-uglify" % "2.0.0")
