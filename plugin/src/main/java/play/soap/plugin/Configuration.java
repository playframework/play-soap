/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import java.util.Arrays;
import play.soap.plugin.FutureApi.JavaFuture;
import play.soap.plugin.FutureApi.ScalaFuture;

public class Configuration {

  public static String OPTION_PREFIX = "Xplay";

  public enum Option {
    LANG("lang");
    final String name;

    Option(String name) {
      this.name = String.format("-%s:%s", OPTION_PREFIX, name);
    }
  }

  public enum Lang {
    JAVA("java", new JavaFuture()),
    SCALA("scala", new ScalaFuture());

    final String name;

    final FutureApi futureApi;

    Lang(String name, FutureApi futureApi) {
      this.name = name;
      this.futureApi = futureApi;
    }

    public static Lang findByName(String name) {
      return Arrays.stream(values())
          .filter(lang -> lang.name.equals(name))
          .findFirst()
          .orElse(null);
    }

    public static boolean exists(String name) {
      return findByName(name) != null;
    }
  }
}
