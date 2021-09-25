package go.lang;

import java.util.Objects;

public class Float64 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Float64.class);

    /**
     * The double value underlying this Float64
     */
    public double value;

    /**
     * Constructs a default Float64, whose value is 0.
     */
    public Float64() {
        this(0);
    }

    /**
     * Constructs a Float64 with the given value
     * @param value The double value
     */
    public Float64(double value) {
        super(Float64.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Float64(Float64 other) {
        super(Float64.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Float64) {
            assign((Float64) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Float64 the value of the other Float64
     * @param other The Float64 whose value will be copied
     */
    public void assign(Float64 other) {
        this.value = other.value;
    }

    @Override
    public Float64 clone() {
        return new Float64(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Float64) {
            return equals((Float64) other);
        }
        return false;
    }

    /**
     * Compares the components of this and the other Float64
     * @param other The other Float64
     * @return True if both Float64 have the same value; False, otherwise
     */
    public boolean equals(Float64 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
