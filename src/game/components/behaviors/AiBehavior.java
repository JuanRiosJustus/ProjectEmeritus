package game.components.behaviors;

import java.util.SplittableRandom;

import game.components.SecondTimer;
import game.entity.Entity;

public class AiBehavior extends Behavior {
    // entities containing this class are ai
    public Entity lastTargetedUnit = null;
    public SecondTimer actionDelay = new SecondTimer();
    public SecondTimer slowlyStartTurn = new SecondTimer();
    public boolean investigated;
    public boolean actThenMove;

    private SplittableRandom random = new SplittableRandom();

    public void startTurn() {
        if (!investigated) {
            actThenMove = random.nextBoolean();
            investigated = true;
        }
    }
    public void reset() {
        investigated = false;
    }
}
