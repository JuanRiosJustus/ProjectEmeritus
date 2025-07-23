package test;

import com.alibaba.fastjson2.JSONObject;
import javafx.embed.swing.JFXPanel;
import main.game.main.GameController;
import main.game.queue.InitiativeQueue;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InitiativeQueueTest extends GameTests {

    @Before
    public void initJavaFX() {
        // This initializes the JavaFX runtime
        new JFXPanel(); // implicitly calls Platform.startup(...) if not already done
    }




//    @Test
//    public void testPeekReturnsConsistentResultWithoutChange() {
//        // Setup
//        EntityStore.getInstance().clear();
//        createEntity("A", 10f);
//        createEntity("B", 20f);
//        createEntity("C", 15f);
//
//        InitiativeQueue queue = new InitiativeQueue();
//        queue.add("A");
//        queue.add("B");
//        queue.add("C");
//
//        // Test
//        String firstPeek = queue.peek();
//        String secondPeek = queue.peek();
//        String thirdPeek = queue.peek();
//
//        assertNotNull(firstPeek);
//        assertEquals(firstPeek, secondPeek, "Peek should be consistent across calls without mutation");
//        assertEquals(secondPeek, thirdPeek, "Peek should be consistent even with more calls");
//    }

    @Test
    public void testPeekChangesAfterTick() {

        // Setup
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue queue = new InitiativeQueue(1000f);

        String unit1 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 33); // same speed

        String unit2 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 66); // same speed

        String unit3 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit3, "speed", 99); // same speed

        queue.add(unit1);
        queue.add(unit2);
        queue.add(unit3);

        // Test
        for (int i = 0; i < 10000; i++) {
            String beforeTick = queue.peek();
            String afterTick = queue.peek();


            assertNotNull(beforeTick);
            assertNotNull(afterTick);
            assertTrue(Map.of(unit1, true, unit2, true, unit3, true).containsKey(afterTick));
            assertEquals(List.of(unit3, unit2, unit1), queue.getTurnOrder());
        }
    }


    @Test
    public void testQueueHasOrderDuringAdd() {

        // Setup
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue queue = new InitiativeQueue(1000f);

        String unit1 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 10); // same speed

        String unit2 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 20); // same speed

        String unit3 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit3, "speed", 30); // same speed

        queue.add(unit1);
        queue.add(unit2);
        queue.add(unit3);

        // Test

        System.out.println(queue.getTurnOrder());
    }

    @Test
    public void testQueueChangesAfterDeque() {

        // Setup
        GameController game = GameController.create();
        InitiativeQueue queue = new InitiativeQueue(1000f);

        String unit1 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 10);

        String unit2 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 20); // same speed

        String unit3 = game.createCpuUnit().getString("unit_id");
        setUnitAttributeToValue(game, unit3, "speed", 30); // same speed

        queue.add(unit1);
        queue.add(unit2);
        queue.add(unit3);

        Map<String, Integer> counts = new HashMap<>();
        Map<String, String> order = Map.of(unit1, " " + 1, unit2, " " + 2, unit3, " " + 3);
        // Test
        for (int i = 0; i < 10000; i++) {
            int beforeHash = queue.hashCode();
            String beforeTick = queue.peek();
            String unit = queue.dequeue();
            int afterHash = queue.hashCode();
            String afterTick = queue.peek();


            assertNotNull(beforeTick);
            assertNotNull(afterTick);
            assertNotEquals(beforeHash, afterHash);
            counts.put(unit, counts.getOrDefault(unit, 0) + 1);
        }
        System.out.println(String.valueOf(counts));
        System.out.println(String.valueOf(order));
    }

    @Test
    public void testPeekReturnsNullIfEmpty() {
        InitiativeQueue queue = new InitiativeQueue();
        assertNull(queue.peek(), "Peek should return null when queue is empty");
    }


    @Test
    public void testDequeueWithSimultaneousThreshold() {
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue queue = new InitiativeQueue(1000f);

        JSONObject unitA = game.createCpuUnit();
        String unit1 = unitA.getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 100); // same speed

        JSONObject unitB = game.createCpuUnit();
        String unit2 = unitB.getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 100); // same speed

        queue.add(unit1);
        queue.add(unit2);

        // Tick 10 times to get both to exactly 1000
        for (int i = 0; i < 10; i++) {
            queue.tick();
        }

        Map<String, Float> metersBefore = queue.getTurnMeters();

        // Now dequeue should randomly choose either unit
        String first = queue.dequeue();
        assertTrue(Set.of(unit1, unit2).contains(first), "Dequeue should return one of the two units");


        String second = queue.dequeue();
        assertTrue(Set.of(unit1, unit2).contains(second), "Dequeue should return the remaining unit");

        Map<String, Float> metersAfter = queue.getTurnMeters();
        float firstUnitMeter = metersAfter.get(first);
        assertEquals(firstUnitMeter, 0);

        // Tick 10 more times to get both to threshold again
        for (int i = 0; i < 10; i++) {
            queue.tick();
        }
    }

    @Test
    public void initiativeQueueGetsPopulatedCorrectly() {
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue initiativeQueue = new InitiativeQueue();

        // Setup unit1
        JSONObject response = game.createCpuUnit();
        String unit1 = response.getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 160);

        response = game.createCpuUnit();
        String unit2 = response.getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 120);


        initiativeQueue.add(unit1);
        initiativeQueue.add(unit2);

        List<String> queueOrder = initiativeQueue.getTurnOrder();
        assertEquals(List.of(unit1, unit2), queueOrder);
    }


    @Test
    public void fillMetersUntilNextTurnProcessesTurnsInCorrectOrder() {
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue initiativeQueue = new InitiativeQueue();

        // Create two units with different speeds
        JSONObject response1 = game.createCpuUnit();
        String unit1 = response1.getString("unit_id");
        setUnitAttributeToValue(game, unit1, "speed", 33);

        JSONObject response2 = game.createCpuUnit();
        String unit2 = response2.getString("unit_id");
        setUnitAttributeToValue(game, unit2, "speed", 100);

        initiativeQueue.add(unit1);
        initiativeQueue.add(unit2);

        // Simulate the first 5 turns
        List<String> turns = List.of(
                initiativeQueue.dequeue(),
                initiativeQueue.dequeue(),
                initiativeQueue.dequeue(),
                initiativeQueue.dequeue(),
                initiativeQueue.dequeue()
        );

        // With speeds of 160 and 120, we expect unit1 to act more frequently
        // Specifically, in 1200 units of time:
        // - unit1 gets 1 turn every 1000/160 = 6.25 ticks -> ~5 turns in 31.25 ticks
        // - unit2 gets 1 turn every 1000/120 = 8.33 ticks -> ~3 turns in 25 ticks
        // So unit1 should appear more often
        int countUnit1 = (int) turns.stream().filter(id -> id.equals(unit1)).count();
        int countUnit2 = (int) turns.stream().filter(id -> id.equals(unit2)).count();

        System.out.println("Turns: " + turns);
        assertEquals(1, countUnit1);
        assertEquals(4, countUnit2);
    }


    @Test
    public void testDequeueSingleUnitLoopsCorrectly() {
        GameController game = createGameWithDefaults(10, 10, true);
        InitiativeQueue queue = new InitiativeQueue(1000f);

        JSONObject unitObj = game.createCpuUnit();
        String unit = unitObj.getString("unit_id");
        setUnitAttributeToValue(game, unit, "speed", 250);

        queue.add(unit);

        for (int i = 0; i < 5; i++) {
            String next = queue.dequeue();
            assertEquals(unit, next, "Single unit should repeatedly get the turn");
        }
    }
}
