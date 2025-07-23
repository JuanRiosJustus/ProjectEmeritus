package main.game.systems;

import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.AnimationPool;

import java.util.HashMap;
import java.util.Map;


public class UnitVisualsSystem extends GameSystem {
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    private Map<String, Float> mUnitIdleSpeed = new HashMap<>();

    public UnitVisualsSystem(GameModel gameModel) { super(gameModel); }

//    public void update(GameModel model, String unitID) {
//
//        mSpriteWidth = model.getGameState().getSpriteWidth();
//        mSpriteHeight = model.getGameState().getSpriteHeight();
//
//        Entity unit = getEntityWithID(unitID);
//        StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
//        IdentityComponent identityComponent = unit.get(IdentityComponent.class);
//
////        System.out.println(mSpriteWidth + " " + mSpriteHeight + " " + Platform.isFxApplicationThread());
//        String id = AssetPool.getInstance().getOrCreateVerticalStretchAsset(
//                (int) (mSpriteWidth * .9),
//                (int) (mSpriteHeight * 1),
//                statisticsComponent.getUnit(),
//                -1,
//                model.getGameState().hashCode() + identityComponent.getID() + mSpriteWidth + mSpriteHeight
//        );
//
//        AssetComponent assetComponent = unit.get(AssetComponent.class);
//        assetComponent.putMainID(id);
//        AssetPool.getInstance().update(id);
//    }



    public void update(GameModel model, SystemContext systemContext) {

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        systemContext.getAllUnitEntityIDs().forEach(unitID -> {
            Entity unit = getEntityWithID(unitID);
            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
            IdentityComponent identityComponent = unit.get(IdentityComponent.class);

            String id = AnimationPool.getInstance().getOrCreateVerticalStretch(
                    statisticsComponent.getUnit(),
                    (int) (mSpriteWidth * .9),
                    (int) (mSpriteHeight),
                    (identityComponent.getID() + mSpriteWidth + mSpriteHeight)
            );

            AssetComponent assetComponent = unit.get(AssetComponent.class);
            assetComponent.putMainID(id);

            // Set the idle speed multiplier
            float initialSpeed = mUnitIdleSpeed.getOrDefault(id, 0f);
            if (initialSpeed == 0) {
                initialSpeed = mRandom.nextFloat(0.2f, 0.45f);
                mUnitIdleSpeed.put(id, initialSpeed);
                AnimationPool.getInstance().setSpeed(id, initialSpeed);
            }

            double deltaTime = model.getDeltaTime();
            AnimationPool.getInstance().update(id, deltaTime);
        });
    }

//    public void update(GameModel model, SystemContext systemContext) {
//
//        mSpriteWidth = model.getGameState().getSpriteWidth();
//        mSpriteHeight = model.getGameState().getSpriteHeight();
//
//        systemContext.getAllUnitEntityIDs().forEach(unitID -> {
//            Entity unit = getEntityWithID(unitID);
//            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
//            IdentityComponent identityComponent = unit.get(IdentityComponent.class);
//
////            System.out.println(mSpriteWidth + " " + mSpriteHeight + " " + Platform.isFxApplicationThread());
//            String id = AnimationPool.getInstance().getOrCreateVerticalStretchAsset(
//                    (int) (mSpriteWidth * .9),
//                    (int) (mSpriteHeight),
//                    statisticsComponent.getUnit(),
//                    -1,
//                    identityComponent.getID() + mSpriteWidth + "_" + mSpriteHeight
//            );
//
//            AssetComponent assetComponent = unit.get(AssetComponent.class);
//            assetComponent.putMainID(id);
//
//            double deltaTime = model.getDeltaTime();
//            AnimationPool.getInstance().update(id, deltaTime);
//        });
//    }
}
