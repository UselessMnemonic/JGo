package go.builtin;

import java.util.Objects;
import java.util.stream.IntStream;

public class MutableString extends GoObject implements CharSequence {

    public static final GoClass goClass = GoClass.forBuiltin(MutableString.class);

    // The underlying data
    private String value;

    /**
     * Constructs a default MutableString, whose value is the empty String.
     */
    public MutableString() {
        this("");
    }

    /**
     * Constructs an MutableString with the given value
     * @param value The String value
     */
    public MutableString(CharSequence value) {
        super(MutableString.goClass);
        this.value = value.toString();
    }

    // Private copy constructor
    private MutableString(MutableString other) {
        super(MutableString.goClass);
        this.value = other.value;
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof MutableString) {
            assign((MutableString) other);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Assigns this MutableString the value of the other MutableString
     * @param other The MutableString whose value will be copied
     */
    public void assign(MutableString other) {
        this.value = other.value;
    }

    @Override
    public MutableString clone() {
        return new MutableString(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MutableString) {
            return equals((MutableString) other);
        }
        return false;
    }

    /**
     * Compares the String values of this and the other MutableString
     * @param other The other MutableString
     * @return True if both MutableStrings have the same value; False, otherwise
     */
    public boolean equals(MutableString other) {
        return value.toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public IntStream chars() {
        return value.chars();
    }

    @Override
    public IntStream codePoints() {
        return value.codePoints();
    }
}
