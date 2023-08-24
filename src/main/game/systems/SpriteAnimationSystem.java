package main.game.systems;

import main.constants.Constants;
import main.engine.Engine;
import main.game.components.Vector;
import main.game.components.Track;
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
        Track track = unit.get(Track.class);
        if (track.track.isEmpty()) { return; }
        Animation animation = unit.get(Animation.class);
        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * track.speed;
//        if (engine.model.ui.settings.fastForward.isSelected()) { pixelsTraveledThisTick *= 10; }
        double pixelsBetweenStartPositionAndEndPosition = Constants.CURRENT_SPRITE_SIZE;
        track.progress += (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);
        Vector.lerp(
                track.track.get(track.index),
                track.track.get(track.index + 1),
                track.progress,
                animation.position
        );
        if (track.progress > 1) {
            animation.position.copy(track.track.get(track.index + 1));
            track.progress = 0;
            track.index++;
            if (track.index == track.track.size() - 1) {
                track.clear();
            }
        }
    }
}
