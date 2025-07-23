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

public class SpeedQueue extends GameQueue {

    private static final EmeritusLogger mLogger = EmeritusLogger.create(SpeedQueue.class);
    private static final Comparator<String> turnOrdering = new Comparator<String>() {
        @Override
        public int compare(String entityID1, String entityID2) {
            Entity entity1 = getEntityWithID(entityID1);
            Entity entity2 = getEntityWithID(entityID2);
            int speed1 = entity1.get(StatisticsComponent.class).getTotal(Constants.SPEED);
            int speed2 = entity2.get(StatisticsComponent.class).getTotal(Constants.SPEED);
            return speed2 - speed1;
        }
    };


    private int mHashCode = 0;
    private final PriorityQueue<String> mSpeedQueue = new PriorityQueue<>(turnOrdering);
    private final PriorityQueue<String> mSpeedQueue2 = new PriorityQueue<>(turnOrdering);
    private final PriorityQueue<String> mFinished = new PriorityQueue<>(turnOrdering);
    private final Map<String, Entity> mEntityMap = new LinkedHashMap<>();

    public String peek() { String entityID = mSpeedQueue.peek(); return entityID; }

    public String toString() { return mSpeedQueue.toString(); }

    public boolean refill() {
        if (mEntityMap.isEmpty()) { return false; }
        if (!mSpeedQueue.isEmpty()) { return false; }

        mSpeedQueue.addAll(mEntityMap.keySet());
        mHashCode = Objects.hashCode(mSpeedQueue.toString());
        mFinished.clear();
        mLogger.info("Speed queue updated.");
        return true;
    }

    public String dequeue() {
        String dequeued = mSpeedQueue.poll();
        mHashCode = Objects.hashCode(mSpeedQueue.toString());
        if (dequeued != null) {
            mFinished.add(dequeued);
        }
        return dequeued;
    }

    public void add(String entityID) {
        Entity entity = mEntityMap.get(entityID);
        if (entity != null) { return; }

        entity = getEntityWithID(entityID);
        if (entity == null) { return; }

        mEntityMap.put(entityID, entity);

        mHashCode = Objects.hashCode(mSpeedQueue.toString());
        mLogger.info("Added unit {} into queue", entityID);
    }

    public JSONArray turnOrder() {
        PriorityQueue<String> copy = new PriorityQueue<>(turnOrdering);
        copy.addAll(mSpeedQueue);
        JSONArray ordering = new JSONArray();
        while (!copy.isEmpty()) {
            String id = copy.poll();
            ordering.add(id);
        }
        return ordering;
    }

    public JSONArray nextTurnOrder() {
        PriorityQueue<String> copy = new PriorityQueue<>(turnOrdering);
        copy.addAll(mEntityMap.keySet());
        JSONArray ordering = new JSONArray();
        while (!copy.isEmpty()) {
            String id = copy.poll();
            ordering.add(id);
        }
        return ordering;
    }

    private static Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }

    public int hashCode() { return mHashCode; }

}



