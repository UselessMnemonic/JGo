package go.lang;

import java.util.Objects;

public class Int32 extends GoObject {

    private int value;

    public Int32() {
        value = 0;
    }

    public Int32(int value) {
        this.value = value;
    }

    public Int32(Int32 other) {
        this.value = other.value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Int32 clone() {
        return new Int32(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Int32) {
            return ((Int32) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}