package go.lang.tuple;

public class Triple<A, B, C> {
    public final A a;
    public final B b;
    public final C c;
    Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
