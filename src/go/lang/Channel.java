package go.lang;

import go.lang.internal.AsynchronousChannel;
import go.lang.internal.SynchronousChannel;

public abstract class Channel<T> extends GoObject {

    protected final Class<T> elementType;

    public Channel(Class<T> clazz) {
        this.elementType = clazz;
    }

    public Class<T> getElementType() {
        return elementType;
    }

    public abstract void send(T elem);

    public abstract T receive();

    public static <T> Channel<T> make(Class<T> clazz) {
        return new SynchronousChannel<>(clazz);
    }

    public static <T> Channel<T> make(Class<T> clazz, int capacity) {
        return new AsynchronousChannel<>(clazz, capacity);
    }
}

