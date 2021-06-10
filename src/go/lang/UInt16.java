package go.lang;

import java.util.Objects;

public class UInt16 extends GoObject {

    private int value;

    public UInt16() {
        value = 0;
    }

    public UInt16(int value) {
        this.value = value & 0xFFFF;
    }

    public UInt16(UInt16 other) {
        this.value = other.value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value & 0xFFFF;
    }

    @Override
    public String toString() {
        return Integer.toUnsignedString(value);
    }

    @Override
    public UInt16 clone() {
        return new UInt16(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof UInt16) {
            return ((UInt16) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
