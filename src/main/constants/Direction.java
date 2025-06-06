package main.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

public enum Direction {

    North(0, -1),
    NorthEast(1, -1),
    East(1, 0),
    SouthEast(1, 1),
    South(0, 1),
    SouthWest(-1, 1),
    West(-1, 0),
    NorthWest(-1, -1);

    public static final Direction[] cardinal = new Direction[] {
            North, East, South, West
    };

    public static final Direction[] ordinal = new Direction[] {
            NorthEast, SouthEast, SouthWest, SouthEast
    };

    public final int x;
    public final int y;

    Direction(int horizontal, int vertical) {
        x = horizontal;
        y = vertical;
    }

    private static final List<Direction> randomized = new ArrayList<>(List.of(Direction.values()));
    private static final SplittableRandom random = new SplittableRandom();

    public static List<Direction> random() {
        if (random.nextBoolean()) { Collections.shuffle(randomized); }
        return randomized;
    }
}
