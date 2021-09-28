package go.builtin;

/**
 * The basic API for all Go-derived objects.
 * All variable, or variable-like, or addressable, components in Go become Objects in java.
 *
 * <pre>
 * variable int32 = 0
 * array := [3]int32{0, 1, 2}
 * varPtr := *variable
 * elemPtr := *array[1]
 * </pre>
 *
 * ...become statements like these:
 *
 * <pre>
 * Int32 variable = new Int32(0)
 * Array<Int32> array = Array.ofInt32(0, 1, 2)
 * Pointer<Int32> varPtr = new Pointer<>(variable)
 * Pointer<Int32> elemPtr = new Pointer<>(array.get(1))
 * </pre>
 *
 * Think of instantiating GoObjects as allocating space on the stack for local variables, or on the
 * heap for calls to new() or make().
 */
public abstract class GoObject implements Cloneable {

    private final GoClass goClass;

    protected GoObject(GoClass goClass) {
        this.goClass = goClass;
    }

    /**
     * Assigns this object the value of the other object.
     * This minimizes object creation by allowing objects representing variables or indexed elements
     * to act as single addressable components.
     *
     * @param other The new value
     */
    public abstract void assign(GoObject other);

    /**
     * Creates a new, independent, clone of the current object. The clone is, in the Java sense, a
     * deep copy if this object is a value-type--types like Int32 or Structs.
     *
     * If this object is a reference type, like a Slice, then the clone is, in the Java sense,
     * a shallow copy.
     *
     * @return A copy of this object
     */
    public abstract GoObject clone();

    /**
     * Compares this object with another object for equality.
     * All GoObjects have some internal state, which is compared for equality. For example, two
     * Int32's are equal if their internal integers are equal.
     *
     * Reference types behave similarly to simple reference equality in Java (the == operator). Two
     * reference types are equal if their internal references are exactly the same.
     *
     * Not all types in Go have a defined equality operator; equality on those types might therefore
     * be useless.
     *
     * Always test two GoObjects using this method.
     *
     * @param other The other GoObject to compare
     * @return true if both objects are equal according to the rules above; false, otherwise
     */
    public abstract boolean equals(Object other);

    /**
     * Computes a hash code appropriate for Java APIs that preserves the same equality principles
     * as for {@link GoObject#clone()}. If two GoObjects are equal, their hash codes will be, too.
     *
     * @return A hash code representing this object
     */
    public abstract int hashCode();

    /**
     * Stringifies this object as it would be if it were formatted with the "%v" verb.
     *
     * @return A string representing this object.
     */
    public abstract String toString();

    /**
     * Returns the Go class for this object.
     *
     * @return A go class representing all type information about this object.
     */
    public GoClass getGoClass() {
        return goClass;
    }
}
