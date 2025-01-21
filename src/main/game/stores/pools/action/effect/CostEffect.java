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
    protected String mScalingMagnitude = null;
    protected String mScalingAttribute = null;
    protected float mScalingValue = 0f;

    public CostEffect(JSONObject effect) {
        super(effect);

        mBase = effect.getInt("base");
        mTargetResource = effect.getString("target");

        mScalingMagnitude = effect.optString("scaling_magnitude", null);
        mScalingAttribute = effect.optString("scaling_attribute", null);
        mScalingValue = effect.optFloat("scaling_value", 0f);
    }

    @Override
    public boolean validate(GameModel model, Entity user, Set<Entity> targets) {

        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int totalCost = 0;

        totalCost += (int) calculateCost(user, true);

        int unitTotalResource = statisticsComponent.getTotal(mTargetResource);

        boolean canPay = totalCost <= unitTotalResource;

        return canPay;
    }

    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int totalCost = (int) calculateCost(user, true);

        statisticsComponent.toResource(mTargetResource, -totalCost);

        return false;
    }

    public float calculateCost(Entity user, boolean addBase) {
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        float cost = 0;

        if (addBase) { cost += mBase; }

        if (getScalingValue() != 0) {
            String magnitude = mScalingMagnitude;
            String attribute = mScalingAttribute;
            float value = mScalingValue;
            int baseModifiedOrTotal = statisticsComponent.getScaling(attribute, magnitude);
            int additionalCost = (int) (value * baseModifiedOrTotal);
            cost += additionalCost;
        }

        return cost;
    }

    public String getResourceToTarget() { return mTargetResource; }
    public int getBaseCost() { return mBase; }
    public String getScalingMagnitude() { return mScalingMagnitude; }
    public String getScalingAttribute() { return mScalingAttribute; }
    public float getScalingValue() { return mScalingValue; }
}
