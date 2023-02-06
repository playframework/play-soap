/*
 * Copyright (C) Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.test.primitives;

import jakarta.jws.WebService;
import java.util.List;

@WebService(
    serviceName = "PrimitivesService",
    portName = "Primitives",
    targetNamespace = "http://testservice.soap.play/primitives",
    wsdlLocation = "wsdl/primitives.wsdl",
    endpointInterface = "play.soap.test.primitives.Primitives")
public class PrimitivesImpl implements Primitives {
  public byte byteOp(byte x) {
    return x;
  }

  public double doubleOp(double x) {
    return x;
  }

  public List<Integer> intSequence(List<Integer> xs) {
    return xs;
  }

  public boolean booleanOp(boolean x) {
    return x;
  }

  public List<Boolean> booleanSequence(List<Boolean> xs) {
    return xs;
  }

  public List<Byte> byteSequence(List<Byte> xs) {
    return xs;
  }

  public List<Long> longSequence(List<Long> xs) {
    return xs;
  }

  public List<Double> doubleSequence(List<Double> xs) {
    return xs;
  }

  public long longOp(long x) {
    return x;
  }

  public int intOp(int x) {
    return x;
  }

  public short shortOp(short x) {
    return x;
  }

  public List<Short> shortSequence(List<Short> xs) {
    return xs;
  }

  public List<Float> floatSequence(List<Float> xs) {
    return xs;
  }

  public float floatOp(float x) {
    return x;
  }
}
