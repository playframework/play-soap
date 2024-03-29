#!/usr/bin/env python

# This script generates source files for testing primitive types in SOAP.
# To run it, simply invoke it without any arguments. It will generate several
# files in the Scala and Java client directory.
#
# e.g.
# $ python generate-primitives.py

import os

def gen(f, text):
  f.write(text)

def genForPrimitives(f, text):
  primitives = [
    ('boolean', 'java.lang.Boolean',  'true',       'true'),
    ('byte',    'java.lang.Byte',     '1.toByte',   '(byte) 1'),
    ('double',  'java.lang.Double',   '1.0d',       '1.0d'),
    ('float',   'java.lang.Float',    '1.0f',       '1.0f'),
    ('int',     'java.lang.Integer',  '1',          '1'),
    ('long',    'java.lang.Long',     '1L',         '1L'),
    ('short',   'java.lang.Short',    '1.toShort',  '(short) 1')
  ]
  # p = primitive, b = box, s = Scala value, j = Java value
  for p, b, s, j in primitives:
    gen(f, text.format(p=p, b=b, s=s, j=j))

scriptDir = os.path.dirname(os.path.abspath(__file__))
pluginTestDir = os.path.abspath(os.path.join(scriptDir, '..', 'sbt-plugin', 'src', 'sbt-test', 'play-soap'))

print('Generating files in {}'.format(pluginTestDir))

for project in ['simple-client-scala-future', 'simple-client-play-java-future']:

  #### primitives.wsdl ####

  with open(os.path.join(pluginTestDir, project, 'conf', 'wsdls', 'primitives.wsdl'), 'w') as f:
    gen(f,
"""<?xml version='1.0' encoding='UTF-8'?>
<wsdl:definitions xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://testservice.soap.play/primitives" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:ns1="http://schemas.xmlsoap.org/soap/http" name="Primitives" targetNamespace="http://testservice.soap.play/primitives">
  <!--
   ~ Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
   -->

  <!--
   ~ THIS FILE IS AUTO GENERATED. DO NOT EDIT THIS FILE MANUALLY.
   ~
   ~ Run 'generate-primitives.py' to regenerate it.
  -->

  <wsdl:types>
    <xs:schema attributeFormDefault="unqualified" elementFormDefault="unqualified" targetNamespace="http://testservice.soap.play/primitives">
    """)

    genForPrimitives(f, """
      <xs:element name="{p}Op">
        <xs:complexType name="{p}Op">
          <xs:sequence>
            <xs:element name="x" type="xs:{p}"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="{p}OpResponse">
        <xs:complexType name="{p}OpResponse">
          <xs:sequence>
            <xs:element name="return" type="xs:{p}"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="{p}Sequence">
        <xs:complexType name="{p}Sequence">
          <xs:sequence>
            <xs:element name="xs" type="xs:{p}" minOccurs="0" maxOccurs="unbounded"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>
      <xs:element name="{p}SequenceResponse">
        <xs:complexType name="{p}SequenceResponse">
          <xs:sequence>
            <xs:element name="return" type="xs:{p}" minOccurs="0" maxOccurs="unbounded"/>
          </xs:sequence>
        </xs:complexType>
      </xs:element>""")

    gen(f, """
    </xs:schema>
  </wsdl:types>
""")

    genForPrimitives(f, """
    <wsdl:message name="{p}Op">
      <wsdl:part element="tns:{p}Op" name="parameters"/>
    </wsdl:message>
    <wsdl:message name="{p}OpResponse">
      <wsdl:part element="tns:{p}OpResponse" name="return"/>
    </wsdl:message>""")

    genForPrimitives(f, """
  <wsdl:message name="{p}Sequence">
    <wsdl:part element="tns:{p}Sequence" name="parameters"/>
  </wsdl:message>
  <wsdl:message name="{p}SequenceResponse">
    <wsdl:part element="tns:{p}SequenceResponse" name="return"/>
  </wsdl:message>""")

    gen(f, """
  <wsdl:portType name="Primitives">""")

    genForPrimitives(f, """
    <wsdl:operation name="{p}Op">
      <wsdl:input message="tns:{p}Op" name="{p}Op"/>
      <wsdl:output message="tns:{p}OpResponse" name="{p}OpResponse"/>
    </wsdl:operation>
    <wsdl:operation name="{p}Sequence">
      <wsdl:input message="tns:{p}Sequence" name="{p}Sequence"/>
      <wsdl:output message="tns:{p}SequenceResponse" name="{p}SequenceResponse"/>
    </wsdl:operation>""")

    gen(f, """
  </wsdl:portType>

  <wsdl:binding name="PrimitivesServiceSoapBinding" type="tns:Primitives">
    <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>""")

    genForPrimitives(f, """
    <wsdl:operation name="{p}Op">
      <soap:operation soapAction="" style="document"/>
      <wsdl:input name="{p}Op">
        <soap:body use="literal"/>
      </wsdl:input>
      <wsdl:output name="{p}OpResponse">
        <soap:body use="literal"/>
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="{p}Sequence">
      <soap:operation soapAction="" style="document"/>
      <wsdl:input name="{p}Sequence">
        <soap:body use="literal"/>
      </wsdl:input>
      <wsdl:output name="{p}SequenceResponse">
        <soap:body use="literal"/>
      </wsdl:output>
    </wsdl:operation>""")

    gen(f, """
  </wsdl:binding>
  <wsdl:service name="PrimitivesService">
    <wsdl:port binding="tns:PrimitivesServiceSoapBinding" name="Primitives">
      <soap:address location="http://localhost:53916/primitives"/>
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>""")

  ### Primitives.java ###

  with open(os.path.join(pluginTestDir, project, 'app', 'play', 'soap', 'testservice', 'Primitives.java'), 'w') as f:
    gen(f, """/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/*
 * THIS FILE IS AUTO GENERATED. DO NOT EDIT THIS FILE MANUALLY.
 *
 * Run 'generate-primitives.py' to regenerate it.
 */

@WebService(targetNamespace = "http://testservice.soap.play/primitives")
public interface Primitives {""")
    genForPrimitives(f, """
    public {p} {p}Op(@WebParam(name = "x") {p} x);
    public java.util.List<{b}> {p}Sequence(@WebParam(name = "xs") {p} xs);
""")

    gen(f, """
}""")

  ### PrimitivesImpl.java ###

  with open(os.path.join(pluginTestDir, project, 'app', 'play', 'soap', 'testservice', 'PrimitivesImpl.java'), 'w') as f:
    gen(f, """/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.testservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/*
 * THIS FILE IS AUTO GENERATED. DO NOT EDIT THIS FILE MANUALLY.
 *
 * Run 'generate-primitives.py' to regenerate it.
 */

@WebService(
        targetNamespace = "http://testservice.soap.play/primitives",
        endpointInterface = "play.soap.testservice.Primitives",
        serviceName = "PrimitivesService", portName = "Primitives")
public class PrimitivesImpl implements Primitives {""")

    genForPrimitives(f, """
    @Override
    public {p} {p}Op(@WebParam(name = "x") {p} x) {{
        return x;
    }}
    @Override
    public java.util.List<{b}> {p}Sequence(@WebParam(name = "xs") {p} xs) {{
        return java.util.Arrays.<{b}>asList({j}, {j}, {j});
    }}
""")

    gen(f, """
}""")



