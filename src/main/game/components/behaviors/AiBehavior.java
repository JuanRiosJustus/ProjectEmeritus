package main.game.components.behaviors;

import java.util.SplittableRandom;

import main.game.components.SecondTimer;
import main.game.entity.Entity;

public class AiBehavior extends Behavior {
    // entities containing this class are ai
    public Entity lastTargetedUnit = null;
    public SecondTimer actionDelay = new SecondTimer();
    public SecondTimer slowlyStartTurn = new SecondTimer();
    public boolean investigated;
    public boolean actThenMove;

    private final SplittableRandom random = new SplittableRandom();

    public void startTurn() {
        if (!investigated) {
            actThenMove = random.nextBoolean();
            investigated = true;
        }
    }
}