//package main.game.queue;
//
//import java.util.*;
//
//import main.constants.Constants;
//import main.constants.HashSlingingSlasher;
//import main.game.components.IdentityComponent;
//import main.game.components.statistics.StatisticsComponent;
//import main.game.entity.Entity;
//import main.game.stores.EntityStore;
//import main.logging.EmeritusLogger;
//import com.alibaba.fastjson2.JSONArray;
//
//public class SpeedQueue extends GameQueue {
//
//    private static final EmeritusLogger mLogger = EmeritusLogger.create(SpeedQueue.class);
//    public static Comparator<Entity> turnOrdering() {
//        return (entity1, entity2) ->
//                entity2.get(StatisticsComponent.class).getTotal(Constants.SPEED) -
//                        entity1.get(StatisticsComponent.class).getTotal(Constants.SPEED);
//    }
//    private final HashSlingingSlasher mQueuedEntitiesHashSlingingSlasher = new HashSlingingSlasher();
//    private final HashSlingingSlasher mFinishedEntitiesHashSlingingSlasher = new HashSlingingSlasher();
//    private final HashSlingingSlasher mAllEntitiesHashSlingingSlasher = new HashSlingingSlasher();
//    private final HashSlingingSlasher mCheckSum = new HashSlingingSlasher();
//    private final PriorityQueue<Entity> mSpeedQueue = new PriorityQueue<>(turnOrdering());
//    private final PriorityQueue<Entity> mFinished = new PriorityQueue<>(turnOrdering());
//
//    private final Map<String, List<Entity>> mTeamMap = new HashMap<>();
//    private final Map<String, Entity> mEntityMap = new LinkedHashMap<>();
//    private int mIterations = 0;
//
//    public String peek() {
//        Entity unitEntity = mSpeedQueue.peek();
//        if (unitEntity == null) { return null; }
//        IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
//        return identityComponent.getID();
//    }
//
//    public String toString() { return mSpeedQueue.toString(); }
//    public int getCycleCount() { return mIterations; }
//
//    public boolean refill() {
//        if (mEntityMap.isEmpty()) { return false; }
//        if (!mSpeedQueue.isEmpty()) { return false; }
//
//        mSpeedQueue.addAll(mEntityMap.values());
//        mFinished.clear();
//        mIterations++;
//        mLogger.info("Speed queue updated.");
//        return true;
//    }
//
//    public String dequeue() {
//        Entity dequeued = mSpeedQueue.poll();
//        mFinished.add(dequeued);
//        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mSpeedQueue.toString());
//        mFinishedEntitiesHashSlingingSlasher.setOnDifference(mFinished.toString());
//        mCheckSum.setOnDifference(mSpeedQueue.toString());
//        String id = null;
//        if (dequeued != null)  {
//            IdentityComponent identityComponent = dequeued.get(IdentityComponent.class);
//            id = identityComponent.getID();
//        }
//        return id;
//    }
//
//    public void add(String entityID) {
//        Entity entity = mEntityMap.get(entityID);
//        if (entity != null) { return; }
//
//        entity = getEntityWithID(entityID);
//        if (entity == null) { return; }
//
//        mEntityMap.put(entityID, entity);
//
//        mQueuedEntitiesHashSlingingSlasher.setOnDifference(mEntityMap.keySet().toString());
//        mCheckSum.setOnDifference(mEntityMap.keySet().toString());
//        mLogger.info("Added unit {} into queue", entityID);
//    }
//
//    public List<String> getAllEntitiesInTurnQueueWithPendingTurn() {
//        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
//        copy.addAll(mSpeedQueue);
//        List<String> ordering = new ArrayList<>();
//        while (!copy.isEmpty()) {
//            Entity entity = copy.poll();
//            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//            String id = identityComponent.getID();
//            ordering.add(id);
//        }
//        return ordering;
//    }
//
//    public List<String> getFinished() {
//        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
//        copy.addAll(mFinished);
//        List<String> ordering = new ArrayList<>();
//        while (!copy.isEmpty()) {
//            Entity entity = copy.poll();
//            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//            String id = identityComponent.getID();
//            ordering.add(id);
//        }
//        return Collections.unmodifiableList(ordering);
//    }
//
//    public List<String> getAllEntitiesInTurnQueue() {
//        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
//        copy.addAll(mEntityMap.values());
//        List<String> ordering = new ArrayList<>();
//        while (!copy.isEmpty()) {
//            Entity entity = copy.poll();
//            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//            String id = identityComponent.getID();
//            ordering.add(id);
//        }
//        return Collections.unmodifiableList(ordering);
//    }
//
//
//
//    public JSONArray getAllEntityIDsInTurnQueue() {
//        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
//        copy.addAll(mEntityMap.values());
//        JSONArray ordering = new JSONArray();
//        while (!copy.isEmpty()) {
//            Entity entity = copy.poll();
//            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//            String id = identityComponent.getID();
//            ordering.add(id);
//        }
//        return ordering;
//    }
//
//    public JSONArray getAllEntityIDsPendingTurnInTurnQueue() {
//        PriorityQueue<Entity> copy = new PriorityQueue<>(turnOrdering());
//        copy.addAll(mSpeedQueue);
//        JSONArray ordering = new JSONArray();
//        while (!copy.isEmpty()) {
//            Entity entity = copy.poll();
//            IdentityComponent identityComponent = entity.get(IdentityComponent.class);
//            String id = identityComponent.getID();
//            ordering.add(id);
//        }
//        return ordering;
//    }
//
//    private Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
//    public int getCheckSum() { return mCheckSum.get(); }
//
//    public List<String> order() { return new ArrayList<>(mEntityMap.keySet()); }
//    public Set<String> getAllUnitIDs() { return mEntityMap.keySet(); }
//}