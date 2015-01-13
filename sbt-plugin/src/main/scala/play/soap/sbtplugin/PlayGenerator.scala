package play.soap.sbtplugin

trait PlayGenerator {

  def setPlayAttributes() = {
    setAttribute("version", Version.pluginVersion)
    setAttribute("fullversion", Version.name + " " + Version.pluginVersion)
    setAttribute("name", Version.name)
    setAttribute("generatorclass", SbtWsdl.getClass.getName)
  }

  def setAttribute(name: String, value: AnyRef)
}
