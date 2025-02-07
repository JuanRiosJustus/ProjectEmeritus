package main.game.queue;

import java.util.*;

import main.constants.Constants;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;

public class SpeedQueue {

    public static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) ->
                entity2.get(StatisticsComponent.class).getTotal(Constants.SPEED) -
                entity1.get(StatisticsComponent.class).getTotal(Constants.SPEED);
    }

    private final PriorityQueue<Entity> mQueue = new PriorityQueue<>(turnOrdering());
    private final Queue<Entity> mFinished = new LinkedList<>();
    private final Map<String, List<Entity>> mTeamMap = new HashMap<>();
    private final Map<Entity, String> mIdentityMap = new HashMap<>();
    private int mIterations = 0;

    public Entity peek() { return mQueue.peek(); }
    public String peekV2() {
        Entity unitEntity = mQueue.peek();
        if (unitEntity == null) { return null; }
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        return identityComponent.getID();
    }

    public String toString() { return mQueue.toString(); }
    public int getCycleCount() { return mIterations; }

    public boolean update() {
        boolean update = mQueue.isEmpty();
        if (update) { mQueue.addAll(mIdentityMap.keySet()); mFinished.clear(); mIterations++; }
        return update;
    }

    public boolean removeIfNoCurrentHealth(Entity toRemove) {
        if (toRemove.get(StatisticsComponent.class).getCurrentHealth() > 0) {
            return false;
        }
        mQueue.remove(toRemove);
        mFinished.remove(toRemove);
        String teamId = mIdentityMap.get(toRemove);
        if (mTeamMap.get(teamId).remove(toRemove)) {
            if (mTeamMap.get(teamId).isEmpty()) { mTeamMap.remove(teamId); }
        }
        mIdentityMap.remove(toRemove);
//        toRemove.get(MovementComponent.class).mCurrentTile.get(Tile.class).removeUnit();
//        toRemove.get(MovementComponent.class).
        System.out.println("SHOULD KILLED UNIT");
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
        mIdentityMap.put(entity, teamName);
        mTeamMap.put(teamName, team);
    }

    public void enqueue(Entity[] entities, String teamId) {
        for (Entity entity : entities) { enqueue(entity, teamId); }
    }

    public boolean isOnSameTeam(Entity entity1, Entity entity2) {
        String teamOfEntity1 = mIdentityMap.get(entity1);
        String teamOfEntity2 = mIdentityMap.get(entity2);

        if (teamOfEntity1 == null || teamOfEntity2 == null) {
            return false;
        }

        return teamOfEntity1.equalsIgnoreCase(teamOfEntity2);
    }
    public List<Entity> getUnfinished() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mQueue);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return ordering;
    }

    public List<Entity> getFinished() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mFinished);
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public List<Entity> getAll() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mIdentityMap.keySet());
        List<Entity> ordering = new ArrayList<>();
        while(!copy.isEmpty()) { ordering.add(copy.poll()); }
        return Collections.unmodifiableList(ordering);
    }

    public int teams() { return mTeamMap.size(); }
}
