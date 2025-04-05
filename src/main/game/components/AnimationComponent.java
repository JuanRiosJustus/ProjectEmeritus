package main.game.components;

import main.game.components.animation.AnimationTrack;

import java.util.*;

public class AnimationComponent extends Component {
    private final Queue<AnimationTrack> mAnimationTracks = new LinkedList<>();

    public void addTrack(AnimationTrack newAnimationTrack) { mAnimationTracks.add(newAnimationTrack); }

    public AnimationTrack getCurrentAnimation() { return mAnimationTracks.peek(); }

    public boolean hasNoAnimationsPending() { return mAnimationTracks.isEmpty(); }
    public void completeTrack() {
        AnimationTrack animationTrack = mAnimationTracks.poll();
        if (animationTrack == null) { return; }
        animationTrack.notifyListeners();
    }

    public void addOnCompleteListener(Runnable listener) {
        AnimationTrack currentAnimationTrack = getCurrentAnimation();
        if (currentAnimationTrack == null) { return; }
        currentAnimationTrack.addOnCompleteListener(listener);
    }

    public boolean hasPendingAnimations() { return !mAnimationTracks.isEmpty(); }

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