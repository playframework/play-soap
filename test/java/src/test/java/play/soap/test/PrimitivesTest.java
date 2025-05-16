/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import play.soap.PlayJaxWsProxyFactoryBean;
import play.soap.test.primitives.Primitives;

@Testcontainers
@DisplayName("SOAP service that uses primitives should")
public class PrimitivesTest {
  private static final String MOCK_SERVER_IMAGE = "play/soap-test-server";
  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private static final int EXPOSED_PORT = 8080;

  @Container
  private static final GenericContainer<?> PRIMITIVES_SERVER =
      new GenericContainer<>(MOCK_SERVER_IMAGE)
          .withExposedPorts(EXPOSED_PORT)
          .withCreateContainerCmdModifier(
              it -> it.withEntrypoint("bin/primitives_primitives_server"));

  private static Primitives PRIMITIVES;

  @BeforeAll
  static void initClient() {
    assertThat(PRIMITIVES_SERVER.isRunning()).isTrue();
    PlayJaxWsProxyFactoryBean factory = new PlayJaxWsProxyFactoryBean();
    factory.setServiceClass(Primitives.class);
    factory.setAddress(
        format(
            "http://%s:%s/primitives",
            PRIMITIVES_SERVER.getHost(), PRIMITIVES_SERVER.getMappedPort(EXPOSED_PORT)));
    PRIMITIVES = (Primitives) factory.create();
  }

  @Test
  @DisplayName("work with boolean")
  public void testBoolean() {
    assertThat(PRIMITIVES.booleanOp(true)).succeedsWithin(TIMEOUT).isEqualTo(true);
    assertThat(PRIMITIVES.booleanOp(false)).succeedsWithin(TIMEOUT).isEqualTo(false);
  }

  @Test
  @DisplayName("work with boolean sequence")
  public void testBooleanSequence() {
    assertThat(PRIMITIVES.booleanSequence(List.of(true, false, true)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly(true, false, true);
    assertThat(PRIMITIVES.booleanSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with byte")
  public void testByte() {
    byte val = 1;
    assertThat(PRIMITIVES.byteOp(val)).succeedsWithin(TIMEOUT).isEqualTo(val);
  }

  @Test
  @DisplayName("work with byte sequence")
  public void testByteSequence() {
    List<Byte> seq = List.of((byte) 1, (byte) 2, (byte) 3);
    assertThat(PRIMITIVES.byteSequence(seq))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactlyElementsOf(seq);
    assertThat(PRIMITIVES.byteSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with double")
  public void testDouble() {
    assertThat(PRIMITIVES.doubleOp(1.0d)).succeedsWithin(TIMEOUT).isEqualTo(1.0d);
  }

  @Test
  @DisplayName("work with double sequence")
  public void testDoubleSequence() {
    assertThat(PRIMITIVES.doubleSequence(List.of(1.0d, 2.0d, 3.0d)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly(1.0d, 2.0d, 3.0d);
    assertThat(PRIMITIVES.doubleSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with float")
  public void testFloat() {
    assertThat(PRIMITIVES.floatOp(1.5f)).succeedsWithin(TIMEOUT).isEqualTo(1.5f);
  }

  @Test
  @DisplayName("work with float sequence")
  public void testFloatSequence() {
    assertThat(PRIMITIVES.floatSequence(List.of(1.5f, 2.5f, 3.5f)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly(1.5f, 2.5f, 3.5f);
    assertThat(PRIMITIVES.floatSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with int")
  public void testInt() {
    assertThat(PRIMITIVES.intOp(100)).succeedsWithin(TIMEOUT).isEqualTo(100);
  }

  @Test
  @DisplayName("work with int sequence")
  public void testIntSequence() {
    assertThat(PRIMITIVES.intSequence(List.of(100, 200, 300)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly(100, 200, 300);
    assertThat(PRIMITIVES.intSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with long")
  public void testLong() {
    assertThat(PRIMITIVES.longOp(1000L)).succeedsWithin(TIMEOUT).isEqualTo(1000L);
  }

  @Test
  @DisplayName("work with long sequence")
  public void testLongSequence() {
    assertThat(PRIMITIVES.longSequence(List.of(1000L, 2000L, 3000L)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly(1000L, 2000L, 3000L);
    assertThat(PRIMITIVES.longSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }

  @Test
  @DisplayName("work with short")
  public void testShort() {
    assertThat(PRIMITIVES.shortOp((short) 10)).succeedsWithin(TIMEOUT).isEqualTo((short) 10);
  }

  @Test
  @DisplayName("work with short sequence")
  public void testShortSequence() {
    assertThat(PRIMITIVES.shortSequence(List.of((short) 10, (short) 20, (short) 30)))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .containsExactly((short) 10, (short) 20, (short) 30);
    assertThat(PRIMITIVES.shortSequence(List.of()))
        .succeedsWithin(TIMEOUT)
        .asInstanceOf(LIST)
        .isEmpty();
  }
}
