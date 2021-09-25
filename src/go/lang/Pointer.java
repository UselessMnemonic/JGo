package go.lang;

import java.util.Objects;

/**
 * A pointer is a reference to a value. Unlike in C, no arithmetic is permitted. Pointer itself
 * is a value type--it should be cloned like all the other primitives when being passed around.
 * @param <T> The type of the value referenced
 */
public class Pointer<T extends GoObject> extends GoObject {

    // Reference value underlying this Pointer
    private T ref;

    /**
     * Creates a nil pointer for the given type.
     * @param referentType The type this pointer may point to
     */
    public Pointer(GoClass referentType) {
        super(GoClass.forPointer(referentType));
        ref = null;
    }

    /**
     * Creates a live pointer to the given object.
     * @param referent The addressable element
     */
    public Pointer(T referent) {
        super(GoClass.forPointer(referent.getGoClass()));
        ref = referent;
    }

    // Private copy constructor
    private Pointer(Pointer<T> mother) {
        super(mother.getGoClass());
        this.ref = mother.ref;
    }

    /**
     * Dereferences the pointer and retrieves the data. If the pointer is nil, this method
     * will throw an exception.
     * @return The data pointed to by this pointer
     * @throws NullPointerException if the pointer is nil.
     */
    public T get() throws NullPointerException {
        if (ref == null) throw new NullPointerException();
        return ref;
    }

    /**
     * Assigns the underlying reference to the given object
     * @param ref The object to which this Pointer will point
     */
    public void set(T ref) {
        this.ref = ref;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Pointer) {
            assign((Pointer<T>) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Makes this pointer point to the same data as the other pointer.
     * @param other A pointer
     */
    public void assign(Pointer<T> other) {
        if (other.getGoClass() != this.getGoClass()) {
            throw new IllegalArgumentException();
        }
        this.ref = other.ref;
    }

    @Override
    public Pointer<T> clone() {
        return new Pointer<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Pointer) {
            return equals((Pointer<T>) other);
        }
        return false;
    }

    /**
     * Compares two pointers for equality. Two pointers are equal if they point to the same
     * addressable element.
     * @param other The other pointer
     * @return true if both pointers point to the same location; false, otherwise
     */
    public boolean equals(Pointer<T> other) {
        return this.ref == other.ref;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ref);
    }

    @Override
    public String toString() {
        return String.format("0x%x", System.identityHashCode(ref));
    }
}