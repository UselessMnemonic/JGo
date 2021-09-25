package go.lang;

import java.util.Objects;

public class Complex128 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Complex128.class);

    /**
     * The double components of this Complex128
     */
    public double real, imag;

    /**
     * Constructs a default Complex128 whose value is 0 + 0i
     */
    public Complex128() {
        this(0, 0);
    }

    /**
     * Constructs a Complex128 with the given components
     * @param real The real component
     * @param imag The imaginary component
     */
    public Complex128(double real, double imag) {
        super(Complex128.goClass);
        this.real = real;
        this.imag = imag;
    }

    // Private copy constructor
    private Complex128(Complex128 other) {
        super(Complex128.goClass);
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Complex128) {
            assign((Complex128) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Complex128 the components of the other Complex128
     * @param other The Complex128 whose components will be copied
     */
    public void assign(Complex128 other) {
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public Complex128 clone() {
        return new Complex128(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Complex128) {
            return equals((Complex128) other);
        }
        return false;
    }

    /**
     * Compares the components of this and the other Complex128
     * @param other The other Complex128
     * @return True if both Complex128 have the same components; False, otherwise
     */
    public boolean equals(Complex128 other) {
        return this.real == other.real
            && this.imag == other.imag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }

    @Override
    public String toString() {
        return String.format("(%s + %si)", real, imag);
    }
}
