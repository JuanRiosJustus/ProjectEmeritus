package main.game.systems;


import javafx.scene.image.Image;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpdateSystem {
    private List<GameSystem> mGameSystems = new ArrayList<>();
    private JSONEventBus mEventBus = null;
    private VisualsSystem mVisualsSystem = null;
    public UpdateSystem(GameModel gameModel) {

        mGameSystems.add(new CameraSystem(gameModel));
        mGameSystems.add(new MovementSystem(gameModel));
        mGameSystems.add(new AbilitySystem(gameModel));
        mGameSystems.add(new BehaviorSystem(gameModel));
        mGameSystems.add(new AnimationSystem(gameModel));
        mGameSystems.add(new MovementSystem(gameModel));
        mVisualsSystem = new VisualsSystem(gameModel);
        mGameSystems.add(mVisualsSystem);
        mGameSystems.add(new OverlaySystem(gameModel));
        mGameSystems.add(new FloatingTextSystem(gameModel));
        mGameSystems.add(new UnitVisualsSystem(gameModel));
        mGameSystems.add(new HandleEndOfTurnSystem(gameModel));
        mGameSystems.add(new CombatSystem(gameModel));
    }
    public void update(GameModel model) {

        SystemContext systemContext = SystemContext.create(model);
        for (GameSystem gameSystem : mGameSystems) {
            gameSystem.update(model, systemContext);
        }

        JSONObject eventQueue = model.getGameState().consumeEventQueue();
        for (String key : eventQueue.keySet()) {
            JSONObject event = eventQueue.getJSONObject(key);
            mEventBus.publish(key, event);
        }

        boolean newRound = model.getSpeedQueue().update();
        if (newRound) { model.mLogger.log("New Round"); }
    }

    public Image getBackgroundWallpaper() {
        return mVisualsSystem.getBackgroundWallpaper();
    }
}
