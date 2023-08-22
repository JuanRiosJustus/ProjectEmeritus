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
import main.game.components.Summary;
import main.game.components.Tile;
import main.game.entity.Entity;

public class SpeedQueue {

    private static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) -> {
            Summary stats1 = entity1.get(Summary.class);
            Summary stats2 = entity2.get(Summary.class);
            return stats2.getStatTotal(Constants.SPEED) - stats1.getStatTotal(Constants.SPEED);
        };
    }

    private final PriorityQueue<Entity> available = new PriorityQueue<>(turnOrdering());
    private final Queue<Entity> finished = new LinkedList<>();
    private final Set<Entity> individuals = new HashSet<>();
    private final Map<Entity, Set<Entity>> entityToTeamMap = new HashMap<>();

    public Entity peek() { return available.peek(); }
    public String toString() { return available.toString(); }

    public boolean update() {
        boolean update = available.isEmpty();
        if (update) { available.addAll(individuals); finished.clear(); }
        return update;
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(Summary.class).getStatCurrent(Constants.HEALTH) > 0) {
            return false;
        }
        available.remove(toRemove);
        finished.remove(toRemove);
        individuals.remove(toRemove);
        toRemove.get(MovementManager.class).currentTile.get(Tile.class).removeUnit();
        return true;
    }

    public void dequeue() { finished.add(available.poll()); }

    public void enqueue(Entity[] creatures) {
        Set<Entity> team = new HashSet<>(Arrays.asList(creatures));
        individuals.addAll(team);
        // teams.add(team);
        available.addAll(team);
        for (Entity entity : creatures) {
            entityToTeamMap.put(entity, team);
        }
    }

    public List<Entity> getAvailable() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(available);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public List<Entity> getFinished() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(finished);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public Set<Set<Entity>> getTeams() { return new HashSet<>(entityToTeamMap.values()); }
//    public Set<Entity> getIndividuals() { return new HashSet<>(individuals); }
//    public List<Entity> getFinished() { return new ArrayList<>(finished); }
    public boolean shareSameTeam(Entity entity1, Entity entity2) {
        return entityToTeamMap.get(entity1) == entityToTeamMap.get(entity2);
    }
}
