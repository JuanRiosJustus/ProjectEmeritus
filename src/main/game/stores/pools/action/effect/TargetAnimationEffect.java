package main.game.stores.pools.action.effect;

import main.game.components.AnimationComponent;
import main.game.components.animation.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.util.Set;

import static main.game.systems.AnimationSystem.*;

public class TargetAnimationEffect extends Effect {
    private String mAnimation = null;
    public TargetAnimationEffect(JSONObject effect) {
        super(effect);
        String animationType = effect.getString("animation");
        mAnimation = animationType;
    }


    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
        // 2. Animate based on the ability range
        for (Entity target : targets) {
            Tile tile = target.get(Tile.class);
            Entity unit = tile.getUnit();
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
