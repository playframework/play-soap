package build.play.soap

import sbt._
import Keys._

/**
 * For projects that are not to be published.
 */
object NoPublish extends AutoPlugin {
  override def requires = plugins.JvmPlugin

  override def projectSettings = Seq(
    skip in publish := true,
  )
}

class Publish(isLibrary: Boolean) extends AutoPlugin {
  import bintray.BintrayPlugin
  import bintray.BintrayPlugin.autoImport._

  override def trigger  = allRequirements
  override def requires = BintrayPlugin

  val (releaseRepo, snapshotRepo) = 
    if (isLibrary) 
      ("maven", "snapshots")
    else 
      ("sbt-plugin-releases", "sbt-plugin-snapshots")

  override def projectSettings = Seq(
    bintrayOrganization := Some("playframework"),
    bintrayRepository := (if (isSnapshot.value) snapshotRepo else releaseRepo),
    bintrayPackage := "play-soap",
    bintrayReleaseOnPublish := false,
    // maven style should only be used for libraries, not for plugins
    publishMavenStyle := isLibrary,
    bintrayPackageLabels := Seq("playframework", "soap", "plugin")
  )
}

object PublishLibrary extends Publish(isLibrary = true)
object PublishSbtPlugin extends Publish(isLibrary = false)
