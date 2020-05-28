package powerdancer.serializable.internal;

import powerdancer.serializable.Sink;

import java.util.function.Consumer;
import java.util.function.Function;

public enum SinkPool {
    INSTANCE;

    public final Pool<Sink.ByteBufferSink> p = new Pool<Sink.ByteBufferSink>() {

        @Override
        protected Sink.ByteBufferSink newObject() {
            return new Sink.ByteBufferSink();
        }

        @Override
        protected void prepareObject(Sink.ByteBufferSink sink) {
            sink.buffer.clear();
        }
    };

    public static void with(Consumer<Sink.ByteBufferSink> c) {
        INSTANCE.p.with(c);
    }

    public static <R> R with(Function<Sink.ByteBufferSink, R> f) {
        return INSTANCE.p.with(f);
    }
}
