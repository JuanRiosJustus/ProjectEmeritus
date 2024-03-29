package main.game.components.behaviors;

import main.game.components.Component;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public abstract class Behavior extends Component {

    protected static final ELogger logger = ELoggerFactory.getInstance().getELogger(Behavior.class);

    public abstract void reset();
}
