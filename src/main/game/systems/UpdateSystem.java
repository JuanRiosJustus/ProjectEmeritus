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
    private CameraSystem mCameraSystem = null;
    private List<GameSystem> mGameSystems = new ArrayList<>();
    private GameModel mGameModel = null;
    private JSONEventBus mEventBus = null;
    private VisualsSystem mVisualsSystem = null;
    public UpdateSystem(GameModel gameModel) {


        mGameModel = gameModel;

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

    public void publish(String eventType, JSONObject eventData) {
        mEventBus.publish(eventType, eventData);
    }

    private boolean endTurn = false;
    private final EmeritusLogger logger = EmeritusLogger.create(getClass());
//    private final HandleEndOfTurnSystem mHandleEndOfTurnSystem = new HandleEndOfTurnSystem();
//    public final AnimationSystem mAnimationSystem = new AnimationSystem();
//    public final OverlaySystem mOverlaySystem = new OverlaySystem();
//    public final FloatingTextSystem mFloatingTextSystem = new FloatingTextSystem();
//    private final GemSpawnerSystem gemSpawnerSystem = new GemSpawnerSystem();
//    private final VisualsSystem mVisualsSystem = new VisualsSystem();
//    private final UnitVisualsSystem mUnitVisualsSystem = new UnitVisualsSystem();
//    private final AbilitySystem mAbilitySystem = new AbilitySystem();
//    private final BehaviorSystem mBehaviourSystem = new BehaviorSystem();
//    private final MovementSystem mMovementSystem = new MovementSystem();

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


//        mVisualsSystem.createBackgroundImageWallpaper(model);
    }





    public void endTurn() { endTurn = true; }

    private void endTurn(GameModel model, Entity unit) {
//        TagComponent tagComponent = unit.get(TagComponent.class);
////        model.getSpeedQueue().dequeue();
//        if (tagComponent.contains(TagComponent.YIELD)) {
////            model.getSpeedQueue().requeue(unit);
//        }
//
//        Entity turnStarter = model.getSpeedQueue().peek();
//        if (turnStarter != null) { model.mLogger.log(turnStarter.get(IdentityComponent.class) + "'s turn starts"); }
//
//        logger.info("Starting new Turn");
//
//        AbilityComponent abilityComponent = unit.get(AbilityComponent.class);
//        abilityComponent.reset();
//
//        MovementComponent movementComponent = unit.get(MovementComponent.class);
//        movementComponent.reset();
//
//        Behavior behavior = unit.get(AiBehavior.class);
////        if (behavior == null) { behavior = unit.get(UserBehavior.class); }
////        behavior.reset();
//
////        Tags tags = unit.get(Tags.class);
//        TagComponent.handleEndOfTurn(model, unit);
//        tagComponent.reset();
//
////        Passives passives = unit.get(Passives.class);
////        if (passives.contains(Passives.MANA_REGEN_I)) {
////            Summary summary = unit.get(Summary.class);
////            int amount = summary.addTotalAmountToResource(Summary.MANA, .05f);
////            Animation animation = unit.get(Animation.class);
////            model.system.floatingText.floater("+" + amount + "EP", animation.getVector(), ColorPalette.WHITE);
////        }
//
//        gemSpawnerSystem.update(model, unit);
//        endTurn = false;
    }

//    public FloatingTextSystem getFloatingTextSystem() { return mFloatingTextSystem; }
//    public AbilitySystem getActionSystem() { return mAbilitySystem; }
//    public MovementSystem getMovementSystem() { return mMovementSystem; }
//    public AnimationSystem getAnimationSystem() { return mAnimationSystem; }
    public Image getBackgroundWallpaper() {
        return mVisualsSystem.getBackgroundWallpaper();
    }
}
