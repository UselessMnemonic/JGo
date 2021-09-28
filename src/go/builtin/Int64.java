package go.builtin;

import java.util.Objects;

public class Int64 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Int64.class);

    /**
     * The long value underlying this Int64
     */
    public long value;

    /**
     * Constructs a default Int64, whose value is 0.
     */
    public Int64() {
        this(0);
    }

    /**
     * Constructs an Int64 with the given value
     * @param value The long value
     */
    public Int64(long value) {
        super(Int64.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Int64(Int64 other) {
        super(Int64.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Int64) {
            assign((Int64) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Int64 the value of the other Int64
     * @param other The Int64 whose value will be copied
     */
    public void assign(Int64 other) {
        this.value = other.value;
    }

    @Override
    public Int64 clone() {
        return new Int64(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Int64) {
            return equals((Int64) other);
        }
        return false;
    }

    /**
     * Compares the long values of this and the other Int64
     * @param other The other Int64
     * @return True if both Int64s have the same value; False, otherwise
     */
    public boolean equals(Int64 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
