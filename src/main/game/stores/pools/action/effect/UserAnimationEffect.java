package main.game.stores.pools.action.effect;

import main.game.components.AnimationComponent;
import main.game.components.animation.Animation;
import main.game.entity.Entity;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.util.Set;

import static main.game.systems.AnimationSystem.GYRATE;
import static main.game.systems.AnimationSystem.TO_TARGET_AND_BACK;

public class UserAnimationEffect extends Effect {
    private String mAnimation = null;
    public UserAnimationEffect(JSONObject effect) {
        super(effect);
        String animationType = effect.getString("animation");
        if (animationType.equalsIgnoreCase(TO_TARGET_AND_BACK)) {
            mAnimation = TO_TARGET_AND_BACK;
        } else {
            mAnimation = GYRATE;
        }
    }

//    @Override
//    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
//        // 2. Animate based on the ability range
//        Animation animation = model.getSystems().getAnimationSystem().applyAnimation(
//                model,
//                user,
//                mAnimation,
//                targets.iterator().next()
//        );
//
//        // Trigger an animation for the user
//        // Add a listener to notify when the animation completes
//        AnimationComponent animationComponent = user.get(AnimationComponent.class);
//        animationComponent.addOnCompleteAnimationListener(() -> {
//            // Notify that this effect is complete
//            notifyComplete();
//        });
//
//        // Return true to indicate this effect is asynchronous
//        return true;
//    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {
        // 2. Animate based on the ability range
        Entity user = getEntityFromID(userID);
        String targetTileID = targetTileIDs.iterator().next();
        Entity target = getEntityFromID(targetTileID);
        Animation animation = model.getSystems().getAnimationSystem().applyAnimation(
                model,
                user,
                mAnimation,
                target
        );

        // Trigger an animation for the user
        // Add a listener to notify when the animation completes
        AnimationComponent animationComponent = user.get(AnimationComponent.class);
        animationComponent.addOnCompleteListener(() -> {
            // Notify that this effect is complete
            notifyComplete();
        });

        // Return true to indicate this effect is asynchronous
        return true;
    }

//    @Override
//    public boolean apply(GameModel model, Entity user, Set<Entity> targets, Runnable onComplete) {
//        // 2. Animate based on the ability range
//        Animation animation = model.getSystems().getAnimationSystem().applyAnimation(
//                model,
//                user,
//                mAnimation,
//                targets.iterator().next()
//        );
//
//        animation.addOnCompleteListener(onComplete);
//
//
//        return true;
//    }
}
