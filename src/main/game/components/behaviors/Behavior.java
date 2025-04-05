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
    private boolean mIsSetup = false;
    public Behavior(boolean isControlled) { mIsControlled = isControlled; }
    public boolean isUserControlled() { return mIsControlled; }
    public boolean shouldMoveFirst() { return mShouldMoveFirst; }
    public void setMoveFirst(boolean moveFirst) { mShouldMoveFirst = moveFirst; }
    public boolean shouldWait() { return shouldWait(ACTION_DELAY); }

    public boolean shouldWait(float seconds) {
        boolean shouldWait = mDelayTimer.elapsed() < seconds;
        if (!shouldWait) {
            mDelayTimer.reset();
        }
        return shouldWait;
    }

    public void setIsSetup(boolean b) { mIsSetup = b; }
    public boolean isSetup() { return mIsSetup; }
}
