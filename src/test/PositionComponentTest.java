package test;

import main.game.components.PositionComponent;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionComponentTest {

    @Test
    public void testGetManhattanDistance() {
        PositionComponent a = new PositionComponent();
        PositionComponent b = new PositionComponent();

        // Case 1: Same position
        a.setPosition(3, 5);
        b.setPosition(3, 5);
        assertEquals(0, PositionComponent.getManhattanDistance(a, b), "Distance to self should be 0");

        // Case 2: One axis different
        a.setPosition(0, 0);
        b.setPosition(3, 0);
        assertEquals(3, PositionComponent.getManhattanDistance(a, b), "Horizontal distance of 3");

        // Case 3: Both axes different
        a.setPosition(1, 2);
        b.setPosition(4, 6);
        assertEquals(7, PositionComponent.getManhattanDistance(a, b), "Distance should be |4-1| + |6-2| = 3 + 4");

        // Case 4: Negative coordinates
        a.setPosition(-2, -3);
        b.setPosition(1, 2);
        assertEquals(8, PositionComponent.getManhattanDistance(a, b), "Distance with negative coordinates");
    }
}
