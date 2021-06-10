package go.lang;

import java.util.Objects;

public class Int16 extends GoObject {

    private short value;

    public Int16() {
        value = 0;
    }

    public Int16(short value) {
        this.value = value;
    }

    public Int16(Int16 other) {
        this.value = other.value;
    }

    public short get() {
        return value;
    }

    public void set(short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Short.toString(value);
    }

    @Override
    public Int16 clone() {
        return new Int16(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Int16) {
            return ((Int16) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
