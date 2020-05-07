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

class Publish(releaseRepo: String, snapshotRepo: String) extends AutoPlugin {
  import bintray.BintrayPlugin
  import bintray.BintrayPlugin.autoImport._

  override def trigger  = allRequirements
  override def requires = BintrayPlugin

  override def projectSettings = Seq(
    bintrayOrganization := Some("playframework"),
    bintrayRepository := (if (isSnapshot.value) snapshotRepo else releaseRepo),
    bintrayPackage := "play-soap",
    bintrayReleaseOnPublish := false
  )
}

object PublishLibrary extends Publish("maven", "snapshots")
object PublishSbtPlugin extends Publish("sbt-plugin-releases", "sbt-plugin-snapshots")