### PrimitivesSpec.scala (Scala project only) ###

with open(os.path.join(pluginTestDir, "simple-client-scala-future", "tests", "play", "soap", "sbtplugin", "tester", "PrimitivesSpec.scala"), 'w') as f:
  gen(f, """/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester

import java.util.Arrays

import play.soap.testservice.PrimitivesImpl
import play.soap.testservice.client._

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

/*
 * THIS FILE IS AUTO GENERATED. DO NOT EDIT THIS FILE MANUALLY.
 *
 * Run 'generate-primitives.py' to regenerate it.
 */

class PrimitivesSpec extends ServiceSpec {

  sequential
  "Primitives" should {
""")
  genForPrimitives(f, """
    "handle {p} ops" in withClient {{ client =>
      await(client.{p}Op({s})) must_== {s}
    }}

    "handle {p} sequences" in withClient {{ client =>
      await(client.{p}Sequence(Arrays.asList({s}, {s}))).asScala must_== List({s}, {s}, {s})
    }}""")

  gen(f, """
  }

  override type ServiceClient = PrimitivesService

  override type Service = Primitives

  override implicit val serviceClientClass: ClassTag[PrimitivesService] = ClassTag(classOf[PrimitivesService])

  override def getServiceFromClient(c: ServiceClient): Service = c.primitives

  override def createServiceImpl(): Any = new PrimitivesImpl

  val servicePath: String = "primitives"

}""")

### HelloWorldTest.java (Java project only) ###

with open(os.path.join(pluginTestDir, "simple-client-play-java-future", "tests", "play", "soap", "sbtplugin", "tester", "PrimitivesTest.java"), 'w') as f:
  gen(f, """/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.sbtplugin.tester;

import java.lang.RuntimeException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.*;
import org.apache.cxf.jaxws.EndpointImpl;
import org.junit.*;
import play.soap.testservice.client.*;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.concurrent.CompletionStage;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

/*
 * THIS FILE IS AUTO GENERATED. DO NOT EDIT THIS FILE MANUALLY.
 *
 * Run 'generate-primitives.py' to regenerate it.
 */

public class PrimitivesTest {

""")
  genForPrimitives(f, """
    @Test
    public void {p}Op() {{
        withClient(client ->
            assertEquals(({b}) {j}, await(client.{p}Op({j})))
        );
    }}
    @Test
    public void {p}Sequence() {{
        withClient(client ->
            assertEquals(Arrays.asList({j}, {j}, {j}), await(client.{p}Sequence(Arrays.asList({j}, {j}))))
        );
    }}""")

  gen(f, """
    private static <T> T await(CompletionStage<T> promise) {
        try {
            return promise.toCompletableFuture().get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void withClient(Consumer<Primitives> block) {
        withApp(app -> {
            Primitives client = app.injector().instanceOf(PrimitivesService.class).getPrimitives();
            block.accept(client);
        });
    }

    private static void withApp(Consumer<Application> block) {
        withService(port -> {
            GuiceApplicationBuilder builder = new GuiceApplicationBuilder()
                    .configure("play.soap.address", "http://localhost:"+port+"/primitives")
                    .configure("play.soap.debugLog", true);
            Application app = builder.build();
            running(app, () -> block.accept(app));
        });
    }

    private static void withService(Consumer<Integer> block) {
        final int port = findAvailablePort();
        final Endpoint endpoint = Endpoint.publish(
                "http://localhost:"+port+"/primitives",
                new play.soap.testservice.PrimitivesImpl());
        try {
            block.accept(port);
        } finally {
            endpoint.stop();
            // Need to shutdown whole engine.  Note, Jetty's shutdown doesn't seem to happen synchronously, have to wait
            // a few seconds for the port to be released. This is why we use a different port each time.
            ((EndpointImpl) endpoint).getBus().shutdown(true);
        }
    }

    private static int findAvailablePort() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            try {
                return socket.getLocalPort();
            } finally {
                socket.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}""")
