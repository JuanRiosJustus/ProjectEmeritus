package game.queue;

import constants.Constants;
import game.components.MovementManager;
import game.components.Tile;
import game.components.statistics.Health;
import game.entity.Entity;
import game.components.statistics.Statistics;
import game.stats.node.ScalarNode;

import java.util.*;

public class SpeedQueue {

    private static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) -> {
            Statistics stats1 = entity1.get(Statistics.class);
            ScalarNode speed1 = stats1.getScalarNode(Constants.SPEED);
            Statistics stats2 = entity2.get(Statistics.class);
            ScalarNode speed2 = stats2.getScalarNode(Constants.SPEED);
            return speed2.getTotal() - speed1.getTotal();
        };
    }

    private final Map<Entity, Set<Entity>> grouping = new HashMap<>();
    private final PriorityQueue<Entity> queue = new PriorityQueue<>(turnOrdering());
    private final Set<Entity> participants = new HashSet<>();

    public void dequeue() { queue.poll(); }
    public Entity peek() { return queue.peek(); }
    public String toString() {
        return queue.toString();
    }
    public boolean update() {
        boolean updated = queue.isEmpty();
        if (updated) { queue.addAll(participants); }
        return updated;
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(Health.class).current > 0) { return false; }
        grouping.remove(toRemove);
        queue.remove(toRemove);
        participants.remove(toRemove);
        toRemove.get(MovementManager.class).currentTile.get(Tile.class).removeUnit();
        return true;
    }

    public void enqueue(Entity[] creatures) {
        for (Entity entity : creatures) {
            // Check/Ensure no duplicates
            if (grouping.containsKey(entity)) { return; }
            if (participants.contains(entity)) { return; }
            // Assign/link entity to its team
            grouping.put(entity, new HashSet<>(Arrays.asList(creatures)));
        }
        participants.addAll(Arrays.asList(creatures));
        queue.addAll(Arrays.asList(creatures));
    }

    public List<Entity> getOrdering() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(queue);
        List<Entity> ordering = new ArrayList<>();
        while(copy.size() > 0) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }
}
