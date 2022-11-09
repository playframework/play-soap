// Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// NOTE: !!! THIS IS A COPY !!!                                                                               //
// For edit this file use a main version in https://github.com/playframework/.github/blob/main/sbt/common.sbt //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

addCommandAlias(
  "formatCode",
  List(
    "headerCreateAll",
    "scalafmtSbt",
    "scalafmtAll",
    "javafmtAll"
  ).mkString(";")
)

addCommandAlias(
  "validateCode",
  List(
    "headerCheckAll",
    "scalafmtSbtCheck",
    "scalafmtCheckAll",
    "javafmtCheckAll"
  ).mkString(";")
)
