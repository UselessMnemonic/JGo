package go.lang;

public class Int32 implements GoObject {

    public int value;

    public Int32(int value) {
        this.value = value;
    }

    public static Int32[] arrayOf(int ...values) {
        Int32[] result = new Int32[values.length];
        for (int i = 0 ; i < values.length; i++) {
            result[i] = new Int32(values[i]);
        }
        return result;
    }

    @Override
    public Int32 clone() {
        return new Int32(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
