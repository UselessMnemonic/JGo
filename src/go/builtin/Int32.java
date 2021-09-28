package go.builtin;

import java.util.Objects;

public class Int32 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Int32.class);

    /**
     * The int value underlying this Int32
     */
    public int value;

    /**
     * Constructs a default Int32, whose value is 0.
     */
    public Int32() {
        this(0);
    }

    /**
     * Constructs an Int32 with the given value
     * @param value The int value
     */
    public Int32(int value) {
        super(Int32.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Int32(Int32 other) {
        super(Int32.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Int32) {
            assign((Int32) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Int32 the value of the other Int32
     * @param other The Int32 whose value will be copied
     */
    public void assign(Int32 other) {
        this.value = other.value;
    }

    @Override
    public Int32 clone() {
        return new Int32(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Int32) {
            return equals((Int32) other);
        }
        return false;
    }

    /**
     * Compares the int values of this and the other Int32
     * @param other The other Int32
     * @return True if both Int32s have the same value; False, otherwise
     */
    public boolean equals(Int32 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
