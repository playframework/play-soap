/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import static play.soap.plugin.Configuration.Lang.SCALA;
import static play.soap.plugin.Configuration.Option.LANG;
import static play.soap.plugin.Configuration.XJC;

import java.io.Writer;
import java.util.Arrays;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator;
import play.soap.plugin.Configuration.Lang;

/** Generates the Service Endpoint Interface, ie the actual thing that gets called. */
public class PlaySEIGenerator extends SEIGenerator implements PlayGenerator {

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
    if (templateName.endsWith("/sei.vm")) {
      newTemplate = "play/soap/plugin/sei.vm";
    }
    // Add the future API to the velocity context.  The reason this must be done here is that the
    // method that invokes
    // doWrite() first clears the context before invoking this.
    setAttributes("future", new FutureGenerator(findLang().futureApi));
    super.doWrite(newTemplate, outputs);
  }

  public Lang findLang() {
    String[] xjcValues = (String[]) env.get(XJC);
    Lang result = SCALA;
    if (xjcValues != null) {
      result =
          Arrays.stream(xjcValues)
              .filter(s -> s.startsWith(LANG.name))
              .map(s -> s.substring(LANG.name.length()).trim())
              .map(Lang::findByName)
              .findFirst()
              .orElse(result);
    }
    return result;
  }
}
