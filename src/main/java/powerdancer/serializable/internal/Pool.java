package powerdancer.serializable.internal;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Pool<T> {

    final LinkedBlockingQueue<T> objects;

    public Pool(int capacity) {
        objects = new LinkedBlockingQueue<>(capacity);
    }

    public Pool() {
        this(Integer.MAX_VALUE);
    }

    T borrow() {
        T t = objects.poll();
        if (t == null) return newObject();
        prepareObject(t);
        return t;
    }

    void release(T t) {
        objects.offer(t);
    }

    protected abstract T newObject();
    protected void prepareObject(T t) {

    }

    public void with(Consumer<T> consumer) {
        T t = borrow();
        try {
            consumer.accept(t);
        }
        finally {
            release(t);
        }
    }

    public <R> R with(Function<T, R> f) {
        T t = borrow();
        try {
            return f.apply(t);
        }
        finally {
            release(t);
        }
    }
}