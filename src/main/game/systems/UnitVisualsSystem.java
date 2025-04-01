package main.game.systems;

import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.AssetPool;


public class UnitVisualsSystem extends GameSystem {
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;

    public void update(GameModel model, String unitID) {

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        Entity unit = getEntityWithID(unitID);
        StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
        IdentityComponent identityComponent = unit.get(IdentityComponent.class);

//        System.out.println(mSpriteWidth + " " + mSpriteHeight + " " + Platform.isFxApplicationThread());
        String id = AssetPool.getInstance().getOrCreateVerticalStretchAsset(
                (int) (mSpriteWidth * .9),
                (int) (mSpriteHeight * 1),
                statisticsComponent.getUnit(),
                -1,
                model.getGameState().hashCode() + identityComponent.getID() + mSpriteWidth + mSpriteHeight
        );

        AssetComponent assetComponent = unit.get(AssetComponent.class);
        assetComponent.putMainID(id);
        AssetPool.getInstance().update(id);
    }
}
