package main.game.queue;

import java.util.*;

import main.constants.Constants;
import main.constants.HashSlingingSlasher;
import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stores.EntityStore;
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
    private final HashSlingingSlasher mCheckSum = new HashSlingingSlasher();
    private final PriorityQueue<Entity> mSpeedQueue = new PriorityQueue<>(turnOrdering());
    private final PriorityQueue<Entity> mFinished = new PriorityQueue<>(turnOrdering());

    private final Map<String, List<Entity>> mTeamMap = new HashMap<>();
    private final Map<String, Entity> mEntityMap = new LinkedHashMap<>();
    private int mIterations = 0;

    public String peek() {
        Entity unitEntity = mSpeedQueue.peek();
        if (unitEntity == null) { return null; }
        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
        return identityComponent.getID();
    }

    public String toString() { return mSpeedQueue.toString(); }
    public int getCycleCount() { return mIterations; }

    public boolean update() {
        if (mEntityMap.isEmpty()) { return false; }
        if (!mSpeedQueue.isEmpty()) { return false; }

        mSpeedQueue.addAll(mEntityMap.values());
        mFinished.clear();
        mIterations++;
        mLogger.info("Speed queue updated.");
        return true;
//        if (mSpeedQueue.isEmpty() && mEntityMap.isEmpty()) { return false; }
//        mSpeedQueue.addAll(mEntityMap.values());
//        mFinished.clear();
//        mIterations++;
//        mLogger.info("Speed queue updated.");
//        return true;
    }

    public void dequeue() {
        Entity dequeued = mSpeedQueue.poll();
        mFinished.add(dequeued);
        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mSpeedQueue.toString());
        mFinishedEntitiesHashSlingingSlasher.setOnDifference(mFinished.toString());
        mCheckSum.setOnDifference(mSpeedQueue.toString());
    }

    public void add(String entityID) {
        Entity entity = mEntityMap.get(entityID);
        if (entity != null) { return; }

        entity = getEntityWithID(entityID);
        if (entity == null) { return; }

        mEntityMap.put(entityID, entity);

        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mEntityMap.keySet().toString());
        mCheckSum.setOnDifference(mEntityMap.keySet().toString());
        mLogger.info("Added unit {} into queue", entityID);
    }

//    public void enqueue(Entity entity, String teamName) {
//        // Get team if exists
//        List<Entity> team = mTeamMap.getOrDefault(teamName, new ArrayList<>());
//
//        // Ensure the entity does not already exist in the team
//        if (team.contains(entity)) { return; }
//
//        // Add the entity
//        team.add(entity);
//
//        // re-register
//        mIdentityMap.put(entity, teamName);
//        mTeamMap.put(teamName, team);
//
//        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mIdentityMap.keySet().toString());
//        mCheckSum.setOnDifference(mIdentityMap.keySet().toString());
//        mLogger.info("Added unit {}:{} into queue", teamName, entity);
//    }



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
        copy.addAll(mSpeedQueue);
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
        copy.addAll(mEntityMap.values());
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
        copy.addAll(mEntityMap.values());
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
        copy.addAll(mSpeedQueue);
        JSONArray ordering = new JSONArray();
        while (!copy.isEmpty()) {
            Entity entity = copy.poll();
            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
            String id = identityComponent.getID();
            ordering.add(id);
        }
        return ordering;
    }

    private Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
    public int getCheckSum() { return mCheckSum.get(); }
    public int teams() { return mTeamMap.size(); }
    public int getAllEntitiesInTurnQueueWithPendingTurnChecksum() { return mQueuedEntitiesHashSlingingSlasher.get(); }
    public int getAllEntitiesInTurnQueueWithFinishedTurnChecksum() { return mFinishedEntitiesHashSlingingSlasher.get(); }
    public int getAllEntitiesInTurnQueueChecksum() { return mAllEntitiesHashSlingingSlasher.get(); }
    public Set<String> getAllUnitIDs() { return mEntityMap.keySet(); }
}
