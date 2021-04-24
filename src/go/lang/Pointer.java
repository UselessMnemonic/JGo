package go.lang;

/**
 * A pointer is a reference to a value. Unlike in C, no arithmetic is permitted.
 * @param <T> The type of the value referenced
 */
public abstract class Pointer<T extends GoObject> implements GoObject {

    public abstract T deref();

    public static <T extends GoObject> Pointer<T> of(T value) {
        return new PointerImpl<>(value);
    }

    public static <T extends GoObject> Pointer<T> of(Array<T> array, int i) {
        return new PointerInArray<>(array, i);
    }

    public static <T extends GoObject> Pointer<T> of(Slice<T> slice, int i) {
        return new PointerInSlice<>(slice, i);
    }

    public abstract Pointer<T> clone();
}