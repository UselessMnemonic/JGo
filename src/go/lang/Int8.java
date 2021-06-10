package go.lang;

import java.util.Objects;

public class Int8 extends GoObject {

    private byte value;

    public Int8() {
        value = 0;
    }

    public Int8(byte value) {
        this.value = value;
    }

    public Int8(Int8 other) {
        this.value = other.value;
    }

    public byte get() {
        return value;
    }

    public void set(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Byte.toString(value);
    }

    @Override
    public Int8 clone() {
        return new Int8(this);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Int8) {
            return ((Int8) other).value == this.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
