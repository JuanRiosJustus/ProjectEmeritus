package game.components.behaviors;

import game.components.Component;
import logging.Logger;
import logging.LoggerFactory;

public abstract class Behavior extends Component {

    protected final Logger logger = LoggerFactory.instance().logger(getClass());
}
