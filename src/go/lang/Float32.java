package go.lang;

import java.util.Objects;

public class Float32 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Float32.class);

    /**
     * The float value underlying this Float32
     */
    public float value;

    /**
     * Constructs a default Float32, whose value is 0.
     */
    public Float32() {
        this(0);
    }

    /**
     * Constructs a Float32 with the given value
     * @param value The float value
     */
    public Float32(float value) {
        super(Float32.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Float32(Float32 other) {
        super(Float32.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Float32) {
            assign((Float32) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Float32 the value of the other Float32
     * @param other The Float32 whose value will be copied
     */
    public void assign(Float32 other) {
        this.value = other.value;
    }

    @Override
    public Float32 clone() {
        return new Float32(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Float32) {
            return equals((Float32) other);
        }
        return false;
    }

    /**
     * Compares the components of this and the other Float32
     * @param other The other Float32
     * @return True if both Float32 have the same value; False, otherwise
     */
    public boolean equals(Float32 other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}
