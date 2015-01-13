package play.soap.sbtplugin

object Version {

  private lazy val versionProps = {
    val props = new java.util.Properties
    val stream = getClass.getClassLoader.getResourceAsStream("play-soap.version.properties")
    try { props.load(stream) }
    catch { case e: Exception => }
    finally { if (stream ne null) stream.close() }
    props
  }

  lazy val clientVersion = versionProps.getProperty("play-soap-client.version")

  lazy val pluginVersion = versionProps.getProperty("play-soap-sbt.version")

  val name = "Play SOAP"
}
