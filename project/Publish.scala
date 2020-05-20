package build.play.soap

import sbt._
import Keys._
import bintray.BintrayPlugin
import bintray.BintrayPlugin.autoImport._

object PublishSettings {

  private def settings(isLibrary: Boolean) = {

    val (releaseRepo, snapshotRepo) = 
      if (isLibrary) 
        ("maven", "snapshots")
      else 
        ("sbt-plugin-releases", "sbt-plugin-snapshots")
    
    Seq(
      bintrayOrganization := Some("playframework"),
      bintrayRepository := (if (isSnapshot.value) snapshotRepo else releaseRepo),
      bintrayPackage := "play-soap",
      bintrayReleaseOnPublish := false,
      // maven style should only be used for libraries, not for plugins
      publishMavenStyle := isLibrary,
      bintrayPackageLabels := Seq("playframework", "soap", "plugin")
    )
  }

  val forLibrary = settings(isLibrary = true)
  val forPlugin = settings(isLibrary = false)
}