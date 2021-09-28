package go.builtin.channel;

import go.builtin.Bool;
import go.builtin.GoClass;
import go.builtin.GoObject;
import go.builtin.tuple.Couple;

import java.util.Objects;

/**
 * "Do not communicate by sharing memory; instead, share memory by communicating."
 * The Go Channel, useful for sharing data between goroutines.
 *
 * @param <T> The type of element in the Channel
 */
public class Channel<T extends GoObject> extends GoObject {

    HChan hchan;

    /**
     * Creates a nil channel for the given element type
     *
     * @param elementType The type of element provided by the channel
     */
    public Channel(GoClass elementType) {
        super(GoClass.forChannel(elementType));
        this.hchan = null;
    }

    // private copy constructor
    private Channel(Channel<T> other) {
        super(other.getGoClass());
        this.hchan = other.hchan;
    }

    public void send(T elem) {
        HChan.chansend(hchan, elem, true);
    }

    public T receive() {
        return receive2().a;
    }

    public Couple<T, Bool> receive2() {
        Object[] result = new Object[1];
        HChan.chanrecv(hchan, result, true);
        if (result[0] == null) {
            return Couple.of((T) getGoClass().getElementType().newDefaultValue(), new Bool(false));
        }
        else {
            return Couple.of((T) result[0], new Bool(true));
        }
    }

    public void close() {
        HChan.closechan(hchan);
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Channel) {
            assign((Channel<T>) other);
        }
        throw new IllegalArgumentException();
    }

    public void assign(Channel<T> other) {
        if (other.getGoClass() != this.getGoClass()) {
            throw new IllegalArgumentException();
        }
        this.hchan = other.hchan;
    }

    @Override
    public Channel<T> clone() {
        return new Channel<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Channel) {
            return equals((Channel<T>) other);
        }
        return false;
    }

    public boolean equals(Channel<T> other) {
        return this.hchan == other.hchan;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hchan);
    }

    @Override
    public String toString() {
        return String.format("0x%x", System.identityHashCode(hchan));
    }

    public static <T extends GoObject> Channel<T> make(GoClass elementType) {
        Channel<T> result = new Channel<>(elementType);
        result.hchan = new HChan();
        return result;
    }

    public static <T extends GoObject> Channel<T> make(GoClass elementType, int size) {
        Channel<T> result = new Channel<>(elementType);
        result.hchan = new HChan(size);
        return result;
    }
}

