package go.builtin;

import java.util.Objects;

public class Interface<I extends GoInterface> extends GoObject {

    private GoObject dynamic;

    public Interface(Class<I> inf) {
        super(GoClass.forInterface(inf));
        dynamic = null;
    }

    // private copy constructor
    private Interface(Interface<I> other) {
        super(other);
        this.dynamic = other.dynamic.clone();
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Interface<?> otherif) {
            assign(otherif);
        }
        else if (other instanceof Pointer<?> otherp) {
            assign(otherp);
        }
        else if (other.getGoClass() == this.getGoClass().getElementType())
        throw new IllegalArgumentException("");
    }

    public void assign(Interface<I> other) {
        this.dynamic = other.dynamic;
    }

    public void assign(Pointer<?> other) {
        GoClass elemClass = other.getGoClass().getElementType();
        GoClass implClass = this.getGoClass().getElementType();
        if (elemClass == implClass) {
            this.dynamic = other.clone();
        }
        else {
            throw new IllegalArgumentException("");
        }
    }

    public void assign(I other) {
        dynamic = ((GoObject) other).clone();
    }

    public I getDynamic() {
        if (dynamic instanceof Pointer<?> p) {
            return (I) p.get();
        }
        else {
            return (I) dynamic;
        }
    }

    @Override
    public Interface<I> clone() {
        return new Interface<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Interface<?> otherif) {
            return equals(otherif);
        }
        throw new IllegalArgumentException("");
    }

    public boolean equals(Interface<I> other) {
        return (other.getGoClass() == this.getGoClass())
               && Objects.equals(this.dynamic, other.dynamic);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        if (dynamic == null) {
            return "<nil>";
        }
        else {
            return dynamic.toString();
        }
    }
}
