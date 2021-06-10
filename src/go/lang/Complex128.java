package go.lang;

import java.util.Objects;

public class Complex128 extends GoObject {

    private double real, imag;

    public Complex128() {
        real = 0;
        imag = 0;
    }

    public Complex128(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public Complex128(Complex128 other) {
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public String toString() {
        return String.format("(%s + %si)", real, imag);
    }

    @Override
    public Complex128 clone() {
        return new Complex128(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Complex128) {
            return this.real == ((Complex128) other).real
                && this.imag == ((Complex128) other).imag;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }
}
