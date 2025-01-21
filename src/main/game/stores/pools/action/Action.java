package main.game.stores.pools.action;

import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.effect.*;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Action {
    protected Map<String, Effect> mEffects = new LinkedHashMap<>();
    public Action(JSONObject data) {
        JSONArray effectsData = data.getJSONArray("effects");
        for (int index = 0; index < effectsData.length(); index++) {
            JSONObject effectData = effectsData.getJSONObject(index);
            String name = effectData.getString("effect");
            Effect effect = getEffect(name, effectData);

            mEffects.put(UUID.randomUUID().toString(), effect);
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
            case "tag_to_user" -> effect = new TagToUserEffect(effectData);
            case "tag_to_target" -> effect = new TagToTargetEffect(effectData);
        }
        return effect;
    }

    public int getTotalDamage(Entity user, String resource) {
        int baseDamage = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == DamageEffect.class)
                .map(e -> (DamageEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .mapToInt(DamageEffect::getBaseDamage)
                .sum();

        int scalingDamage = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == DamageEffect.class)
                .map(e -> (DamageEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .filter(e -> e.getScalingValue() != 0)
                .mapToInt(e -> (int)e.calculateDamage(user, false))
                .sum();

        return baseDamage + scalingDamage;
    }

    public List<String> getTotalDamageFormula(Entity user, String resource) {
        int baseDamage = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == DamageEffect.class)
                .map(e -> (DamageEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .mapToInt(DamageEffect::getBaseDamage)
                .sum();

        List<String> formula = new ArrayList<>();
        formula.add(baseDamage + " = Base");

        List<String> scalingDamage = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == DamageEffect.class)
                .map(e -> (DamageEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .filter(e -> e.getScalingValue() != 0)
                .map(e -> {
                   String prettyPercent = StringUtils.floatToPercentage(e.getScalingValue());
                   String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(e.getScalingMagnitude());
                   String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(e.getScalingAttribute());

                    StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);
                    int value = statisticsComponent.getScaling(e.getScalingAttribute(), e.getScalingMagnitude());
                    int finalValue = (int) (value * e.getScalingValue());

                   return finalValue + " = " + prettyPercent + " " + prettyMagnitude + " " + prettyAttribute;
                })
                .toList();
        formula.addAll(scalingDamage);

        return formula;
    }

    public int getTotalCost(Entity user, String resource) {
        int baseCost = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == CostEffect.class)
                .map(e -> (CostEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .mapToInt(CostEffect::getBaseCost)
                .sum();

        int scalingCost = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == CostEffect.class)
                .map(e -> (CostEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .filter(e -> e.getScalingValue() != 0)
                .mapToInt(e -> (int) e.calculateCost(user, false))
                .sum();

        return baseCost + scalingCost;
    }

    public List<String> getTotalCostFormula(Entity user, String resource) {
        int baseCost = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == CostEffect.class)
                .map(e -> (CostEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .mapToInt(CostEffect::getBaseCost)
                .sum();

        List<String> formula = new ArrayList<>();
        formula.add(baseCost + " = Base");

        List<String> scalingCost = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == CostEffect.class)
                .map(e -> (CostEffect)e)
                .filter(e -> e.getResourceToTarget().equalsIgnoreCase(resource))
                .filter(e -> e.getScalingValue() != 0)
                .map(e -> {
                    String prettyPercent = StringUtils.floatToPercentage(e.getScalingValue());
                    String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(e.getScalingMagnitude());
                    String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(e.getScalingAttribute());

                    StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);
                    int value = statisticsComponent.getScaling(e.getScalingAttribute(), e.getScalingMagnitude());
                    int finalValue = (int) (value * e.getScalingValue());

                    return finalValue + " = " + prettyPercent + " " + prettyMagnitude + " " + prettyAttribute;
                })
                .toList();
        formula.addAll(scalingCost);

        return formula;
    }



    public Set<String> getResourcesToDamage() {
        Set<String> resources = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == DamageEffect.class)
                .map(e -> {
                    DamageEffect effect = (DamageEffect) e;
                    String resourceToTarget = effect.getResourceToTarget();
                    return  resourceToTarget;
                })
                .collect(Collectors.toSet());
        return resources;
    }

    public Set<String> getResourcesToCost() {
        Set<String> resources = mEffects.values()
                .stream()
                .filter(e -> e.getClass() == CostEffect.class)
                .map(e -> {
                    CostEffect effect = (CostEffect) e;
                    String resourceToTarget = effect.getResourceToTarget();
                    return  resourceToTarget;
                })
                .collect(Collectors.toSet());
        return resources;
    }

    // Validation phase before applying effects
    public boolean validateEffects(GameModel model, Entity user, Set<Entity> targets) {
        for (Map.Entry<String, Effect> entry : mEffects.entrySet()) {
            boolean isValid = entry.getValue().validate(model, user, targets);
            if (isValid) { continue; }
            // Abort on any validation failures
            return false;
        }
        return true; // All effects are valid
    }


    public void applyEffects(GameModel model, Entity user, Set<Entity> targets) {
        Collection<Effect> effects = mEffects.values();
        // Use a queue to process effects iteratively
        Queue<Effect> effectQueue = new LinkedList<>(effects);

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
}
