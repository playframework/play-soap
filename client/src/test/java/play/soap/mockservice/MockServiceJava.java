/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package play.soap.mockservice;

import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;
import java.util.concurrent.CompletionStage;

@WebService(name = "MockService")
public interface MockServiceJava {

    CompletionStage<Bar> getBar(Foo foo);

    CompletionStage<Integer> add(int a, int b);

    CompletionStage<String> multiReturn(Holder<String> part, String toSplit, int index);

    CompletionStage<Void> noReturn(String nothing);

    @Action(fault = {
            @FaultAction(className = SomeException.class)
    })
    CompletionStage<String> declaredException();

    CompletionStage<String> runtimeException();
}
