package main.game.systems;

import main.constants.Checksum;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;

public abstract class GameSystem {
    protected final SplittableRandom random = new SplittableRandom();

    protected final Map<String, Checksum> mCheckSumMap = new LinkedHashMap<>();
//    public abstract void update(GameModel model, Entity unit);
    public abstract void update(GameModel model, String id);
    public Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }

    protected boolean isUpdated(String key, Object... values) {
        Checksum checkSum = mCheckSumMap.get(key);
        if (checkSum != null) {
            return checkSum.set(values);
        }

        checkSum = new Checksum();
        mCheckSumMap.put(key, checkSum);
        return checkSum.set(values);
    }
}
