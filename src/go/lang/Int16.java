package go.lang;

import java.util.Objects;

public class Int16 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Int16.class);

    /**
     * The short value underlying this Int16
     */
    public short value;

    /**
     * Constructs a default Int16, whose value is 0.
     */
    public Int16() {
        this((short) 0);
    }

    /**
     * Constructs an Int16 with the given value
     * @param value The short value
     */
    public Int16(short value) {
        super(Int16.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Int16(Int16 other) {
        super(Int16.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Int16) {
            assign((Int16) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Int16 the value of the other Int16
     * @param other The Int16 whose value will be copied
     */
    public void assign(Int16 other) {
        this.value = other.value;
    }

    @Override
    public Int16 clone() {
        return new Int16(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Int16) {
            return equals((Int16) other);
        }
        return false;
    }

    /**
     * Compares the short values of this and the other Int16
     * @param other The other Int16
     * @return True if both Int16s have the same value; False, otherwise
     */
    public boolean equals(Int16 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Short.toString(value);
    }
}
