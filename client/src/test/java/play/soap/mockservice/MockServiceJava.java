/*
 * Copyright (C) 2015-2016 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import play.libs.F.Promise;

import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;

@WebService(name = "MockService")
public interface MockServiceJava {

    public Promise<Bar> getBar(Foo foo);

    public Promise<Integer> add(int a, int b);

    public Promise<String> multiReturn(Holder<String> part, String toSplit, int index);

    public Promise<Void> noReturn(String nothing);

    @Action(fault = {
            @FaultAction(className = SomeException.class)
    })
    public Promise<String> declaredException();

    public Promise<String> runtimeException();
}
