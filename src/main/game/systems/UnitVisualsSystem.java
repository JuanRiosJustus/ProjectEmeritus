package main.game.systems;

import main.constants.Settings;
import main.game.components.Animation;
import main.game.components.Assets;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.asset.AssetPool;


public class UnitVisualsSystem extends GameSystem {
    @Override
    public void update(GameModel model, Entity unit) {

        Assets unitAssets = unit.get(Assets.class);
        Animation animation = unitAssets.getAnimation(Assets.UNIT_ASSET);
        animation.update();
    }
}
