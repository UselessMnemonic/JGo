package go.lang;

import java.util.Objects;

public class UInt8 extends GoObject {

    private int value;

    public UInt8() {
        value = 0;
    }

    public UInt8(int value) {
        this.value = value & 0xFF;
    }

    public UInt8(UInt8 other) {
        this.value = other.value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value & 0xFF;
    }

    @Override
    public String toString() {
        return Integer.toUnsignedString(value);
    }

    @Override
    public UInt8 clone() {
        return new UInt8(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof UInt8) {
            return ((UInt8) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
