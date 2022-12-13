package game.systems;

import constants.Constants;
import engine.Engine;
import engine.EngineController;
import game.components.Vector;
import game.components.Movement;
import game.components.SpriteAnimation;
import game.entity.Entity;

public class SpriteAnimationSystem {

    public static void update(EngineController engine, Entity unit) {
        addMovementToSpriteAnimation(engine, unit);
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        spriteAnimation.update();
    }

    private static void addMovementToSpriteAnimation(EngineController engine, Entity unit) {
        Movement movement = unit.get(Movement.class);
        if (movement.track.isEmpty()) { return; }
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        double pixelsTraveledThisTick = Engine.get().getDeltaTime() * movement.speed;
        if (engine.model.ui.settings.fastForward.isSelected()) { pixelsTraveledThisTick *= 10; }
        double pixelsBetweenStartPositionAndEndPosition = Constants.SPRITE_SIZE;
        movement.progress += (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);
        Vector.lerp(
                movement.track.get(movement.index),
                movement.track.get(movement.index + 1),
                movement.progress,
                spriteAnimation.position
        );
        if (movement.progress > 1) {
            spriteAnimation.position.copy(movement.track.get(movement.index + 1));
            movement.progress = 0;
            movement.index++;
            if (movement.index == movement.track.size() - 1) {
                movement.clear();
            }
        }
    }
}
