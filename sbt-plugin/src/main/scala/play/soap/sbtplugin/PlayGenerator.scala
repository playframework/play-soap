/*
 * Copyright (C) 2009-2015 Typesafe Inc. <http://www.typesafe.com>
 */
package play.soap.sbtplugin

/**
 * Adds common attributes for all source generation templates
 */
trait PlayGenerator {

  /**
   * Set the Play attributes
   */
  def setPlayAttributes() = {
    setAttribute("version", Version.pluginVersion)
    setAttribute("fullversion", Version.name + " " + Version.pluginVersion)
    setAttribute("name", Version.name)
    setAttribute("generatorclass", SbtWsdl.getClass.getName)
  }

  /**
   * Set an attribute
   */
  def setAttribute(name: String, value: AnyRef)
}
