package main.game.components;

import main.game.components.animation.Animation;

import java.util.*;

public class AnimationComponent extends Component {
    private final Queue<Animation> mAnimations = new LinkedList<>();

    public void addAnimation(Animation newAnimation) { mAnimations.add(newAnimation); }

    public Animation getCurrentAnimation() { return mAnimations.peek(); }

    public boolean hasNoAnimationsPending() { return mAnimations.isEmpty(); }
    public void popAnimation() {
        Animation animation = mAnimations.poll();
        if (animation == null) { return; }
        animation.notifyListeners();
    }

    public void addOnCompleteListener(Runnable listener) {
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