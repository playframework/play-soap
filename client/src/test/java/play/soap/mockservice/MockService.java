/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
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
