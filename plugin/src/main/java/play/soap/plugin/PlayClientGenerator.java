/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import static play.soap.plugin.Configuration.Option.TARGET;
import static play.soap.plugin.Configuration.Target.PLAY;
import static play.soap.plugin.Configuration.XJC;

import java.io.Writer;
import java.util.Arrays;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.util.ClassCollector;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.ServiceGenerator;
import play.soap.plugin.Configuration.Target;

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
    if (PLAY.equals(findTarget())) {
      String newTemplate = templateName;
      if (templateName.endsWith("/service.vm")) {
        newTemplate = "play/soap/plugin/client.vm";
      }
      setAttribute("portMethod", new PlayPortMethodNameGenerator());
      super.doWrite(newTemplate, outputs);
    } else {
      super.doWrite(templateName, outputs);
    }
  }

  /** Overridden to make the output name Scala instead of Java. */
  @Override
  protected Writer parseOutputName(String packageName, String filename) throws ToolException {
    Writer writer;
    if (PLAY.equals(findTarget())) {
      register(env.get(ClassCollector.class), packageName, filename);
      writer = parseOutputName(packageName, filename, ".scala");
    } else {
      writer = super.parseOutputName(packageName, filename);
    }
    return writer;
  }

  private Target findTarget() {
    String[] xjcValues = (String[]) env.get(XJC);
    Target result = null;
    if (xjcValues != null) {
      result =
          Arrays.stream(xjcValues)
              .filter(s -> s.startsWith(TARGET.name))
              .map(s -> s.substring(TARGET.name.length()).trim())
              .map(Target::findByName)
              .findFirst()
              .orElse(null);
    }
    return result;
  }
}
