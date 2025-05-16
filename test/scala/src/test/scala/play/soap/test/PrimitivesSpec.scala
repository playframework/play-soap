/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.test

import com.dimafeng.testcontainers.ForAllTestContainer
import com.dimafeng.testcontainers.GenericContainer
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.AsyncTimeLimitedTests
import org.scalatest.matchers.should.Matchers._
import org.scalatest.time.Span
import org.scalatest.time.SpanSugar._
import org.scalatest.wordspec.AsyncWordSpec
import play.soap.PlayJaxWsProxyFactoryBean
import play.soap.test.primitives.Primitives

import java.util.{ List => JList }

class PrimitivesSpec extends AsyncWordSpec with BeforeAndAfterAll with AsyncTimeLimitedTests with ForAllTestContainer {

  private val MOCK_SERVER_IMAGE = "play/soap-test-server"

  override def timeLimit: Span = 5.seconds

  override val container: GenericContainer = GenericContainer(
    dockerImage = s"$MOCK_SERVER_IMAGE",
    exposedPorts = Seq(8080)
  ).configure { container =>
    container.withCreateContainerCmdModifier(it => it.withEntrypoint("bin/primitives_primitives_server"))
  }
  container.start()

  private var client: Primitives = _

  protected override def beforeAll(): Unit = {
    container.container.isRunning shouldBe true
    val factory = new PlayJaxWsProxyFactoryBean
    factory.setServiceClass(classOf[Primitives])
    // noinspection HttpUrlsUsage
    factory.setAddress(s"http://${container.containerIpAddress}:${container.mappedPort(8080)}/primitives")
    client = factory.create.asInstanceOf[Primitives]
  }

  "SOAP service that uses primitives" should {

    "work with boolean" in {
      client.booleanOp(true).map { _ shouldBe true }
    }

    "work with boolean sequence" in {
      client.booleanSequence(JList.of(true, false, true)).map { seq =>
        seq should contain theSameElementsAs Seq(true, false, true)
      }
    }

    "work with empty boolean sequence" in {
      client.booleanSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with byte" in {
      client.byteOp(1).map { _ shouldBe 1 }
    }

    "work with byte sequence" in {
      client.byteSequence(JList.of(1.toByte, 2.toByte, 3.toByte)).map { seq =>
        seq should contain theSameElementsAs Seq(1, 2, 3)
      }
    }

    "work with empty byte sequence" in {
      client.byteSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with double" in {
      client.doubleOp(1.0d).map { _ shouldBe 1.0d }
    }

    "work with double sequence" in {
      client.doubleSequence(JList.of(1.0d, 2.0d, 3.0d)).map { seq =>
        seq should contain theSameElementsAs Seq(1.0d, 2.0d, 3.0d)
      }
    }

    "work with empty double sequence" in {
      client.doubleSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with float" in {
      client.floatOp(1.5f).map { _ shouldBe 1.5f }
    }

    "work with float sequence" in {
      client.floatSequence(JList.of(1.5f, 2.5f, 3.5f)).map { seq =>
        seq should contain theSameElementsAs Seq(1.5f, 2.5f, 3.5f)
      }
    }

    "work with empty float sequence" in {
      client.floatSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with int" in {
      client.intOp(100).map { _ shouldBe 100 }
    }

    "work with int sequence" in {
      client.intSequence(JList.of(100, 200, 300)).map { seq =>
        seq should contain theSameElementsAs Seq(100, 200, 300)
      }
    }

    "work with empty int sequence" in {
      client.intSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with long" in {
      client.longOp(1000).map { _ shouldBe 1000 }
    }

    "work with long sequence" in {
      client.longSequence(JList.of(1000L, 2000L, 3000L)).map { seq =>
        (seq should contain).theSameElementsInOrderAs(Seq(1000L, 2000L, 3000L))
      }
    }

    "work with empty long sequence" in {
      client.longSequence(JList.of()).map { _ shouldBe empty }
    }

    "work with short" in {
      client.shortOp(10).map { _ shouldBe 10 }
    }

    "work with short sequence" in {
      client.shortSequence(JList.of(10.toShort, 20.toShort, 30.toShort)).map { seq =>
        (seq should contain).theSameElementsInOrderAs(Seq(10, 20, 30))
      }
    }

    "work with empty short sequence" in {
      client.shortSequence(JList.of()).map { _ shouldBe empty }
    }
  }
}
