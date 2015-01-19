/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = ProjectRef(file("../../").getCanonicalFile.toURI, "plugin")
