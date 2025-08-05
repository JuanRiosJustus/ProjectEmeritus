package main.game.queue;

import java.util.*;

import com.alibaba.fastjson2.JSONArray;
import main.constants.Constants;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stores.EntityStore;
import main.logging.EmeritusLogger;

public class InitiativeQueue extends GameQueue {

    private static final EmeritusLogger mLogger = EmeritusLogger.create(SpeedQueue.class);
    private final Map<String, Float> mTurnMeterMap = new HashMap<>();
    private final Map<String, Entity> mEntityMap = new LinkedHashMap<>();
    private final List<String> mReadied = new ArrayList<>();
    private float mTurnThreshold = 1000f;
    private int mHashCode = -1;

    public InitiativeQueue() { this(1000f); }
    public InitiativeQueue(float threshold) { mTurnThreshold = threshold; }

    public void add(String entityID) {
        Entity entity = getEntityWithID(entityID);
        if (entity == null) { return; }

        mEntityMap.put(entityID, entity);

        float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
        mTurnMeterMap.put(entityID, speed);

        updateHashCode();
        invalidatePeekCache();
    }


    /**
     * Advances all turn meters by one tick.
     * Each entity’s turn meter increases by its speed value.
     */
    public void tick() {
        invalidatePeekCache();
        for (String id : mEntityMap.keySet()) {
            Entity entity = mEntityMap.get(id);
            float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
            float current = mTurnMeterMap.getOrDefault(id, 0f);
            mTurnMeterMap.put(id, current + speed);
        }
    }

    /**
     * Advances the simulation by ticking up to a safety max (e.g. 10,000 times)
     * until one unit reaches the TURN_THRESHOLD.
     */
    public String dequeue() {
        final int MAX_TICKS = 10_000;
        Random random = new Random();

        for (int i = 0; i < MAX_TICKS; i++) {
            // Check first if anyone is already ready
            List<String> ready = new ArrayList<>();
            for (String id : mEntityMap.keySet()) {
                float meter = mTurnMeterMap.getOrDefault(id, 0f);
                if (meter >= mTurnThreshold) {
                    ready.add(id);
                }
            }

            if (!ready.isEmpty()) {
                String selected = ready.get(random.nextInt(ready.size()));
                float current = mTurnMeterMap.get(selected);
                mTurnMeterMap.put(selected, current - mTurnThreshold);
                updateHashCode();
                invalidatePeekCache();
                return selected;
            }

            // Only tick if no one was ready
            tick();
        }

        return null;

//        throw new IllegalStateException("No entity reached the turn threshold within max ticks");
    }

    private List<String> mPeekCachedReadied = new LinkedList<>();
    private int mPeekCachedHash = -1;
    /**
     * Peeks the next unit that would reach the turn threshold without modifying internal state.
     * Simulates ticking until one or more units reach the threshold, then randomly selects one.
     */
    public String peek() {
        int currentHash = computeSimulationHash();

        if (currentHash == mPeekCachedHash && !mPeekCachedReadied.isEmpty()) {
            return mPeekCachedReadied.getFirst();
        }
        // Get Speeds
        final int MAX_TICKS = 10_000;
        Map<String, Float> simulatedMeters = new HashMap<>(mTurnMeterMap);
        Map<String, Float> speeds = new HashMap<>();

        // Setup speeds
        for (String id : mEntityMap.keySet()) {
            Entity entity = mEntityMap.get(id);
            float value = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
            speeds.put(id, value);
        }

        for (int tick = 0; tick < MAX_TICKS; tick++) {
            // Increase units meters by their speed
            for (String id : simulatedMeters.keySet()) {
                float current = simulatedMeters.getOrDefault(id, 0f);
                float speed = speeds.getOrDefault(id, 0f);
                simulatedMeters.put(id, current + speed);
            }
            // Check if any units ready
            List<String> ready = new ArrayList<>();
            for (Map.Entry<String, Float> entry : simulatedMeters.entrySet()) {
                if (entry.getValue() >= mTurnThreshold) {
                    ready.add(entry.getKey());
                }
            }
            // If units are ready, prepare them to be read
            if (!ready.isEmpty()) {
                ready.sort((a, b) -> Float.compare(simulatedMeters.get(b), simulatedMeters.get(a)));

                float topMeter = simulatedMeters.get(ready.get(0));
                List<String> topCandidates = new ArrayList<>();
                for (String id : ready) {
                    if (simulatedMeters.get(id) == topMeter) {
                        topCandidates.add(id);
                    } else {
                        break; // since list is sorted, stop when meter drops
                    }
                }

                Collections.shuffle(topCandidates); // random among tied
                String selected = topCandidates.getFirst();

                mPeekCachedReadied = topCandidates;
                mPeekCachedHash = currentHash;

                return selected;
            }
        }

        return null;
    }

