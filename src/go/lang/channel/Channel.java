package go.lang.channel;

import go.lang.GoObject;

public abstract class Channel<T extends GoObject> extends GoObject {

    protected final Class<T> elementType;
    protected ChannelState state;

    public Channel(Class<T> clazz) {
        this.elementType = clazz;
    }

    public Class<T> getElementType() {
        return elementType;
    }

    public abstract void send(T elem);

    public abstract T receive();

    public static <T extends GoObject> Channel<T> make(Class<T> clazz) {
        return new SynchronousChannel<>(clazz);
    }

    public static <T extends GoObject> Channel<T> make(Class<T> clazz, int capacity) {
        return new AsynchronousChannel<>(clazz, capacity);
    }
}

