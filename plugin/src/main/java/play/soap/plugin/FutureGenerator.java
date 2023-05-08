/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper used by {@link PlaySEIGenerator} to generate the correct future type to wrap a Java
 * type.
 */
public class FutureGenerator {

  private final FutureApi futureApi;

  private final Map<String, String> mapping;

  public FutureGenerator(FutureApi futureApi) {
    this.futureApi = futureApi;
    this.mapping =
        new HashMap<String, String>() {
          {
            put("void", futureApi.voidType());
            put("boolean", "java.lang.Boolean");
            put("byte", "java.lang.Byte");
            put("char", "java.lang.Character");
            put("double", "java.lang.Double");
            put("float", "java.lang.Float");
            put("int", "java.lang.Integer");
            put("long", "java.lang.Long");
            put("short", "java.lang.Short");
          }
        };
  }

  /**
   * Generate the correct future type that holds the given Java type. E.g. for a Java type of "void"
   * this could be "scala.concurrent.Future {@literal <}scala.Unit{@literal >}" or
   * "java.util.concurrent.CompletionStage{@literal <}Void{@literal >}". For a Java type of "int" it
   * could be "scala.concurrent.Future{@literal <}java.lang.Integer{@literal >}" or
   * "java.util.concurrent.CompletionStage{@literal <}java.lang.Integer{@literal >}"
   *
   * @param javaType The Java type to wrap.
   */
  public String futureType(String javaType) {
    String type = mapping.get(javaType);
    String elementType = type != null ? type : javaType;
    return futureApi.fqn() + "<" + elementType + ">";
  }
}