    private void invalidatePeekCache() {
        mPeekCachedReadied.clear();
        mPeekCachedHash = -1;
    }

    private int computeSimulationHash() {
        int hash = Float.hashCode(mTurnThreshold);

//        for (String id : mEntityMap.keySet()) {
//            Entity entity = mEntityMap.get(id);
//            float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
//            float meter = mTurnMeterMap.getOrDefault(id, 0f);
//            hash = 31 * hash + id.hashCode();
//            hash = 31 * hash + Float.hashCode(meter);
//            hash = 31 * hash + Float.hashCode(speed);
//        }

//        return hash;
        return mTurnMeterMap.hashCode();
    }

    public int getTurnMeterMapHashCode() { return mTurnMeterMap.hashCode(); }

    public Map<String, Float> getTurnMeters() {
        return Collections.unmodifiableMap(mTurnMeterMap);
    }

    private Entity getEntityWithID(String id) {
        return EntityStore.getInstance().get(id);
    }

    private void updateReadiedUnits() {
        mReadied.clear();
        for (Map.Entry<String, Float> entry : mTurnMeterMap.entrySet()) {
            if (entry.getValue() >= mTurnThreshold) {
                mReadied.add(entry.getKey());
            }
        }
    }

    /**
     * Returns a list of unit IDs ordered by how quickly they will reach the turn threshold
     * assuming current turn meter values and constant speed.
     *
     * This does not simulate ticks — it computes time-to-threshold for all units.
     */
    public List<String> getTurnOrder() {
        return mEntityMap.keySet().stream()
                .sorted(Comparator.comparingDouble(id -> {
                    float currentMeter = mTurnMeterMap.getOrDefault(id, 0f);
                    Entity entity = mEntityMap.get(id);
                    float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);

                    if (speed <= 0) return Float.MAX_VALUE; // slower units go last

                    float remaining = mTurnThreshold - currentMeter;
                    return remaining / speed; // lower time = sooner turn
                }))
                .toList();
    }

    public JSONArray turnOrder() {
        return new JSONArray();
//        return new mEntityMap.keySet().stream()
//                .sorted(Comparator.comparingDouble(id -> {
//                    float currentMeter = mTurnMeterMap.getOrDefault(id, 0f);
//                    Entity entity = mEntityMap.get(id);
//                    float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
//
//                    if (speed <= 0) return Float.MAX_VALUE; // slower units go last
//
//                    float remaining = mTurnThreshold - currentMeter;
//                    return remaining / speed; // lower time = sooner turn
//                }))
//                .toList();
    }

    @Override
    public JSONArray nextTurnOrder() {
        return new JSONArray();
    }

    @Override
    public void remove(String id) {

    }

    public List<String> getPredictedTurnOrder(int count) {
        // Clone the state to simulate forward
        Map<String, Float> tempMeters = new HashMap<>(mTurnMeterMap);
        List<String> predictedOrder = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String next = null;
            float minTicksNeeded = Float.MAX_VALUE;

            // Find the unit that will reach threshold first
            for (String id : mEntityMap.keySet()) {
                Entity entity = mEntityMap.get(id);
                float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
                float current = tempMeters.get(id);
                float ticksNeeded = (mTurnThreshold - current) / speed;

                if (ticksNeeded < minTicksNeeded) {
                    minTicksNeeded = ticksNeeded;
                    next = id;
                }
            }

            if (next != null) {
                // Simulate advancement for all units
                for (String id : mEntityMap.keySet()) {
                    Entity entity = mEntityMap.get(id);
                    float speed = entity.get(StatisticsComponent.class).getTotal(Constants.SPEED);
                    float current = tempMeters.get(id);
                    float updated = current + speed * minTicksNeeded;
                    tempMeters.put(id, updated);
                }

                float overflow = tempMeters.get(next) - mTurnThreshold;
                tempMeters.put(next, overflow); // reset with overflow
                predictedOrder.add(next);
            }
        }

        return predictedOrder;
    }

    private void updateHashCode() {
        int result = Float.hashCode(mTurnThreshold);

        for (Map.Entry<String, Entity> entry : mEntityMap.entrySet()) {
            String id = entry.getKey();
            Float meter = mTurnMeterMap.getOrDefault(id, 0f);

//            result = 31 * result + id.hashCode();
            result = 31 * result + Float.hashCode(meter.intValue());
        }
        mHashCode = result;
//        mHashCode = mTurnMeterMap.hashCode();
    }
    @Override
    public int hashCode() {
        return mHashCode;
    }
}