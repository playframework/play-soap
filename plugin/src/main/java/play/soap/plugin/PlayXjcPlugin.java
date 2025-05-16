/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import static play.soap.plugin.Configuration.OPTION_PREFIX;
import static play.soap.plugin.Configuration.Option.LANG;
import static play.soap.plugin.Configuration.Option.TARGET;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import play.soap.plugin.Configuration.Lang;
import play.soap.plugin.Configuration.Target;

/** Generate the future type to wrap an original type. */
public class PlayXjcPlugin extends Plugin {

  @Override
  public String getOptionName() {
    return OPTION_PREFIX;
  }

  @Override
  public String getUsage() {
    return "  -Xplay : Generate the future type to wrap an original type.";
  }

  @Override
  public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) throws SAXException {
    return false;
  }

  @Override
  public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException {
    int recognized = this.parseArgument(args[i]);
    if (recognized == 0) {
      throw new BadCommandLineException("Invalid argument \"" + args[i] + "\"");
    }
    return recognized;
  }

  private int parseArgument(String arg) {
    int recognized = 0;
    if (arg.startsWith(LANG.name)) {
      ++recognized;
      String value = arg.substring(LANG.name.length()).trim();
      if (!Lang.exists(value)) {
        throw new IllegalArgumentException("Unknown lang: \"" + value + "\"");
      }
    }
    if (arg.startsWith(TARGET.name)) {
      ++recognized;
      String value = arg.substring(TARGET.name.length()).trim();
      if (!Target.exists(value)) {
        throw new IllegalArgumentException("Unknown target: \"" + value + "\"");
      }
    }
    return recognized;
  }
}
