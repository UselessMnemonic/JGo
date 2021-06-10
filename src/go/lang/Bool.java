package go.lang;

import java.util.Objects;

public class Bool extends GoObject {

    private boolean value;

    public Bool() {
        value = false;
    }

    public Bool(boolean value) {
        this.value = value;
    }

    public Bool(Bool other) {
        this.value = other.value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    public Bool clone() {
        return new Bool(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Bool) {
            return this.value == ((Bool) other).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
