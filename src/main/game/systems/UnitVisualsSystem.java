package main.game.systems;

import main.game.components.Animation;
import main.game.components.Assets;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.AssetPool;

import java.util.HashMap;
import java.util.Map;


public class UnitVisualsSystem extends GameSystem {
    private final Map<String, String> mUnitToNonSpaceName = new HashMap<>();
    private int mSpriteWidth = 0;
    private int mSpriteHeight = 0;
    @Override
    public void update(GameModel model, Entity unit) {

        mSpriteWidth = model.getSettings().getSpriteWidth();
        mSpriteHeight = model.getSettings().getSpriteHeight();

        Statistics statistics = unit.get(Statistics.class);
        Identity identity = unit.get(Identity.class);
        String animation = AssetPool.STRETCH_Y_ANIMATION;
        String id = AssetPool.getInstance().getOrCreateAsset(
                model,
                statistics.getUnitFileName(),
                animation,
                identity.getUuid() + mSpriteWidth + mSpriteHeight
        );
        Assets assets = unit.get(Assets.class);
        assets.put(Assets.UNIT_ASSET, id);
        Animation anime = AssetPool.getInstance().getAnimation(id);
        anime.update();
    }
}
