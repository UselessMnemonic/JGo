package go.lang;

import java.util.Objects;

public class Float32 extends GoObject {

    private float value;

    public Float32() {
        value = 0;
    }

    public Float32(float value) {
        this.value = value;
    }

    public Float32(Float32 other) {
        this.value = other.value;
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Float.toString(value); //TODO
    }

    @Override
    public Float32 clone() {
        return new Float32(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Float32) {
            return this.value == ((Float32) other).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
