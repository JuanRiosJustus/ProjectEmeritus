package main.constants;

public class Quadruple<A, B, C, D> {
    public final A first;
    public final B second;
    public final C third;
    public final D fourth;
    public Quadruple(A first, B second, C third, D fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ", " + third.toString() + ", " + fourth.toString() + ")";
    }
}
