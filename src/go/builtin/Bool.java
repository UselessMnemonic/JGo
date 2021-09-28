package go.builtin;

import java.util.Objects;

public class Bool extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Bool.class);

    /**
     * The boolean value underlying this Bool
     */
    public boolean value;

    /**
     * Constructs a default Bool, whose value is false.
     */
    public Bool() {
        this(false);
        var x = Bool.class;
    }

    /**
     * Constructs a Bool with the given value
     * @param value The boolean value
     */
    public Bool(boolean value) {
        super(Bool.goClass);
        this.value = value;
    }

    // Private copy constructor
    private Bool(Bool other) {
        super(Bool.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Bool) {
            assign((Bool) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Bool the value of the other Bool
     * @param other The Bool whose value will be copied
     */
    public void assign(Bool other) {
        this.value = other.value;
    }

    @Override
    public Bool clone() {
        return new Bool(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Bool) {
            return equals((Bool) other);
        }
        return false;
    }

    /**
     * Compares the boolean values of this and the other Bool
     * @param other The other Bool
     * @return True if both Bools have the same value; False, otherwise
     */
    public boolean equals(Bool other) {
        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }
}
