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

    private boolean mHasStartedMoving = false;
    public void setHasStartedMoving(boolean b) { mHasStartedMoving = b; }
    public boolean hasStartedMoving() { return mHasStartedMoving; }


    private static final String HAS_FINISHED_MOVING = "has_finished_moving";
    public void setHasFinishedMoving(boolean b) { put(HAS_FINISHED_MOVING, b); }
    public boolean hasFinishedMoving() { return getBooleanValue(HAS_FINISHED_MOVING, false); }


    private static final String HAS_STARTED_USING_ABILITY = "has_started_using_ability";
    public void setHasStartedUsingAbility(boolean val)  { put(HAS_STARTED_USING_ABILITY, val);  }
    public boolean hasStartedUsingAbility() { return getBooleanValue(HAS_STARTED_USING_ABILITY); }


    private static final String HAS_FINISHED_USING_ABILITY = "has_finished_using_ability";
    public void setHasFinishedUsingAbility(boolean b) { put(HAS_FINISHED_USING_ABILITY, b); }
    public boolean hasFinishedUsingAbility() { return getBooleanValue(HAS_FINISHED_USING_ABILITY, false); }



    public void reset() {
        setHasStartedUsingAbility(false);
        setHasFinishedUsingAbility(false);

        setHasStartedUsingAbility(false);
        setHasFinishedMoving(false);

        setHasFinishedSetup(false);
    }
}
