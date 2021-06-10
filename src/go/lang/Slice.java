package go.lang;

import java.util.Objects;

/**
 * An array has a fixed size. A slice, on the other hand, is a dynamically-sized, flexible view into
 * the elements of an array. In practice, slices are much more common than arrays.
 * @param <T> The type of the elements in the slice
 */
public class Slice<T extends GoObject> extends GoObject {
    private final Array<T> array;
    private final int offset;
    public final int length;
    public final int capacity;

    /**
     * Generates a slice from a Go Array
     * @param array The array containing data
     */
    public Slice(Array<T> array) {
        this(array, 0);
    }

    /**
     * Generates a slice from a Go Array
     * @param array The array containing data
     * @param offset The offset for the beginning of the slice
     */
    public Slice(Array<T> array, int offset) {
        this(array, offset, array.length - offset);
    }

    /**
     * Generates a slice from a Go array
     * @param array The array containing data
     * @param offset The offset for the beginning of the slice
     * @param length The desired length for the slice
     */
    public Slice(Array<T> array, int offset, int length) {
        this (array, offset, length, array.length - offset);
    }

    /**
     * Generates a slice from a Go array
     * @param array The array containing data
     * @param offset The offset for the beginning of the slice
     * @param length The desired length for the slice
     * @param capacity The desired capacity for the slice
     */
    public Slice(Array<T> array, int offset, int length, int capacity) {
        if (array == null)
            throw new IllegalArgumentException("array cannot be null");
        if (offset < 0 || offset >= array.length)
            throw new IllegalArgumentException("offset("+offset+") cannot exceed array bounds");
        if (length < 0 || length > capacity)
            throw new IllegalArgumentException("length("+length+") cannot exceed capacity("
                                               +capacity+")");
        this.array = array;
        this.offset = offset;
        this.length = length;
        this.capacity = array.length - offset;
    }

    public T get(int i) {
        if (i < 0 || i >= length)
            throw new IllegalArgumentException("index out of range ["+i+"] with length " + length);
        return array.get(i);
    }

    public Slice<T> slice(int from, int to) {
        if (from > to)
            throw new IllegalArgumentException("invalid slice index: "+from+" > "+to);
        if (from < 0 || to > capacity)
            throw new IllegalArgumentException("slice bounds out of range [:"+to+"] with capacity " + capacity);
        return new Slice<>(this.array, offset + from, to - from);
    }

    public Class<T> getElementType() {
        return array.getElementType();
    }

    public Slice<T> append(T... elems) {
        int newLength = array.length + elems.length;
        T[] result = (T[]) java.lang.reflect.Array.newInstance(getElementType(), newLength);
        for (int i = 0; i < array.length; i++) {
            result[i] = (T) array.get(i).clone();
        }
        for (int i = array.length; i < newLength; i++) {
            result[i] = elems[i];
        }
        return new Slice<>(new Array<>(getElementType(), result));
    }

    @Override
    public Slice<T> clone() {
        return new Slice<>(this.array, this.offset, this.length, this.capacity);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Slice)) return false;
        Slice<?> otherSlice = (Slice<?>) other;
        if (otherSlice.length != this.length) return false;
        if (otherSlice.capacity != this.capacity) return false;
        if (otherSlice.offset != this.offset) return false;
        for (int i = 0; i < this.length; i++) {
            if (!this.get(i).equals(otherSlice.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        Object[] elements = new Object[length];
        for (int i = 0; i < length; i++) elements[i] = get(i);
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        if (length == 0) return "[]";
        else if (length == 1) return "[" + get(0) + "]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < length - 1; i++) {
            sb.append(get(i));
            sb.append(' ');
        }
        sb.append(get(length - 1));

        sb.append(']');
        return sb.toString();
    }
}
