package go.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Array<T extends GoObject> extends GoObject {

    private final T[] arr;

    public final int length;

    private final Class<T> elementType;

    public Array(Class<T> clazz, T... values) {
        this.elementType = clazz;
        this.arr = values;
        this.length = this.arr.length;
    }

    public Array(Class<T> clazz, int n) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        elementType = clazz;
        arr = (T[]) java.lang.reflect.Array.newInstance(elementType, n);
        this.length = arr.length;

        Constructor<T> constructor = elementType.getDeclaredConstructor();
        for(int i = 0; i < n; i++) arr[i] = constructor.newInstance();
    }

    public Class<T> getElementType() {
        return elementType;
    }

    public T get(int i) {
        return arr[i];
    }

    public Slice<T> slice(int low, int high) {
        return new Slice<T>(this, low, high - low);
    }

    @Override
    public String toString() {
        if (arr.length == 0) return "[]";
        else if (arr.length == 1) return "[" + get(0) + "]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < arr.length - 1; i++) {
            sb.append(get(i));
            sb.append(' ');
        }
        sb.append(get(arr.length - 1));

        sb.append(']');
        return sb.toString();
    }

    @Override
    public Array<T> clone() {
        T[] result = Arrays.copyOf(arr, arr.length);
        for (int i = 0; i < result.length; i++) result[i] = (T) result[i].clone();
        return new Array<T>(elementType, result);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Array)) return false;
        Array<?> otherArray = (Array<?>) other;
        if (otherArray.length != this.length) return false;
        for (int i = 0; i < this.length; i++) {
            if (!this.get(i).equals(otherArray.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(arr);
    }
}
