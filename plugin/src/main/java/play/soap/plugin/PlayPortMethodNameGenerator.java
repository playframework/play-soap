/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import org.apache.cxf.tools.common.model.JavaPort;

public class PlayPortMethodNameGenerator {
  public String transform(JavaPort port) {
    return port.getName().substring(0, 1).toLowerCase() + port.getName().substring(1);
  }
}
