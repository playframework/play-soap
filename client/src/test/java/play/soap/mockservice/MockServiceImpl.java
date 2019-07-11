/*
 * Copyright (C) 2015-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

import javax.jws.WebService;
import javax.xml.ws.Holder;

@WebService(endpointInterface = "play.soap.mockservice.MockService")
public class MockServiceImpl implements MockService {
  public Bar getBar(Foo foo) {
    return new Bar(foo, "bar");
  }

  public int add(int a, int b) {
    return a + b;
  }

  public String multiReturn(Holder<String> part, String toSplit, int index) {
    String first = toSplit.substring(0, index);
    String second = toSplit.substring(index);
    part.value = second;
    return first;
  }

  public void noReturn(String nothing) {
    System.out.println("Received " + nothing);
  }

  public String declaredException() throws SomeException {
    throw new SomeException("an error occurred");
  }

  public String runtimeException() {
    throw new RuntimeException("an error occurred");
  }
}
