/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.plugin;

/** The future API */
public interface FutureApi {

  /**
   * The fully qualify class name of the future API
   *
   * @return {@link String}
   */
  String fqn();

  /**
   * The type that the future returns if the method returns void
   *
   * @return {@link String}
   */
  String voidType();

  /** The Play Java Promise API */
  class JavaFuture implements FutureApi {

    @Override
    public String fqn() {
      return "java.util.concurrent.CompletionStage";
    }

    @Override
    public String voidType() {
      return "Void";
    }
  }

  /** The Play Scala Promise API */
  class ScalaFuture implements FutureApi {

    @Override
    public String fqn() {
      return "scala.concurrent.Future";
    }

    @Override
    public String voidType() {
      return "scala.Unit";
    }
  }
}
