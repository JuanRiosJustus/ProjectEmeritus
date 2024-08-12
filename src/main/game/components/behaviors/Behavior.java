package main.game.components.behaviors;

import main.game.components.Component;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Behavior extends Component {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(Behavior.class);
    private boolean mIsControlled = false;
    public Behavior() { this(false); }
    public Behavior(boolean isControlled) { mIsControlled = isControlled; }
    public boolean isUserControlled() { return mIsControlled; }

}
