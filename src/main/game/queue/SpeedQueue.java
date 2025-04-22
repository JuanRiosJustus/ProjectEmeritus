package main.game.queue;

import java.util.*;
import java.util.stream.Collectors;

import main.constants.Constants;
import main.constants.HashSlingingSlasher;
import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONArray;

public class SpeedQueue {

    private static final EmeritusLogger mLogger = EmeritusLogger.create(SpeedQueue.class);
    public static Comparator<Entity> turnOrdering() {
        return (entity1, entity2) ->
                entity2.get(StatisticsComponent.class).getTotal(Constants.SPEED) -
                entity1.get(StatisticsComponent.class).getTotal(Constants.SPEED);
    }
    private final HashSlingingSlasher mQueuedEntitiesHashSlingingSlasher = new HashSlingingSlasher();
    private final HashSlingingSlasher mFinishedEntitiesHashSlingingSlasher = new HashSlingingSlasher();
    private final HashSlingingSlasher mAllEntitiesHashSlingingSlasher = new HashSlingingSlasher();
    private final HashSlingingSlasher mCheckSum = new HashSlingingSlasher()
;    private final PriorityQueue<Entity> mQueued = new PriorityQueue<>(turnOrdering());
    private final PriorityQueue<Entity> mFinished = new PriorityQueue<>(turnOrdering());

    private final Map<String, List<Entity>> mTeamMap = new HashMap<>();
    private final Map<Entity, String> mIdentityMap = new HashMap<>();
    private int mIterations = 0;

    public String peek() {
        Entity unitEntity = mQueued.peek();
        if (unitEntity == null) { return null; }
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        return identityComponent.getID();
    }

    public String toString() { return mQueued.toString(); }
    public int getCycleCount() { return mIterations; }

    public boolean update() {
        boolean update = mQueued.isEmpty();
        if (update && !mIdentityMap.isEmpty()) {
            mQueued.addAll(mIdentityMap.keySet());
            mFinished.clear();
            mIterations++;
            mLogger.info("Speed queue updated.");
        }
        return update;
    }

//    public boolean removeIfNoCurrentHealth(Entity toRemove) {
//        if (toRemove.get(StatisticsComponent.class).getCurrentHealth() > 0) {
//            return false;
//        }
//        mQueue.remove(toRemove);
//        mFinished.remove(toRemove);
//        String teamId = mIdentityMap.get(toRemove);
//        if (mTeamMap.get(teamId).remove(toRemove)) {
//            if (mTeamMap.get(teamId).isEmpty()) { mTeamMap.remove(teamId); }
//        }
//        mIdentityMap.remove(toRemove);
////        toRemove.get(MovementComponent.class).mCurrentTile.get(Tile.class).removeUnit();
////        toRemove.get(MovementComponent.class).
////        System.out.println("SHOULD KILLED UNIT");
//        return true;
//    }

    public void dequeue() {
        Entity dequeued = mQueued.poll();
        mFinished.add(dequeued);
        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mQueued.toString());
        mFinishedEntitiesHashSlingingSlasher.setOnDifference(mFinished.toString());
        mCheckSum.setOnDifference(mQueued.toString());
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

        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mIdentityMap.keySet().toString());
        mCheckSum.setOnDifference(mIdentityMap.keySet().toString());
        mLogger.info("Added unit {}:{} into queue", teamName, entity);
    }

    public void enqueue(Entity[] entities, String teamId) {
        for (Entity entity : entities) { enqueue(entity, teamId); }
    }


//    public boolean isOnSameTeam(Entity entity1, Entity entity2) {
//        String teamOfEntity1 = mIdentityMap.get(entity1);
//        String teamOfEntity2 = mIdentityMap.get(entity2);
//
//        if (teamOfEntity1 == null || teamOfEntity2 == null) {
//            return false;
//        }
//
//        return teamOfEntity1.equalsIgnoreCase(teamOfEntity2);
//    }

    public List<String> getAllEntitiesInTurnQueueWithPendingTurn() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mQueued);
        List<String> ordering = new ArrayList<>();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return ordering;
    }

    public List<String> getFinished() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mFinished);
        List<String> ordering = new ArrayList<>();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return Collections.unmodifiableList(ordering);
    }

    public List<String> getAllEntitiesInTurnQueue() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mIdentityMap.keySet());
        List<String> ordering = new ArrayList<>();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return Collections.unmodifiableList(ordering);
    }



    public JSONArray getAllEntityIDsInTurnQueue() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mIdentityMap.keySet());
        JSONArray ordering = new JSONArray();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return ordering;
    }

    public JSONArray getAllEntityIDsPendingTurnInTurnQueue() {
        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
        copy.addAll(mQueued);
        JSONArray ordering = new JSONArray();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return ordering;
    }

    public int getCheckSum() { return mCheckSum.get(); }
    public int teams() { return mTeamMap.size(); }
    public int getAllEntitiesInTurnQueueWithPendingTurnChecksum() { return mQueuedEntitiesHashSlingingSlasher.get(); }
    public int getAllEntitiesInTurnQueueWithFinishedTurnChecksum() { return mFinishedEntitiesHashSlingingSlasher.get(); }
    public int getAllEntitiesInTurnQueueChecksum() { return mAllEntitiesHashSlingingSlasher.get(); }
    public List<String> getAllUnitIDs() {
        return mIdentityMap.keySet().stream().map(e -> {
            IdentityComponent identityComponent = e.get(IdentityComponent.class);
            return identityComponent.getID();
        }).collect(Collectors.toList());
    }
}
