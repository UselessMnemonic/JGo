package go.lang.internal;

import go.lang.Channel;
import go.lang.GoObject;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class AsynchronousChannel<T> extends Channel<T> {

    private final Queue<T> q;

    public AsynchronousChannel(Class<T> clazz, int capacity) {
        super(clazz);
        q = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void send(T elem) {
        q.add(elem);
    }

    @Override
    public T receive() {
        return q.poll();
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GoObject clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
