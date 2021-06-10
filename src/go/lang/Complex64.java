package go.lang;

import java.util.Objects;

public class Complex64 extends GoObject {

    private float real, imag;

    public Complex64() {
        real = 0;
        imag = 0;
    }

    public Complex64(float real, float imag) {
        this.real = real;
        this.imag = imag;
    }

    public Complex64(Complex64 other) {
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public String toString() {
        return String.format("(%s + %si)", real, imag);
    }

    @Override
    public Complex64 clone() {
        return new Complex64(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Complex64) {
            return this.real == ((Complex64) other).real
                && this.imag == ((Complex64) other).imag;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }
}
