package go.lang;

public abstract class GoObject implements Cloneable {
    public abstract String toString();
    public abstract GoObject clone();
    public abstract boolean equals(Object other);
    public abstract int hashCode();
}
