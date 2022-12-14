/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.reflect.ClassTag

abstract class ServiceSpec(val launchClassName: String, val exposedPort: Int = 8080) extends AnyFlatSpec with ForAllTestContainer {

  private val IMAGE = "play/soap-test-server"

  private val TAG = "0.0.1"

  override val container: GenericContainer = GenericContainer(
    dockerImage = s"$IMAGE:$TAG",
    exposedPorts = Seq(exposedPort),
    command = Seq(launchClassName)
  )

  container.start()

  /**
   * The type of service.
   */
  type Service

  /**
   * The url of the service.
   */
  val serviceUrl: String

  /**
   * The runtime type of the service client.
   */
  implicit val serviceClass: ClassTag[Service]

  def await[T](future: Future[T]): T = Await.result[T](future, 10.seconds)

  def withClient[T](block: Service => T): T = {
    val factory = new PlayJaxWsProxyFactoryBean
    factory.setServiceClass(serviceClass.runtimeClass)
    factory.setAddress(serviceUrl)
    val client = factory.create.asInstanceOf[Service]
    block.apply(client)
  }
}
