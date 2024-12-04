package main.game.components;

import main.constants.Vector3f;
import main.engine.Engine;
import main.game.components.animation.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class AnimationComponent extends Component {
    private final Queue<Animation> mAnimations = new LinkedList<>();
    private final Map<String, Animation> mBlockingAnimations = new HashMap<>();
    private final Map<String, Animation> mNonBlockingAnimations = new HashMap<>();

    public void addAnimation(Animation newAnimation, boolean isBlocking) {
        mAnimations.add(newAnimation);
        if (isBlocking) {
            mBlockingAnimations.put(newAnimation.toString(), newAnimation);
        } else {
            mNonBlockingAnimations.put(newAnimation.toString(), newAnimation);
        }
    }

    public Animation getCurrentAnimation() { return mAnimations.peek(); }
    public void popAnimation() { mAnimations.poll(); }

    public boolean hasPendingAnimations() { return !mAnimations.isEmpty(); }
}