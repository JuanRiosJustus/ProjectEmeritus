package game.systems;

import game.GameModel;
import game.components.CombatAnimations;
import game.components.Animation;
import game.entity.Entity;
import game.stores.pools.AssetPool;

public class CombatAnimationSystem extends GameSystem {

    public void apply(Entity unit, String... animationNames) {
        CombatAnimations ca = unit.get(CombatAnimations.class);
        for (String animationName : animationNames) {
            if (animationName.isBlank()) { continue; }
            Animation animation = AssetPool.instance().getAbilityAnimation(animationName);
            if (animation == null) { continue; }
            animation.setIterationSpeed(4);
            ca.add(animationName, animation);
        }
    }

    @Override
    public void update(GameModel model, Entity unit) {
        // Update only if needed
        CombatAnimations ca = unit.get(CombatAnimations.class);
        int count = ca.count();
        if (count <= 0) { return; }

        // Maybe this takes care of some weird edge case
        Animation current = ca.getCurrentAnimation();
        if (current == null) { return; }

        // Check that the node has no more animations to play by checking if using last frame
        if (current.getCurrentFrame() >= current.getNumberOfFrames() - 1) {
            // schedule node to be deleted if last frame and last node, else get next animation
            if (count <= 1) { current.reset(); ca.poll(); }
        } else {
            current.update();
        }
    }
}
