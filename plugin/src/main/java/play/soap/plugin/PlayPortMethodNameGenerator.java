/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import org.apache.cxf.tools.common.model.JavaPort;

public class PlayPortMethodNameGenerator {
  public String transform(JavaPort port) {
    return port.getName().substring(0, 1).toLowerCase() + port.getName().substring(1);
  }
}
