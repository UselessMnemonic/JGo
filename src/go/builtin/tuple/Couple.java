package go.builtin.tuple;

public class Couple<A, B> {
    public final A a;
    public final B b;
    public Couple(A a, B b) {
        this.a = a;
        this.b = b;
    }
    public static <A, B> Couple<A, B> of(A a, B b) {
        return new Couple<>(a, b);
    }
}
