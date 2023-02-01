/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import jakarta.jws.WebService;
import jakarta.xml.ws.Action;
import jakarta.xml.ws.FaultAction;
import jakarta.xml.ws.Holder;
import java.util.concurrent.CompletionStage;

@WebService(name = "MockService")
public interface MockServiceJava {

  CompletionStage<Bar> getBar(Foo foo);

  CompletionStage<Integer> add(int a, int b);

  CompletionStage<String> multiReturn(Holder<String> part, String toSplit, int index);

  CompletionStage<Void> noReturn(String nothing);

  @Action(fault = {@FaultAction(className = SomeException.class)})
  CompletionStage<String> declaredException() throws SomeException;

  CompletionStage<String> runtimeException();
}
