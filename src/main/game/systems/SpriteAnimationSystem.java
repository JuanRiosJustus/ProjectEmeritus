package main.game.systems;

import main.constants.Constants;
import main.engine.Engine;
import main.game.components.Vector;
import main.game.components.MovementTrack;
import main.game.components.Animation;
import main.game.entity.Entity;
import main.game.main.GameModel;

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
        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * movementTrack.speed;
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
