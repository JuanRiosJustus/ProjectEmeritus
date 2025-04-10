package main.game.systems;

import main.constants.HashSlingingSlasher;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.main.GameState;
import main.game.stores.EntityStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;

public abstract class GameSystem {
    protected final SplittableRandom mRandom = new SplittableRandom();
    protected JSONEventBus mEventBus = null;
    protected GameState mGameState = null;
    protected GameModel mGameModel = null;
    private final Map<String, HashSlingingSlasher> mCheckSumMap = new LinkedHashMap<>();

    public GameSystem(GameModel gameModel) {
        mGameState = gameModel.getGameState();
        mEventBus = gameModel.getEventBus();
        mGameModel = gameModel;
    }

    public void update(GameModel model, SystemContext systemContext) { }
    public static Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }

    protected boolean isUpdated(String key, Object... values) {
        HashSlingingSlasher checkSum = mCheckSumMap.get(key);
        if (checkSum != null) {
            return checkSum.setOnDifference(values);
        }

        checkSum = new HashSlingingSlasher();
        mCheckSumMap.put(key, checkSum);
        return checkSum.setOnDifference(values);
    }
}
