package go.lang;

import java.util.Objects;

public class Complex64 extends GoObject {

    public static final GoClass goClass = GoClass.forBuiltin(Complex64.class);

    /**
     * The float components of this Complex64
     */
    public float real, imag;

    /**
     * Constructs a default Complex64 whose value is 0 + 0i
     */
    public Complex64() {
        this(0, 0);
    }

    /**
     * Constructs a Complex64 with the given components
     * @param real The real component
     * @param imag The imaginary component
     */
    public Complex64(float real, float imag) {
        super(Complex64.goClass);
        this.real = real;
        this.imag = imag;
    }

    // Private copy constructor
    private Complex64(Complex64 other) {
        super(Complex64.goClass);
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Complex64) {
            assign((Complex64) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this Complex64 the components of the other Complex64
     * @param other The Complex64 whose components will be copied
     */
    public void assign(Complex64 other) {
        this.real = other.real;
        this.imag = other.imag;
    }

    @Override
    public Complex64 clone() {
        return new Complex64(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Complex64) {
            return equals((Complex64) other);
        }
        return false;
    }

    /**
     * Compares the components of this and the other Complex64
     * @param other The other Complex64
     * @return True if both Complex64 have the same components; False, otherwise
     */
    public boolean equals(Complex64 other) {
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
