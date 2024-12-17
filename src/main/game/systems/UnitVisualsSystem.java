package main.game.systems;

import main.game.components.IdentityComponent;
import main.game.components.StatisticsComponent;
import main.graphics.Animation;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.AssetPool;


public class UnitVisualsSystem extends GameSystem {
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    @Override
    public void update(GameModel model, Entity unit) {

        mSpriteWidth = model.getGameState().getSpriteWidth();
        mSpriteHeight = model.getGameState().getSpriteHeight();

        StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
        IdentityComponent identityComponent = unit.get(IdentityComponent.class);
        String animation = AssetPool.STRETCH_Y_ANIMATION;
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                statisticsComponent.getUnit(),
                animation,
                -1,
                identityComponent.getID() + mSpriteWidth + mSpriteHeight
        );
        AssetComponent assetComponent = unit.get(AssetComponent.class);
//        assetComponent.put(AssetComponent.UNIT_ASSET, id);
        assetComponent.putMainID(id);
        Animation anime = AssetPool.getInstance().getAnimation(id);
        anime.update();
    }
}
