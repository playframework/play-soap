package play.soap;

import javax.jws.WebService;

@WebService(
    serviceName = "PrimitivesService",
    portName = "Primitives",
    targetNamespace = "http://testservice.soap.play/primitives",
    wsdlLocation = "wsdl/primitives.wsdl",
    endpointInterface = "play.soap.Primitives")

public class PrimitivesImpl implements Primitives {
    public byte byteOp(byte x) {
        return x;
    }

    public double doubleOp(double x) {
        return x;
    }

    public java.util.List<Integer> intSequence(java.util.List<Integer> xs) {
        return xs;
    }

    public boolean booleanOp(boolean x) {
        return x;
    }

    public java.util.List<Boolean> booleanSequence(java.util.List<Boolean> xs) {
        return xs;
    }

    public java.util.List<Byte> byteSequence(java.util.List<Byte> xs) {
        return xs;
    }

    public java.util.List<Long> longSequence(java.util.List<Long> xs) {
        return xs;
    }

    public java.util.List<Double> doubleSequence(java.util.List<Double> xs) {
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

    public java.util.List<Short> shortSequence(java.util.List<Short> xs) {
        return xs;
    }

    public java.util.List<Float> floatSequence(java.util.List<Float> xs) {
        return xs;
    }

    public float floatOp(float x) {
        return x;
    }
}
