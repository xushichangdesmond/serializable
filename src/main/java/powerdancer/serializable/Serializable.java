package powerdancer.serializable;

import powerdancer.serializable.internal.SinkPool;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@FunctionalInterface
public interface Serializable {
    void writeInto(Sink s);

    default Serializable concat(Serializable other) {
        return s->{
            writeInto(s);
            other.writeInto(s);
        };
    }

    default String asString() {
        return SinkPool.<String>with(s->{
            writeInto(s);
            return s.toString();
        });
    }

    default String asHexString() {
        return SinkPool.<String>with(s->{
            writeInto(s);
            s.buffer.flip();
            StringBuilder b = new StringBuilder();
            while (s.buffer.remaining()>0) {
                b.append(String.format("%02X ", s.buffer.get()));
            }
            return b.toString();
        });
    }

    default String asHexEscapeString() {
        return SinkPool.<String>with(s->{
            writeInto(s);
            s.buffer.flip();
            StringBuilder b = new StringBuilder();
            while (s.buffer.remaining()>0) {
                b.append(String.format("\\x%02X", s.buffer.get()));
            }
            return b.toString();
        });
    }

    static Serializable forByte(byte b) {
        return s-> s.put(b);
    }

    static Serializable forChar(char c) {
        return s-> s.put(c);
    }

    static Serializable forBytes(byte... b) {
        return s-> s.put(b);
    }

    static Serializable forString(String s) {
        return Serializable.forBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    public static final int[] DEC = {
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         00, 01, 02, 03, 04, 05, 06, 07, 8, 9, -1, -1, -1, -1, -1, -1,
         -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
     };

    static Serializable forHexString(String digits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
         for (int i = 0; i < digits.length(); i += 2) {
                 char c1 = digits.charAt(i);
                 if ((i+1) >= digits.length())
                         throw new IllegalArgumentException("odd number of digits");
                 char c2 = digits.charAt(i + 1);
                 byte b = 0;
                 if ((c1 >= '0') && (c1 <= '9'))
                         b += ((c1 - '0') * 16);
                 else if ((c1 >= 'a') && (c1 <= 'f'))
                         b += ((c1 - 'a' + 10) * 16);
                 else if ((c1 >= 'A') && (c1 <= 'F'))
                         b += ((c1 - 'A' + 10) * 16);
                 else
                     throw new IllegalArgumentException("illegal char");
                 if ((c2 >= '0') && (c2 <= '9'))
                         b += (c2 - '0');
                 else if ((c2 >= 'a') && (c2 <= 'f'))
                         b += (c2 - 'a' + 10);
                 else if ((c2 >= 'A') && (c2 <= 'F'))
                         b += (c2 - 'A' + 10);
                 else
                     throw new IllegalArgumentException("illegal char");
                 baos.write(b);
             }
         return forBytes(baos.toByteArray());
    }

    static Serializable forIntAsString(int digits, int n) {
        String s = String.format("%0"+digits+"d", n);
        if (s.length() > digits) throw new IllegalArgumentException("longer than " + digits + " digits");
        return Serializable.forString(s);
    }

    static Serializable forIntAsString(int n) {
        return Serializable.forString(n + "");
    }

    static Serializable forBool(boolean b) {
        return Serializable.forChar(b ? '1' : '0');
    }

    static Serializable of(Serializable... children) {
        return sink-> Arrays.stream(children).forEach(c->c.writeInto(sink));
    }

    default byte[] asByteArray() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writeInto(Sink.forOutputStream(os));
        return os.toByteArray();
    }

    static Serializable littleEndian2ByteInt(int n) {
        return Serializable.forByte((byte)n)
                .concat(Serializable.forByte((byte)(n>>>8)));
    }
}
