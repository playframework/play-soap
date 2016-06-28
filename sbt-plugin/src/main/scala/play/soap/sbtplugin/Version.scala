/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin

/**
 * Loads the version info of the Play SOAP plugin.
 */
object Version {

  private lazy val versionProps = {
    val props = new java.util.Properties
    val stream = getClass.getClassLoader.getResourceAsStream("play-soap.version.properties")
    try { props.load(stream) }
    catch { case e: Exception => }
    finally { if (stream ne null) stream.close() }
    props
  }

  /**
   * The version of the client
   */
  lazy val clientVersion = versionProps.getProperty("play-soap-client.version")

  /**
   * The version of the sbt plugin
   */
  lazy val pluginVersion = versionProps.getProperty("play-soap-sbt.version")

  /**
   * The name of the plugin
   */
  val name = "Play SOAP"
}
