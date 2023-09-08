package main.game.systems;

import main.game.components.Overlay;
import main.game.components.Animation;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class OverlaySystem extends GameSystem {
    private final Queue<Animation> toDelete = new LinkedList<>();
    private final Map<Animation, Set<Entity>> animationsToSetMap = new HashMap<>();

    public void apply(Set<Entity> toApplyTo, Animation anime) {
        if (anime == null) { return; }
        // Adds the animation as an overlay to the targets
        for (Entity entity : toApplyTo) {
            Overlay overlay = entity.get(Overlay.class);
            // Make sure the animation is reset
            anime.reset();
            overlay.set(anime);
//            overlay.set(anime.copy());
        }
        animationsToSetMap.put(anime, toApplyTo);
    }

    @Override
    public void update(GameModel model, Entity unit) {
        // Update all the animations if possible. Remove animations that have finished
        for (Animation anime : animationsToSetMap.keySet()) {
            anime.update();   
            if (anime.hasCompletedLoop()) { 
                toDelete.add(anime); 
            } else {
                anime.update();   
            }
        }
        
        // Remove finished animations
        while (toDelete.size() > 0) {
            Animation entry = toDelete.poll();
            Set<Entity> shared = animationsToSetMap.get(entry);
            for (Entity entity : shared) {
                Overlay overlay = entity.get(Overlay.class);
                overlay.set(null);
            }
            animationsToSetMap.remove(entry);
        }
    }
}
