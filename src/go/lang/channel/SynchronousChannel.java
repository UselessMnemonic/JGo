package go.lang.channel;

import go.lang.GoObject;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class SynchronousChannel<T> extends Channel<T> {

    private final LinkedList<AtomicReference<T>> receivers;
    private final Random random;

    public SynchronousChannel(Class<T> clazz) {
        super(clazz);
        receivers = new LinkedList<>();
        random = new Random();
    }

    @Override
    public void send(T elem) {
        AtomicReference<T> ref;
        synchronized (receivers) {
            while (receivers.isEmpty()) try {
                receivers.wait();
            } catch (InterruptedException ignore) {
            }
            ref = receivers.remove(random.nextInt(receivers.size()));
        }
        synchronized (ref) {
            ref.set(elem);
            ref.notify();
        }
    }

    @Override
    public T receive() {
        AtomicReference<T> ref = new AtomicReference<>();
        synchronized (receivers) {
            receivers.add(ref);
            receivers.notify();
        }
        synchronized (ref) {
            while (ref.get() == null) try {
                ref.wait();
            } catch (InterruptedException ignore) {
            }
        }
        return ref.get();
    }

    @Override
    public void assign(GoObject other) {

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

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
}
