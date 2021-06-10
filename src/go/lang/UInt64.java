package go.lang;

import java.util.Objects;

public class UInt64 extends GoObject {

    private long value;

    public UInt64() {
        value = 0;
    }

    public UInt64(int value) {
        this.value = value;
    }

    public UInt64(UInt64 other) {
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
        return Long.toUnsignedString(value);
    }

    @Override
    public UInt64 clone() {
        return new UInt64(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof UInt64) {
            return ((UInt64) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
