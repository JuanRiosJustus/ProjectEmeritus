package main.game.stores.pools.action.effect;

import main.constants.Tuple;
import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.ActionDatabase;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CostEffect extends Effect {

    protected int mBase = 0;
    protected String mTargetResource = null;
    protected List<Tuple<String, String, Float>> mScalings = null;

    public CostEffect(JSONObject effect) {
        super(effect);

        mBase = effect.getInt("base");
        mTargetResource = effect.getString("target");
        JSONObject scaling = effect.getJSONObject("scaling");

        mScalings = new ArrayList<>();
        Tuple<String, String, Float> result = null;
        for (String scalingKey : scaling.keySet()) {
            String magnitude = scalingKey.substring(0, scalingKey.indexOf(UNDERSCORE_DELIMITER));
            String attribute = scalingKey.substring(scalingKey.indexOf(UNDERSCORE_DELIMITER) + 1);
            float value = scaling.getFloat(scalingKey);
            result = new Tuple<>(magnitude, attribute, value);
            mScalings.add(result);
        }
    }

    @Override
    public boolean validate(GameModel model, Entity user, Set<Entity> targets) {

        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int totalCost = 0;
        int baseCost = mBase;

        totalCost += baseCost;

        List<Tuple<String, String, Float>> scalingCosts = mScalings;
        for (Tuple<String, String, Float> scalingCost : scalingCosts) {
            String magnitude = scalingCost.getFirst();
            String attribute = scalingCost.getSecond();
            Float value = scalingCost.getThird();

            int scaling = statisticsComponent.getScaling(attribute, magnitude);
            int additionalCost = (int) (scaling * value);
            totalCost += additionalCost;
        }

        int unitTotalResource = statisticsComponent.getTotal(mTargetResource);

        boolean canPay = totalCost <= unitTotalResource;

        return canPay;
    }

    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int totalCost = 0;
        int baseCost = mBase;

        totalCost += baseCost;

        List<Tuple<String, String, Float>> scalingCosts = mScalings;
        for (Tuple<String, String, Float> scalingCost : scalingCosts) {
            String magnitude = scalingCost.getFirst();
            String attribute = scalingCost.getSecond();
            Float value = scalingCost.getThird();

            int scaling = statisticsComponent.getScaling(attribute, magnitude);
            int additionalCost = (int) (scaling * value);
            totalCost += additionalCost;
        }

        statisticsComponent.toResource(mTargetResource, -totalCost);

        return false;
    }
}
