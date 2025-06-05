package main.constants;

public class Quadruple<A, B, C, D> {
    private final A mFirst;
    private final B mSecond;
    private final C mThird;
    private final D mFourth;
    public Quadruple(A first, B second, C third, D fourth) {
        mFirst = first;
        mSecond = second;
        mThird = third;
        mFourth = fourth;
    }

    public A getFirst() { return mFirst; }
    public B getSecond() { return mSecond; }
    public C getThird() { return mThird; }
    public D getFourth() { return mFourth; }

    @Override
    public String toString() {
        return "(" + mFirst.toString() + ", " + mSecond.toString() + ", " + mThird.toString() + ", " + mFourth.toString() + ")";
    }
}
