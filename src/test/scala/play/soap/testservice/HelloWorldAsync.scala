package play.soap.testservice

import javax.jws.{WebParam, WebService}
import javax.xml.bind.annotation.adapters.{XmlAdapter, XmlJavaTypeAdapter}

import org.apache.cxf.interceptor.{LoggingOutInterceptor, LoggingInInterceptor}
import play.soap.PlayJaxWsProxyFactoryBean

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@WebService(name = "HelloWorld", serviceName = "HelloWorld")
trait HelloWorldAsync {
  def sayHello(@WebParam(name = "name") name: String): Future[String]
}

object HelloWorldAsyncClient extends App {
  val factory = new PlayJaxWsProxyFactoryBean
  factory.getInInterceptors.add(new LoggingInInterceptor)
  factory.getOutInterceptors.add(new LoggingOutInterceptor)
  factory.setServiceClass(classOf[HelloWorldAsync])
  factory.setAddress("http://localhost:9000/helloWorld")
  val client = factory.create.asInstanceOf[HelloWorldAsync]
  val reply = Await.result(client.sayHello("world"), Duration.Inf)

  System.out.println("Server said: " + reply)
  System.exit(0)
}