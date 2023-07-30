package main.game.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import main.constants.Constants;
import main.game.components.MovementManager;
import main.game.components.Statistics;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.stats.node.StatsNode;

public class SpeedQueue {

    private static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) -> {
            Statistics stats1 = entity1.get(Statistics.class);
            StatsNode speed1 = stats1.getStatsNode(Constants.SPEED);
            Statistics stats2 = entity2.get(Statistics.class);
            StatsNode speed2 = stats2.getStatsNode(Constants.SPEED);
            return speed2.getTotal() - speed1.getTotal();
        };
    }

    private final PriorityQueue<Entity> availableTurnQueue = new PriorityQueue<>(turnOrdering());
    private final Queue<Entity> finishedTurnQueue = new LinkedList<>();
    private final Set<Entity> individuals = new HashSet<>();
    // private final Set<Set<Entity>> teams = new HashSet<>();
    private final Map<Entity, Set<Entity>> entityToTeamMap = new HashMap<>();

    public Entity peek() { return availableTurnQueue.peek(); }
    public String toString() { return availableTurnQueue.toString(); }

    public boolean update() {
        boolean update = availableTurnQueue.isEmpty();
        if (update) { availableTurnQueue.addAll(individuals); finishedTurnQueue.clear(); }
        return update;
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(Statistics.class).getResourceNode(Constants.HEALTH).current > 0) {
            return false;
        }
        availableTurnQueue.remove(toRemove);
        finishedTurnQueue.remove(toRemove);
        individuals.remove(toRemove);
        toRemove.get(MovementManager.class).currentTile.get(Tile.class).removeUnit();
        return true;
    }

    public void dequeue() { finishedTurnQueue.add(availableTurnQueue.poll()); }

    public void enqueue(Entity[] creatures) {
        Set<Entity> team = new HashSet<>(Arrays.asList(creatures));
        individuals.addAll(team);
        // teams.add(team);
        availableTurnQueue.addAll(team);
        for (Entity entity : creatures) {
            entityToTeamMap.put(entity, team);
        }
    }

    public List<Entity> getAvailableTurnQueue() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(availableTurnQueue);
        List<Entity> ordering = new ArrayList<>();
        while(copy.size() > 0) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public Set<Set<Entity>> getTeams() { return new HashSet<>(entityToTeamMap.values()); }
    public Set<Entity> getIndividuals() { return new HashSet<>(individuals); }
    public List<Entity> getFinishedTurnQueue() { return new ArrayList<>(finishedTurnQueue); }
    public boolean shareSameTeam(Entity entity1, Entity entity2) {
        return entityToTeamMap.get(entity1) == entityToTeamMap.get(entity2);
    }
}
