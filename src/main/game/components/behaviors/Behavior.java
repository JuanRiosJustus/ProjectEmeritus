package main.game.components.behaviors;

import main.constants.EventTimingMapper;
import main.game.components.Component;
import main.game.components.SecondTimer;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Behavior extends Component {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(Behavior.class);
    private static final int ACTION_DELAY = 2;
    private boolean mIsControlled = false;
    private SecondTimer mDelayTimer = new SecondTimer();
    private EventTimingMapper mEventTimingMapper = new EventTimingMapper();

    private boolean mShouldMoveFirst;
    private boolean mIsSetup = false;
    public Behavior() { this(false); }
    public Behavior(boolean isControlled) { mIsControlled = isControlled; }
    public boolean isUserControlled() { return mIsControlled; }
    public boolean shouldMoveFirst() { return mShouldMoveFirst; }
    public void setMoveFirst(boolean moveFirst) { mShouldMoveFirst = moveFirst; }
    public boolean shouldWait() {
        boolean shouldWait = mDelayTimer.elapsed() < ACTION_DELAY;
        if (!shouldWait) {
            mDelayTimer.reset();
        }
        return shouldWait;
    }

    public void setIsSetup(boolean b) { mIsSetup = b; }
    public boolean isSetup() { return mIsSetup; }
}
