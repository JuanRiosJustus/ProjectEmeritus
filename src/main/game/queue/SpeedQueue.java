package main.game.queue;

import java.util.*;

import main.constants.Constants;
import main.game.components.MovementManager;
import main.game.components.Summary;
import main.game.components.Tags;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

public class SpeedQueue {

    private static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) ->
                entity2.get(Summary.class).getStatTotal(Constants.SPEED) -
                entity1.get(Summary.class).getStatTotal(Constants.SPEED);
    }

    private final PriorityQueue<Entity> mQueue = new PriorityQueue<>(turnOrdering());
    private final Queue<Entity> mFinished = new LinkedList<>();
    private final Map<String, List<Entity>> mTeamMap = new HashMap<>();
    private final Map<Entity, String> mIndividualMap = new HashMap<>();

    public Entity peek() { return mQueue.peek(); }
    public String toString() { return mQueue.toString(); }

    public boolean update() {
        boolean update = mQueue.isEmpty();
        if (update) { mQueue.addAll(mIndividualMap.keySet()); mFinished.clear(); }
        return update;
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(Summary.class).getStatCurrent(Constants.HEALTH) > 0) {
            return false;
        }
        mQueue.remove(toRemove);
        mFinished.remove(toRemove);
        String teamId = mIndividualMap.get(toRemove);
        if (mTeamMap.get(teamId).remove(toRemove)) {
            if (mTeamMap.get(teamId).isEmpty()) { mTeamMap.remove(teamId); }
        }
        mIndividualMap.remove(toRemove);
        toRemove.get(MovementManager.class).currentTile.get(Tile.class).removeUnit();
        return true;
    }

    public void dequeue() { mFinished.add(mQueue.poll()); }
    public void requeue(Entity entity) {
        mFinished.remove(entity);
        mQueue.add(entity);
    }

    public void enqueue(Entity entity, String teamName) {
        // Get team if exists
        List<Entity> team = mTeamMap.getOrDefault(teamName, new ArrayList<>());

        // Ensure the entity does not already exist in the team
        if (team.contains(entity)) { return; }

        // Add the entity
        team.add(entity);

        // re-register
        mIndividualMap.put(entity, teamName);
        mTeamMap.put(teamName, team);
    }

    public void enqueue(Entity[] entities, String teamId) {
        for (Entity entity : entities) { enqueue(entity, teamId); }
    }

    public void enqueue(Entity[] entities) {
        String teamId = UUID.randomUUID().toString();
        enqueue(entities, teamId);
    }

    public List<Entity> getAvailable() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mQueue);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public List<Entity> getFinished() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mFinished);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public List<Entity> getTeam(String teamName) { return mTeamMap.get(teamName); }
//    public boolean isSameTeam(Entity e1, Entity e2) {
//        if (!mIndividualMap.containsKey(e1)) { return false; }
//        if (!mIndividualMap.containsKey(e2)) { return  false; }
//        return mIndividualMap.get(e1).intValue() == mIndividualMap.get(e2).intValue();
//    }

    public int teams() { return mTeamMap.size(); }
//    public Set<Set<Entity>> getTeams() { return new HashSet<>(entityToTeamMap.values()); }
//    public Set<Entity> getIndividuals() { return new HashSet<>(individuals); }
//    public List<Entity> getFinished() { return new ArrayList<>(finished); }
//    public boolean shareSameTeam(Entity entity1, Entity entity2) {
//        return entityToTeamMap.get(entity1) == entityToTeamMap.get(entity2);
//    }
}
