package main.game.stores.pools.action;

import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.effect.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Action {

    protected List<Effect> mEffects = new ArrayList<>();
    public Action(JSONObject data) {

        JSONArray effects = data.getJSONArray("effects");
        for (int index = 0; index < effects.length(); index++) {
            JSONObject effectData = effects.getJSONObject(index);
            String name = effectData.getString("effect");

            Effect effect = getEffect(name, effectData);

            mEffects.add(effect);
        }
    }

    private static Effect getEffect(String name, JSONObject effectData) {
        Effect effect = null;
        switch (name) {
            case "cost" -> effect = new CostEffect(effectData);
            case "damage" -> effect = new DamageEffect(effectData);
            case "accuracy" -> effect = new AccuracyEffect(effectData);
            case "announcement" -> effect = new AnnounceEffect(effectData);
            case "user_animation" -> effect = new UserAnimationEffect(effectData);
            case "target_animation" -> effect = new TargetAnimationEffect(effectData);
        }
        return effect;
    }




//    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
//        if (mEffects.isEmpty()) { return; }
//
//        // Use a queue to process effects iteratively
//        Queue<Effect> effects = new LinkedList<>(mEffects);
//        AtomicBoolean isWaitingForAsyncEffect = new AtomicBoolean(false);
//
//        while (!effects.isEmpty() || isWaitingForAsyncEffect.get()) {
//            if (isWaitingForAsyncEffect.get()) {
//                // Wait until the asynchronous effect completes
//                continue;
//            }
//
//            // Poll the next effect from the queue
//            Effect currentEffect = effects.poll();
//
//            if (currentEffect == null) { continue; }
//
//            // Apply the effect
//            boolean isAsync = currentEffect.apply(model, user, targets);
//
//            if (isAsync) {
//                isWaitingForAsyncEffect.set(true);
//
//                // Add a listener to resume processing
//                currentEffect.addOnCompleteListener(() -> {
//                    isWaitingForAsyncEffect.set(false);
//                });
//            }
//        }
//    }

    // Validation phase before applying effects
    public boolean validateEffects(GameModel model, Entity user, Set<Entity> targets) {
        for (Effect effect : mEffects) {
            if (!effect.validate(model, user, targets)) {
                return false; // Abort if any effect is invalid
            }
        }
        return true; // All effects are valid
    }


    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
        // Use a queue to process effects iteratively
        Queue<Effect> effectQueue = new LinkedList<>(mEffects);

        // Process the next effect in the queue
        processNextEffect(effectQueue, model, user, targets);
    }

    private void processNextEffect(Queue<Effect> effectQueue, GameModel model, Entity user, Set<Entity> targets) {
        if (effectQueue.isEmpty()) {
            // All effects have been processed
            return;
        }

        // Retrieve the next effect
        Effect currentEffect = effectQueue.poll();

        // Apply the effect
        boolean isAsync = currentEffect.apply(model, user, targets);

        if (isAsync) {
            // Add a listener to continue processing after the async effect completes
            currentEffect.addOnCompleteListener(() -> {
                // Resume processing the next effect
                processNextEffect(effectQueue, model, user, targets);
            });
        } else {
            // If the effect is synchronous, immediately process the next one
            processNextEffect(effectQueue, model, user, targets);
        }
    }



//    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
//        int effectCount = mEffects.size();
//        if (effectCount == 0) { return; }
//
//        for (int index = 0; index < mEffects.size() - 1; index++) {
//            Effect current = mEffects.get(index);
//            Effect next = mEffects.get(index + 1);
//
//            current.apply(model, user, targets, () -> {
//                next.apply(model, user, targets, () -> {});
//            });
//        }
//
//        applyEffectSequentially(0, model, user, targets);
//    }
//
//    private void applyEffectSequentially(int index, GameModel model, Entity user, Set<Entity> targets) {
//        if (index >= mEffects.size()) {
//            // All effects have been applied
//            return;
//        }
//
//        Effect currentEffect = mEffects.get(index);
//        currentEffect.apply(model, user, targets, () -> {
//            // Once the current effect is complete, proceed to the next one
//            applyEffectSequentially(index + 1, model, user, targets);
//        });
//    }








//    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
//        int effectCount = mEffects.size();
//        if (effectCount == 0) { return; }
//
//        applyEffectSequentially(0, model, user, targets);
//    }
//
//    private void applyEffectSequentially(int index, GameModel model, Entity user, Set<Entity> targets) {
//        if (index >= mEffects.size()) {
//            // All effects have been applied
//            return;
//        }
//
//        Effect currentEffect = mEffects.get(index);
//        currentEffect.apply(model, user, targets, () -> {
//            // Once the current effect is complete, proceed to the next one
//            applyEffectSequentially(index + 1, model, user, targets);
//        });
//    }




//    protected Queue<Effect> mEffects = new LinkedList<>();
//    public Action(JSONObject data) {
//
//        JSONArray effects = data.getJSONArray("effects");
//        for (int index = 0; index < effects.length(); index++) {
//            JSONObject effectData = effects.getJSONObject(index);
//            String name = effectData.getString("effect");
//
//            Effect effect = null;
//            switch (name) {
//                case "damage" -> effect = new DamageEffect(effectData);
//                case "accuracy" -> effect = new AccuracyEffect(effectData);
//                case "announcement" -> effect = new AnnounceEffect(effectData);
//            }
//
//            mEffects.add(effect);
//        }
//    }
//
//    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
//        while (!mEffects.isEmpty()) {
//            Effect effect = mEffects.poll();
//            boolean success = effect.apply(model, user, targets);
//            if (success) { continue; }
//            break;
//        }
//    }

//    protected Map<String, Effect> mEffects = new HashMap<>();
//    public Action(JSONObject data) {
//
//        JSONArray effects = data.getJSONArray("effects");
//        for (int index = 0; index < effects.length(); index++) {
//            JSONObject effectData = effects.getJSONObject(index);
//            String name = effectData.getString("effect");
//
//            Effect effect = null;
//            switch (name) {
//                case "damage" -> effect = new DamageEffect(effectData);
//                case "accuracy" -> effect = new AccuracyEffect(effectData);
//                case "announcement" -> effect = new AnnounceEffect(effectData);
//            }
//
//            mEffects.put(UUID.randomUUID().toString(), effect);
//        }
//    }

//    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
//        for (Effect effect : mEffects.values()) {
//            boolean success = effect.apply(model, user, targets);
//            if (success) { continue; }
//            break;
//        }
//    }
}
