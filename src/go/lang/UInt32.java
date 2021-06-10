package go.lang;

import java.util.Objects;

public class UInt32 extends GoObject {

    private int value;

    public UInt32() {
        value = 0;
    }

    public UInt32(int value) {
        this.value = value;
    }

    public UInt32(UInt32 other) {
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
        return Integer.toUnsignedString(value);
    }

    @Override
    public UInt32 clone() {
        return new UInt32(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof UInt32) {
            return ((UInt32) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
