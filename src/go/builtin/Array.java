package go.builtin;

import java.util.Arrays;
import static java.lang.reflect.Array.newInstance;

/**
 * Arrays are fixed-length linear spans of memory in Go. An array is typed according to its element
 * type and the number of elements it holds. Arrays are passed by value.
 * @param <T> The type of element held by the Array
 */
public class Array<T extends GoObject> extends GoObject {

    private final T[] array;

    /**
     * Constructs a default array, which has all of its elements initialized to their default
     * values.
     * @param elementType The type of the elements in the array
     * @param n The number of elements in the array
     */
    public Array(GoClass elementType, int n) {
        super(GoClass.forArray(elementType, n));
        this.array = (T[]) newInstance(elementType.javaClass, n);
        for (int i = 0; i < array.length; i++) {
            array[i] = (T) elementType.newDefaultValue();
        }
    }

    // Private copy constructor
    private Array(Array<T> mother) {
        super(mother.getGoClass());
        this.array = (T[]) newInstance(this.getGoClass().getElementType().javaClass, mother.array.length);
        for (int i = 0; i < array.length; i++) {
            this.array[i] = (T) mother.array[i].clone();
        }
    }

    /**
     * Retrieves the element in the i-th index. The returned object is exactly the object in the
     * underlying array, so changes to the object will appear in the array.
     * @param i The index into the array
     * @return The value at the i-th array
     */
    public T get(int i) {
        return array[i];
    }

    /**
     * Assigns the given value into the i-th index.
     * This method is shorthand for <code>get(i).assign(value)</code>
     * @param i The index into the array
     * @param value The value to assign into the i-th array
     */
    public void set(int i, T value) {
        array[i].assign(value);
    }

    /**
     * Creates a slice using this array as its basis.
     * @param from The lower bound, inclusive
     * @param to The upper bound, exclusive
     * @return A new Slice
     */
    public Slice<T> slice(int from, int to) {
         return new Slice<>(getGoClass().getElementType(), this.array, from, to - from);
    }

    /**
     * Retrieves the length of this array.
     * @return The length of this array
     */
    public int length() {
        return array.length;
    }

    /**
     * Returns the underlying array.
     */
    public T[] getArray() {
        return array;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Array) {
            assign((Array<T>) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this array to the value of the other array. Each element in this array is assigned
     * the value of the corresponding element in the other array, like a deep copy.
     * @param other The other array
     */
    public void assign(Array<T> other) {
        if (other.getGoClass() != this.getGoClass()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < array.length; i++) {
            array[i].assign(other.array[i]);
        }
    }

    /**
     * Assigns this array with the values of the given array. This is shorthand for
     * <code>
     *     for (int i = 0; i < other.length; i++)
     *         this.set(i, other[i]);
     * </code>
     * @param other
     */
    public void assign(T[] other) {
        for (int i = 0; i < array.length; i++) {
            array[i].assign(other[i]);
        }
    }

    @Override
    public Array<T> clone() {
        return new Array<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Array) {
            return equals((Array<T>) other);
        }
        return false;
    }

    /**
     * Determines if this array is value-wise equal to the other array. That is, whether all
     * corresponding elements are value-wise equal.
     * @param other The other array
     * @return true if both arrays are equal; false, otherwise
     */
    public boolean equals(Array<T> other) {
        return Arrays.equals(other.array, this.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public String toString() {
        if (array.length == 0) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < array.length - 1; i++) {
            sb.append(array[i]).append(' ');
        }
        sb.append(array[array.length - 1]).append(']');
        return sb.toString();
    }
}
