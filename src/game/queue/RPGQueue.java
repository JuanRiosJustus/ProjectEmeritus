package game.queue;

import constants.Constants;
import game.components.ActionManager;
import game.components.Tile;
import game.components.statistics.Health;
import game.entity.Entity;
import game.components.statistics.Statistics;
import game.stats.node.ScalarNode;

import java.util.*;

public class RPGQueue {

    private static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) -> {
            Statistics stats1 = entity1.get(Statistics.class);
            ScalarNode speed1 = stats1.getScalarNode(Constants.SPEED);
            Statistics stats2 = entity2.get(Statistics.class);
            ScalarNode speed2 = stats2.getScalarNode(Constants.SPEED);
            return speed2.getTotal() - speed1.getTotal();
        };
    }

    private final Map<Entity, Entity[]> grouping = new HashMap<>();
    private final PriorityQueue<Entity> queue = new PriorityQueue<>(turnOrdering());
    private final Set<Entity> entities = new HashSet<>();
//    private final Queue<Entity> garbage = new LinkedList<>();
    private final PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());

    public void dequeue() { queue.poll(); }
    public Entity peek() { return queue.peek(); }
    public String toString() {
        return queue.toString();
    }

    public void update() {
        if (queue.isEmpty()) { queue.addAll(entities); }

//        while(garbage.size() > 0) { removeIfNoCurrentHealth(garbage.poll()); }
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(Health.class).current > 0) { return false; }
        grouping.remove(toRemove);
        queue.remove(toRemove);
        entities.remove(toRemove);
        toRemove.get(ActionManager.class).tileOccupying.get(Tile.class).removeUnit();
        return true;
    }

    public void enqueue(Entity[] creatures) {
        for (Entity entity : creatures) {
            // Check/Ensure no duplicates
            if (grouping.containsKey(entity)) { return; }
            if (entities.contains(entity)) { return; }
            // Assign/link entity to its team
            grouping.put(entity, creatures);
        }
        entities.addAll(Arrays.asList(creatures));
        queue.addAll(Arrays.asList(creatures));
    }

    public List<Entity> getOrdering() {
        copy.clear();
        copy.addAll(queue);
        List<Entity> ordering = new ArrayList<>();
        while(copy.size() > 0) { ordering.add(copy.poll()); }
        return ordering;
    }
}
