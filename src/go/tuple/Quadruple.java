package go.tuple;

public class Quadruple<A, B, C, D> {
    public final A a;
    public final B b;
    public final C c;
    public final D d;
    Quadruple(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}