/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Testcontainers
@TestInstance(PER_CLASS)
public class PrimitivesTest {
    private static final String IMAGE = "play/soap-test-server";
    private static final String TAG = "0.0.1";
    private static final String URL_TEMPLATE = "http://%s:%s/primitives";
    private static final int EXPOSED_PORT = 8080;
    private static final String MAIN_CLASS_NAME = "play.soap.Primitives_Primitives_Server";

    @Container
    private static final GenericContainer<?> underTest =
        new GenericContainer<>(DockerImageName.parse(IMAGE).withTag(TAG))
            .withCommand(MAIN_CLASS_NAME)
            .withExposedPorts(EXPOSED_PORT);

    @Test
    public void booleanOp() {
        assertEquals(true, await(withClient(client -> client.booleanOp(true))));
    }

    @Test
    public void booleanSequence() {
        assertEquals(asList(true, false, true), await(withClient(client -> client.booleanSequence(asList(true, false, true)))));
    }

    @Test
    public void byteOp() {
        assertEquals((java.lang.Byte) (byte) 1, await(withClient(client -> client.byteOp((byte) 1))));
    }

    @Test
    public void byteSequence() {
        assertEquals(asList((byte) 1, (byte) 2, (byte) 3), await(withClient(client -> client.byteSequence(Arrays.asList((byte) 1, (byte) 2, (byte) 3)))));
    }

    @Test
    public void doubleOp() {
        assertEquals((java.lang.Double) 1.0d, await(withClient(client -> client.doubleOp(1.0d))));
    }

    @Test
    public void doubleSequence() {
        assertEquals(asList(1.0d, 2.0d, 3.0d), await(withClient(client -> client.doubleSequence(asList(1.0d, 2.0d, 3.0d)))));
    }

    @Test
    @Order(7)
    public void floatOp() {
        assertEquals((java.lang.Float) 1.0f, await(withClient(client -> client.floatOp(1.0f))));
    }

    @Test
    public void floatSequence() {
        assertEquals(asList(1.0f, 2.0f, 3.0f), await(withClient(client -> client.floatSequence(asList(1.0f, 2.0f, 3.0f)))));
    }

    @Test
    @Order(9)
    public void intOp() {
        assertEquals((java.lang.Integer) 1, await(withClient(client -> client.intOp(1))));
    }

    @Test
    public void intSequence() {
        assertEquals(asList(1, 2, 3), await(withClient(client -> client.intSequence(asList(1, 2, 3)))));
    }

    @Test
    public void longOp() {
        assertEquals((java.lang.Long) 1L, await(withClient(client -> client.longOp(1L))));
    }

    @Test
    public void longSequence() {
        assertEquals(asList(1L, 2L, 3L), await(withClient(client -> client.longSequence(asList(1L, 2L, 3L)))));
    }

    @Test
    public void shortOp() {
        assertEquals((java.lang.Short) (short) 1, await(withClient(client -> client.shortOp((short) 1))));
    }

    @Test
    public void shortSequence() {
        assertEquals(asList((short) 1, (short) 2, (short) 3), await(withClient(client -> client.shortSequence(asList((short) 1, (short) 2, (short) 3)))));
    }

    private <T> T withClient(Function<Primitives, T> block) {
        PlayJaxWsProxyFactoryBean factory = new PlayJaxWsProxyFactoryBean();
        factory.setServiceClass(Primitives.class);
        factory.setAddress(format(URL_TEMPLATE, underTest.getHost(), underTest.getMappedPort(EXPOSED_PORT)));
        Primitives client = (Primitives) factory.create();
        return block.apply(client);
    }

    private <T> T await(CompletionStage<T> promise) {
        try {
            return promise.toCompletableFuture().get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
