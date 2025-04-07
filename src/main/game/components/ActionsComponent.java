package main.game.components;

import main.constants.SecondTimer;
import main.logging.EmeritusLogger;


public class ActionsComponent extends Component {

    protected static final EmeritusLogger logger = EmeritusLogger.create(ActionsComponent.class);
    private static final int ACTION_DELAY = 2;
    private SecondTimer mDelayTimer = new SecondTimer();
    private boolean mShouldMoveFirst;
    public boolean hasFinishedSetup = false;
    public boolean shouldWait() { return shouldWait(ACTION_DELAY); }

    public boolean shouldWait(float seconds) {
        boolean shouldWait = mDelayTimer.elapsed() < seconds;
        if (!shouldWait) {
            mDelayTimer.reset();
        }
        return shouldWait;
    }

    public void setHasFinishedSetup(boolean b) { hasFinishedSetup = b; }
    public boolean hasFinishedSetup() { return hasFinishedSetup; }


    public boolean shouldMoveFirst() { return mShouldMoveFirst; }
    public void setShouldMoveFirst(boolean b) { mShouldMoveFirst = b; }


    private boolean mHasFinishedMoving = false;
    public void setHasFinishedMoving(boolean b) { mHasFinishedMoving = b; }
    public boolean hasFinishedMoving() { return mHasFinishedMoving; }

    private boolean mHasFinishedUsingAbility = false;
    public void setHasFinishedUsingAbility(boolean b) { mHasFinishedUsingAbility = b; }
    public boolean hasFinishedUsingAbility() { return mHasFinishedUsingAbility; }
    public void reset() {
        setHasFinishedUsingAbility(false);
        setHasFinishedMoving(false);
        setHasFinishedSetup(false);
    }
}
