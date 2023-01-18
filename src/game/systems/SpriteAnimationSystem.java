package game.systems;

import constants.Constants;
import engine.Engine;
import game.GameModel;
import game.components.Vector;
import game.components.MovementTrack;
import game.components.Animation;
import game.entity.Entity;

public class SpriteAnimationSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        addMovementToSpriteAnimation(model, unit);
        Animation animation = unit.get(Animation.class);
        animation.update();
    }

    private void addMovementToSpriteAnimation(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.track.isEmpty()) { return; }
        Animation animation = unit.get(Animation.class);
        double pixelsTraveledThisTick = Engine.instance().getDeltaTime() * movementTrack.speed;
//        if (engine.model.ui.settings.fastForward.isSelected()) { pixelsTraveledThisTick *= 10; }
        double pixelsBetweenStartPositionAndEndPosition = Constants.CURRENT_SPRITE_SIZE;
        movementTrack.progress += (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);
        Vector.lerp(
                movementTrack.track.get(movementTrack.index),
                movementTrack.track.get(movementTrack.index + 1),
                movementTrack.progress,
                animation.position
        );
        if (movementTrack.progress > 1) {
            animation.position.copy(movementTrack.track.get(movementTrack.index + 1));
            movementTrack.progress = 0;
            movementTrack.index++;
            if (movementTrack.index == movementTrack.track.size() - 1) {
                movementTrack.clear();
            }
        }
    }
}
