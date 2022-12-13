package game.systems;

import engine.EngineController;
import game.components.CombatAnimations;
import game.components.SpriteAnimation;
import game.entity.Entity;
import game.stores.pools.AssetPool;

public class CombatAnimationSystem {

    public static void applyAnimations(Entity unit, String... animationNames) {
        CombatAnimations ca = unit.get(CombatAnimations.class);
        for (String animationName : animationNames) {
            if (animationName.isBlank()) { continue; }
            SpriteAnimation spriteAnimation = AssetPool.instance().getAbilityAnimation(animationName);
            if (spriteAnimation == null) { continue; }
            spriteAnimation.setIterationSpeed(4);
            ca.add(animationName, spriteAnimation);
        }
    }

    public static void update(EngineController engine, Entity unit) {

        // Update only if needed
        CombatAnimations ca = unit.get(CombatAnimations.class);
        int count = ca.count();
        if (count <= 0) { return; }

        // Maybe this takes care of some weird edge case
        SpriteAnimation current = ca.getCurrentAnimation();
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
