/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import jakarta.jws.WebService;
import jakarta.xml.ws.Action;
import jakarta.xml.ws.FaultAction;
import jakarta.xml.ws.Holder;
import scala.Unit;
import scala.concurrent.Future;

@WebService(name = "MockService")
public interface MockServiceScala {

  public Future<Bar> getBar(Foo foo);

  public Future<Integer> add(int a, int b);

  public Future<String> multiReturn(Holder<String> part, String toSplit, int index);

  public Future<Unit> noReturn(String nothing);

  @Action(fault = {@FaultAction(className = SomeException.class)})
  public Future<String> declaredException() throws SomeException;

  public Future<String> runtimeException();
}
