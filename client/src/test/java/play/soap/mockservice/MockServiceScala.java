/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import scala.Unit;
import scala.concurrent.Future;

import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;

@WebService(name = "MockService")
public interface MockServiceScala {

  public Future<Bar> getBar(Foo foo);

  public Future<Integer> add(int a, int b);

  public Future<String> multiReturn(Holder<String> part, String toSplit, int index);

  public Future<Unit> noReturn(String nothing);

  @Action(fault = {@FaultAction(className = SomeException.class)})
  public Future<String> declaredException();

  public Future<String> runtimeException();
}
