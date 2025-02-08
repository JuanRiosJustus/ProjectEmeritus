package main.game.stores.pools.action.effect;

import main.game.components.animation.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.util.Set;

public class TargetAnimationEffect extends Effect {
    private String mAnimation = null;
    public TargetAnimationEffect(JSONObject effect) {
        super(effect);
        String animationType = effect.getString("animation");
        mAnimation = animationType;
    }


//    @Override
//    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
//        // 2. Animate based on the ability range
//        for (Entity target : targets) {
//            Tile tile = target.get(Tile.class);
//            String entityID = tile.getUnitID();
//            Entity unit = EntityStore.getInstance().get(entityID);
////            Entity unit = tile.getUnit();
//            if (unit == null) { continue; }
//
//            Animation animation = model.getSystems().getAnimationSystem().applyAnimation(
//                    model,
//                    unit,
//                    mAnimation,
//                    null
//            );
//        }
//
//        // Return true to indicate this effect is asynchronous
//        return false;
//    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {
        // 2. Animate based on the ability range
        for (String targetTileID : targetTileIDs) {
            Entity target = getEntityFromID(targetTileID);
            Tile tile = target.get(Tile.class);
            String entityID = tile.getUnitID();
            Entity unit = getEntityFromID(entityID);
            if (unit == null) { continue; }

            Animation animation = model.getSystems().getAnimationSystem().applyAnimation(
                    model,
                    unit,
                    mAnimation,
                    null
            );
        }

        // Return true to indicate this effect is asynchronous
        return false;
    }
}
