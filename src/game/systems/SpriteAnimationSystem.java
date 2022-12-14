package game.systems;

import constants.Constants;
import engine.Engine;
import game.GameModel;
import game.components.Vector;
import game.components.Movement;
import game.components.SpriteAnimation;
import game.entity.Entity;

public class SpriteAnimationSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        addMovementToSpriteAnimation(model, unit);
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        spriteAnimation.update();
    }

    private void addMovementToSpriteAnimation(GameModel model, Entity unit) {
        Movement movement = unit.get(Movement.class);
        if (movement.track.isEmpty()) { return; }
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        double pixelsTraveledThisTick = Engine.instance().getDeltaTime() * movement.speed;
//        if (engine.model.ui.settings.fastForward.isSelected()) { pixelsTraveledThisTick *= 10; }
        double pixelsBetweenStartPositionAndEndPosition = Constants.CURRENT_SPRITE_SIZE;
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
