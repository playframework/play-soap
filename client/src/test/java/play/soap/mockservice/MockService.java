/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import jakarta.jws.WebService;
import jakarta.xml.ws.Holder;

@WebService
public interface MockService {

  public Bar getBar(Foo foo);

  public int add(int a, int b);

  public String multiReturn(Holder<String> part, String toSplit, int index);

  public void noReturn(String nothing);

  public String declaredException() throws SomeException;

  public String runtimeException();
}
