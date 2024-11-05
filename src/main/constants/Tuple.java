package main.constants;

public class Tuple<A, B, C> {
    public final A first;
    public final B second;
    public final C third;
    public Tuple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ", " + third.toString() + ")";
    }
}

