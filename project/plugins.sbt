/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("2.1.3"))

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.3.0")

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"       % "2.2.1")
addSbtPlugin("com.lightbend.sbt" % "sbt-java-formatter" % "0.4.4")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"         % "4.0.0")

// Used for generating docs
addSbtPlugin("com.typesafe.sbt" %% "sbt-twirl"  % "1.5.0")
addSbtPlugin("com.typesafe.sbt" %% "sbt-web"    % "1.4.4")
addSbtPlugin("com.typesafe.sbt" %% "sbt-less"   % "1.1.2")
addSbtPlugin("com.typesafe.sbt" %% "sbt-uglify" % "2.0.0")
