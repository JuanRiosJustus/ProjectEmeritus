package game.components.behaviors;

import game.components.SecondTimer;
import game.entity.Entity;

public class AiBehavior extends Behavior {
    // entities containing this class are ai
    public Entity lastTargetedUnit = null;
    public SecondTimer actionDelay = new SecondTimer();
    public SecondTimer slowlyStartTurn = new SecondTimer();
    public boolean investigated;
}
