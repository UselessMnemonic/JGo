package go.lang.tuple;

public class NTuple {
    private final Object[] elements;
    public NTuple(Object... elems) {
        elements = elems;
    }
    public int size() {
        return elements.length;
    }
    public Object get(int index) {
        return elements[index];
    }
}
