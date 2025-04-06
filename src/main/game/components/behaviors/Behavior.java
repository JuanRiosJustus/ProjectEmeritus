package main.game.components.behaviors;

import main.constants.EventTimingMapper;
import main.game.components.Component;
import main.game.components.SecondTimer;
import main.logging.EmeritusLogger;


public class Behavior extends Component {

    protected static final EmeritusLogger logger = EmeritusLogger.create(Behavior.class);
    private static final int ACTION_DELAY = 2;
    private final boolean mIsControlled;
    private SecondTimer mDelayTimer = new SecondTimer();
    private EventTimingMapper mEventTimingMapper = new EventTimingMapper();

    private boolean mShouldMoveFirst;
    public boolean hasFinishedSetup = false;
    public Behavior(boolean isControlled) { mIsControlled = isControlled; }
    public boolean isUserControlled() { return mIsControlled; }
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


    public boolean hasStartedMoving = false;
    public boolean hasFinishedMoving = false;
//    public void setHasStartedMoving(boolean b) { mHasStartedMoving = b; }
//    public boolean hasStartedMoving() { }
}
