package main.game.systems;

import main.constants.Settings;
import main.engine.Engine;
import main.game.components.Vector3f;
import main.game.components.MovementTrack;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class MovementTrackSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        MovementTrack movementTrack = unit.get(MovementTrack.class);
        if (movementTrack.track.isEmpty()) { return; }

        int configuredSpriteHeight = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_HEIGHT);
        int configuredSpriteWidth = model.getIntegerSetting(Settings.GAMEPLAY_CURRENT_SPRITE_WIDTH);
        double pixelsBetweenStartPositionAndEndPosition = (double) (configuredSpriteWidth + configuredSpriteHeight) / 2;

        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * movementTrack.speed;
        movementTrack.progress += (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);

        Vector3f result = Vector3f.lerp(
                movementTrack.track.get(movementTrack.index),
                movementTrack.track.get(movementTrack.index + 1),
                movementTrack.progress
        );

        movementTrack.location.copy(result.x, result.y);

        if (movementTrack.progress >= 1) {
            Vector3f next = movementTrack.track.get(movementTrack.index + 1);
            movementTrack.location.copy(next.x, next.y);
            movementTrack.progress = 0;
            movementTrack.index++;
            if (movementTrack.index == movementTrack.track.size() - 1) {
                movementTrack.clear();
            }
        }
    }
}
