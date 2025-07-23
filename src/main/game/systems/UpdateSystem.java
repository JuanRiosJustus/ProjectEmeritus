package main.game.systems;


import javafx.scene.image.Image;
import main.game.main.GameModel;
import main.game.systems.combat.CombatSystem;
import main.game.systems.texts.FloatingTextSystem;
import main.logging.EmeritusLogger;

import java.util.ArrayList;
import java.util.List;

public class UpdateSystem {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(UpdateSystem.class);
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
        mGameSystems.add(new HandleTurnSystem(gameModel));
        mGameSystems.add(new CombatSystem(gameModel));
    }
    public void update(GameModel model) {
        SystemContext systemContext = SystemContext.create(model);
        for (GameSystem gameSystem : mGameSystems) {
            gameSystem.update(model, systemContext);
        }

        boolean newRound = model.getSpeedQueue().refill();
        if (newRound) { mLogger.info("============================== New Round ================================"); }
    }

    public Image getBackgroundWallpaper() {
        return mVisualsSystem.getBackgroundWallpaper();
    }
}
