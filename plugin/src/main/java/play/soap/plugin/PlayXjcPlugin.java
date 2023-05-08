/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import static play.soap.plugin.Configuration.OPTION_PREFIX;
import static play.soap.plugin.Configuration.Option.LANG;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import play.soap.plugin.Configuration.Lang;

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
    int recognized = this.parseLang(args[i]);
    if (recognized == 0) {
      throw new BadCommandLineException("Invalid argument \"" + args[i] + "\"");
    }
    return recognized;
  }

  private int parseLang(String arg) {
    int recognized = 0;
    if (arg.startsWith(LANG.name)) {
      ++recognized;
      String value = arg.substring(LANG.name.length()).trim();
      if (!Lang.exists(value)) {
        throw new IllegalArgumentException("Unknown lang: \"" + value + "\"");
      }
    }
    return recognized;
  }
}
