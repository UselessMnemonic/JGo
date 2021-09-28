package go.builtin;

import java.util.Objects;

public class Int8 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Int8.class);

    /**
     * The byte value underlying this Int8
     */
    public byte value;

    /**
     * Constructs a default Int8, whose value is 0.
     */
    public Int8() {
        this((byte)0);
    }

    /**
     * Constructs an Int8 with the given value
     * @param value The byte value
     */
    public Int8(byte value) {
        super(Int8.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Int8(Int8 other) {
        super(Int8.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Int8) {
            assign((Int8) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Int8 the value of the other Int8
     * @param other The Int8 whose value will be copied
     */
    public void assign(Int8 other) {
        this.value = other.value;
    }

    /**
     * Assigns this Int8 the value of the given byte
     * @param value The byte value
     */
    public void assign(byte value) {
        this.value = value;
    }

    @Override
    public Int8 clone() {
        return new Int8(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Int8) {
            return equals((Int8) other);
        }
        return false;
    }

    /**
     * Compares the byte values of this and the other Int8
     * @param other The other Int8
     * @return True if both Int8s have the same value; False, otherwise
     */
    public boolean equals(Int8 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Byte.toString(value);
    }
}
