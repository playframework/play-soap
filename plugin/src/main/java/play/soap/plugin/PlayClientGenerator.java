/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import java.io.Writer;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.util.ClassCollector;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.ServiceGenerator;

/** Generator for the Play plugin */
public class PlayClientGenerator extends ServiceGenerator implements PlayGenerator {

  @Override
  protected void setCommonAttributes() {
    super.setCommonAttributes();
    setPlayAttributes();
  }

  @Override
  public void setAttribute(String name, Object value) {
    setAttributes(name, value);
  }

  @Override
  protected void doWrite(String templateName, Writer outputs) throws ToolException {
    // Override the template... it should only ever be sei.vm, but in case it's not.
    String newTemplate = templateName;
    if (templateName.endsWith("/service.vm")) {
      newTemplate = "play/soap/plugin/client.vm";
    }
    setAttribute("portMethod", new PlayPortMethodNameGenerator());
    super.doWrite(newTemplate, outputs);
  }

  /** Overridden to make the output name Scala instead of Java. */
  @Override
  protected Writer parseOutputName(String packageName, String filename) throws ToolException {
    register(env.get(ClassCollector.class), packageName, filename);
    return parseOutputName(packageName, filename, ".scala");
  }
}
