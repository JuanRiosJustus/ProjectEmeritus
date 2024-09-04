package main.game.systems;

import main.game.main.Settings;
import main.engine.Engine;
import main.constants.Vector3f;
import main.game.components.MovementTrackComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.RandomUtils;

public class MovementTrackSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        MovementTrackComponent movementTrackComponent = unit.get(MovementTrackComponent.class);
        if (movementTrackComponent.isEmpty()) { return; }

        int configuredSpriteHeight = model.getSettings().getSpriteHeight();
        int configuredSpriteWidth = model.getSettings().getSpriteWidth();
        double pixelsBetweenStartPositionAndEndPosition = (double) (configuredSpriteWidth + configuredSpriteHeight) / 2;

        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * movementTrackComponent.getSpeed();
        movementTrackComponent.increaseProgress(
                (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition)
        );

        Vector3f result = Vector3f.lerp(
                movementTrackComponent.getVectorAt(movementTrackComponent.getIndex()),
                movementTrackComponent.getVectorAt(movementTrackComponent.getIndex() + 1),
                movementTrackComponent.getProgress()
        );

        movementTrackComponent.setLocation((int) result.x, (int) result.y);

        if (movementTrackComponent.getProgress() >= 1) {
            Vector3f next = movementTrackComponent.getVectorAt(movementTrackComponent.getIndex() + 1);
            movementTrackComponent.setLocation((int) next.x, (int) next.y);
            movementTrackComponent.setProgress(0);
            movementTrackComponent.incrementIndex();

            if (movementTrackComponent.getIndex() == movementTrackComponent.getTrackMarkers() - 1) {
                movementTrackComponent.clear();
            }
        }
    }
}
