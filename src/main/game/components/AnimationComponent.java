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

    public void addAnimation(Animation newAnimation) { mAnimations.add(newAnimation); }

    public Animation getCurrentAnimation() { return mAnimations.peek(); }
    public void popAnimation() {
        Animation animation = mAnimations.poll();
        if (animation == null) { return; }
        animation.notifyListeners();
    }

    public void addOnCompleteAnimationListener(Runnable listener) {
        Animation currentAnimation = getCurrentAnimation();
        if (currentAnimation == null) { return; }
        currentAnimation.addOnCompleteListener(listener);
    }

    public boolean hasPendingAnimations() { return !mAnimations.isEmpty(); }

//    public void addAnimationCompleteListener(Runnable listener) {
//        // Queue listener to be invoked after the animation finishes
//        animationCompleteListeners.add(listener);
//    }
//
//    public void completeAnimation() {
//        // Notify all listeners
//        for (Runnable listener : animationCompleteListeners) {
//            listener.run();
//        }
//        animationCompleteListeners.clear();
//    }
}