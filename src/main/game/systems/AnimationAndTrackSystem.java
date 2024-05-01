package main.game.systems;

import main.constants.Constants;
import main.engine.Engine;
import main.game.components.Vector3f;
import main.game.components.AnimationMovementTrack;
import main.game.components.Animation;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class AnimationAndTrackSystem extends GameSystem {

    public void update(GameModel model, Entity unit) {
        addMovementToSpriteAnimation(model, unit);
        Animation animation = unit.get(Animation.class);
        animation.update();
    }

    private void addMovementToSpriteAnimation(GameModel model, Entity unit) {
        AnimationMovementTrack amt = unit.get(AnimationMovementTrack.class);
        if (amt.track.isEmpty()) { return; }
        Animation animation = unit.get(Animation.class);
        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * amt.speed;
//        if (engine.model.ui.settings.fastForward.isSelected()) { pixelsTraveledThisTick *= 10; }
        double pixelsBetweenStartPositionAndEndPosition = Constants.CURRENT_SPRITE_SIZE;
        amt.progress += (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);

        Vector3f result = Vector3f.lerp(
                amt.track.get(amt.index),
                amt.track.get(amt.index + 1),
                amt.progress
        );
        animation.set(result.x, result.y);

        if (amt.progress > 1) {
            Vector3f next = amt.track.get(amt.index + 1);
            animation.set(next.x, next.y);
            amt.progress = 0;
            amt.index++;
            if (amt.index == amt.track.size() - 1) {
                amt.clear();
            }
        }
    }
}
