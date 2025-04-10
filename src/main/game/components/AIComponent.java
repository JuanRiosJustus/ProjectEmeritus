package main.game.components;

public class AIComponent extends Component {
    private final boolean mIsAI;
    public AIComponent(boolean isAI) { mIsAI = isAI; }
    public boolean isAI() { return mIsAI; }
    public boolean isUserControlled() { return !mIsAI; }
}
