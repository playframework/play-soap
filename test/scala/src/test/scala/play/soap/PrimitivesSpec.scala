/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.util.Arrays.asList
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag
import java.lang.{Boolean => JBoolean}
import java.lang.{Byte => JByte}
import java.lang.{Double => JDouble}
import java.lang.{Float => JFloat}
import java.lang.{Integer => JInteger}
import java.util.{List => JList}
import java.lang.{Long => JLong}
import java.lang.{Short => JShort}

class PrimitivesSpec extends ServiceSpec(
  launchClassName = "play.soap.Primitives_Primitives_Server",
  exposedPort = 8080) {

  override type Service = Primitives

  override implicit val serviceClass: ClassTag[Primitives] = ClassTag(classOf[Primitives])

  override val serviceUrl: String = s"http://${container.containerIpAddress}:${container.mappedPort(8080)}/primitives"

  "Primitives" should "handle boolean ops" in {
    assertResult(true) {
      withClient { client => await[JBoolean](client.booleanOp(true)) }
    }
  }
  it should "handle boolean sequences" in {
    assertResult(List(true, true)) {
      withClient { client => await[JList[JBoolean]](client.booleanSequence(asList(true, true))).asScala }
    }
  }
  it should "handle byte ops" in {
    assertResult(1.toByte) {
      withClient { client => await[JByte](client.byteOp(1.toByte)) }
    }
  }
  it should "handle byte sequences" in {
    assertResult(List(1.toByte, 2.toByte)) {
      withClient { client => await[JList[JByte]](client.byteSequence(asList(1.toByte, 2.toByte))).asScala }
    }
  }
  it should "handle double ops" in {
    assertResult(1.0d) {
      withClient { client => await[JDouble](client.doubleOp(1.0d)) }
    }
  }
  it should "handle double sequences" in {
    assertResult(List(1.0d, 2.0d)) {
      withClient { client => await[JList[JDouble]](client.doubleSequence(asList(1.0d, 2.0d))).asScala }
    }
  }
  it should "handle float ops" in {
    assertResult(1.0f) {
      withClient { client => await[JFloat](client.floatOp(1.0f)) }
    }
  }
  it should "handle float sequences" in {
    assertResult(List(1.0f, 2.0f)) {
      withClient { client => await[JList[JFloat]](client.floatSequence(asList(1.0f, 2.0f))).asScala }
    }
  }
  it should "handle int ops" in {
    assertResult(1) {
      withClient { client => await[JInteger](client.intOp(1)) }
    }
  }
  it should "handle int sequences" in {
    assertResult(List(1, 2)) {
      withClient { client => await[JList[JInteger]](client.intSequence(asList(1, 2))).asScala }
    }
  }
  it should "handle long ops" in {
    assertResult(1L) {
      withClient { client => await[JLong](client.longOp(1L)) }
    }
  }
  it should "handle long sequences" in {
    assertResult(List(1L, 2L)) {
      withClient { client => await[JList[JLong]](client.longSequence(asList(1L, 2L))).asScala }
    }
  }
  it should "handle short ops" in {
    assertResult(1.toShort) {
      withClient { client => await[JShort](client.shortOp(1.toShort)) }
    }
  }
  it should "handle short sequences" in {
    assertResult(List(1.toShort, 2.toShort)) {
      withClient { client => await[JList[JShort]](client.shortSequence(asList(1.toShort, 2.toShort))).asScala }
    }
  }
}
