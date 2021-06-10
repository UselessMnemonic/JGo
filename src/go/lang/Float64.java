package go.lang;

import java.util.Objects;

public class Float64 extends GoObject {

    private double value;

    public Float64() {
        value = 0;
    }

    public Float64(double value) {
        this.value = value;
    }

    public Float64(Float64 other) {
        this.value = other.value;
    }

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Couple.toString(value); //TODO
    }

    @Override
    public Float64 clone() {
        return new Float64(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Float64) {
            return this.value == ((Float64) other).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
