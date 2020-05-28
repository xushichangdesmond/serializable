package powerdancer.serializable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface Sink {
    Sink put(byte... b);

    default Sink put(char c) {
        return put((byte)c);
    }

    default Sink put(Serializable s) {
        s.writeInto(this);
        return this;
    }

    static Sink forOutputStream(OutputStream os) {
        return new Sink() {
            @Override
            public Sink put(byte... b) {
                try {
                    os.write(b);
                    return this;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    class ByteBufferSink implements Sink {
        public final ByteBuffer buffer;

        public ByteBufferSink() {
            this(1<<12);
        }

        public ByteBufferSink(int capacity) {
            buffer = ByteBuffer.allocate(capacity);
        }

        @Override
        public Sink put(byte... b) {
            buffer.put(b);
            return this;
        }
    }
}
