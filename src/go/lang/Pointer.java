package go.lang;

import java.util.Objects;

/**
 * A pointer is a reference to a value. Unlike in C, no arithmetic is permitted. Pointer itself
 * is a value type--it should be cloned like all the other primitives when being passed around.
 * @param <T> The type of the value referenced
 */
public class Pointer<T extends GoObject> extends GoObject {

    private final Class<T> referentType;

    private T ref;

    public Pointer(Class<T> clazz) {
        this.referentType = clazz;
        this.ref = null;
    }

    public Pointer(Class<T> clazz, T ref) {
        this.referentType = clazz;
        this.ref = ref;
    }

    public T get() {
        return ref;
    }

    public void set(T ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return String.format("0x%x", System.identityHashCode(ref));
    }

    @Override
    public Pointer<T> clone() {
        return new Pointer<>(referentType, ref);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pointer) {
            return ((Pointer<?>) other).ref == this.ref;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}