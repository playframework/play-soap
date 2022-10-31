/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

/** Adds common attributes for all source generation templates */
public interface PlayGenerator {

  /** The name of the plugin */
  String NAME = "Play SOAP";

  default String version() {
    return PlayGenerator.class.getPackage().getImplementationVersion();
  }

  /** Set the Play attributes */
  default void setPlayAttributes() {
    setAttribute("version", version());
    setAttribute("fullversion", NAME + " " + version());
    setAttribute("name", NAME);
    setAttribute("generatorclass", "PlayGenerator");
  }

  /** Set an attribute */
  void setAttribute(String name, Object value);
}
