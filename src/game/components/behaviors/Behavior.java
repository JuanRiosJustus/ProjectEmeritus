package game.components.behaviors;

import game.components.Component;
import logging.ELogger;
import logging.ELoggerFactory;

public abstract class Behavior extends Component {

    protected final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
}
