package designer.fundamentals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

public enum Direction {

    North(-1, 0),
    NorthEast(-1, 1),
    East(0, 1),
    SouthEast(1, 1),
    South(1, 0),
    SouthWest(1, -1),
    West(0, -1),
    NorthWest(-1, -1);

    public static final Direction[] cardinal = new Direction[] {
            North, East, South, West
    };

    public static final Direction[] ordinal = new Direction[] {
            NorthEast, SouthEast, SouthWest, SouthEast
    };

    public final int x;
    public final int y;

    Direction(int vertical, int horizontal) {
        x = vertical;
        y = horizontal;
    }

    private static final List<Direction> randomized = new ArrayList<>(List.of(Direction.values()));
    private static final SplittableRandom random = new SplittableRandom();

    public static List<Direction> random() {
        if (random.nextBoolean()) { Collections.shuffle(randomized); }
        return randomized;
    }

    public static List<Direction> getRandomized(Direction[] directions) {
        List<Direction> randomizedList = new ArrayList<>(List.of(directions));
        Collections.shuffle(randomizedList);
        return randomizedList;
    }
}
