package go.builtin;

import java.util.Objects;
import static java.lang.reflect.Array.newInstance;

/**
 * A slice is a dynamically-sized, flexible view into the elements of an array. In practice, slices
 * are much more common than arrays. Slices are passed by reference.
 * @param <T> The type of the elements in the slice
 */
public class Slice<T extends GoObject> extends GoObject {

    private T[] array;
    private int offset;
    private int length;
    private int capacity;

    /**
     * Creates a nil slice for the given element type.
     * @param elementType The type of this slice's elements
     */
    public Slice(GoClass elementType) {
        super(GoClass.forSlice(elementType));
        this.array = null;
        this.offset = 0;
        this.length = 0;
        this.capacity = 0;
    }

    Slice(GoClass elementType, int length, int capacity) {
        super(GoClass.forSlice(elementType));
        if (length < 0 || capacity < 0) {
            throw new IllegalArgumentException();
        }
        if (length > capacity) {
            throw new IllegalArgumentException();
        }

        this.array = (T[]) newInstance(elementType.javaClass, capacity);
        this.offset = 0;
        this.length = length;
        this.capacity = capacity;
        for (int i = 0; i < array.length; i++) {
            array[i] = (T) elementType.newDefaultValue();
        }
    }

    Slice(GoClass elementType, T[] elems, int offset, int length) {
        super(GoClass.forSlice(elementType));
        this.array = elems;
        this.offset = offset;
        this.length = length;
        this.capacity = elems.length - offset;
    }

    // Private copy constructor
    private Slice(Slice<T> mother) {
        super(mother.getGoClass());
        this.array = mother.array;
        this.offset = mother.offset;
        this.length = mother.length;
        this.capacity = mother.capacity;
    }

    public T get(int i) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException();
        }
        return array[offset + i];
    }

    public void set(int i, T elem) {
        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException();
        }
        array[offset + i].assign(elem);
    }

    public Slice<T> slice(int from, int to) {
        if (from < 0 || from >= length) {
            throw new IllegalArgumentException();
        }
        else if (to < 0 || to > length) {
            throw new IllegalArgumentException();
        }
        else if (to > from) {
            throw new IllegalArgumentException();
        }
        return new Slice<>(getGoClass().getElementType(), array, offset + from, to - from);
    }

    public Slice<T> append(T... elems) {
        int availableSpace = this.capacity - this.length;
        if (elems.length > availableSpace) {
            int newCapacity = this.length + elems.length;
            T[] newArray = (T[]) newInstance(getGoClass().getElementType().javaClass, newCapacity);
            int i;
            for (i = 0; i < this.length; i++) {
                newArray[i] = (T) this.array[this.offset + i].clone();
            }
            for (; i < elems.length; i++) {
                newArray[i] = (T) elems[i - this.length].clone();
            }
            return new Slice<>(getGoClass().getElementType(), this.array, 0, newCapacity);
        }
        else {
            for (int k = 0; k < elems.length; k++) {
                this.array[this.length + k].assign(elems[k]);
            }
            return new Slice<>(getGoClass().getElementType(), this.array, this.offset, this.length + elems.length);
        }
    }

    /**
     * Retrieves the length of this slice.
     * @return The length of this slice
     */
    public int length() {
        return length;
    }

    /**
     * Retrieves the capacity of this slice.
     * @return The capacity of this slice
     */
    public int capacity() {
        return capacity;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Map) {
            assign((Slice<T>) other);
        }
        throw new IllegalArgumentException();
    }

    public void assign(Slice<T> other) {
        if (other.getGoClass() != this.getGoClass()) {
            throw new IllegalArgumentException();
        }
        this.array = other.array;
        this.offset = other.offset;
        this.length = other.length;
        this.capacity = other.capacity;
    }

    @Override
    public Slice<T> clone() {
        return new Slice<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Slice) {
            return equals((Slice<T>) other);
        }
        return false;
    }

    /**
     * Tests two slices for equality. Two slices are equal if they reference the same array, and
     * have equal lengths.
     * @param other The other slice to compare
     * @return true if both slices are equal; false, otherwise
     */
    public boolean equals(Slice<T> other) {
        return this.array == other.array
            && this.offset == other.offset
            && this.length == other.length
            && this.capacity == other.capacity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(array, offset, length, capacity);
    }

    @Override
    public String toString() {
        if (length == 0) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < length - 1; i++) {
            sb.append(get(i)).append(' ');
        }
        sb.append(get(length - 1)).append(']');
        return sb.toString();
    }

    /**
     * Makes a new slice for the given element type, whose elements are initialized to their default
     * values.
     * @param elementType The GoClass of the intended elements
     * @param length The initial length of the slice
     * @param capacity The initial capacity of the slice
     */
    public static <T extends GoObject> Slice<T> make(GoClass elementType, int length, int capacity) {
        return new Slice<>(elementType, length, capacity);
    }
}
