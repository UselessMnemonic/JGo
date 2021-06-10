package go.lang;

import java.util.Objects;

public class Int64 extends GoObject {

    private long value;

    public Int64() {
        value = 0;
    }

    public Int64(long value) {
        this.value = value;
    }

    public Int64(Int64 other) {
        this.value = other.value;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public Int64 clone() {
        return new Int64(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Int64) {
            return ((Int64) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}