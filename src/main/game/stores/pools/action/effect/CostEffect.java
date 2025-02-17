package main.game.stores.pools.action.effect;

import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.util.*;

public class CostEffect extends Effect {

    protected int mBase = 0;
    protected String mBilledAttribute = null;
    private String mScalingAttribute = null;
    private String mScalingType = null;
    private float mScalingValue = 0f;

    public CostEffect(JSONObject effect) {
        super(effect);

        mBase = effect.optInt("base_cost", 0);
        mBilledAttribute = effect.getString("billed_attribute");

        mScalingType = effect.optString("scaling_type", null);
        mScalingAttribute = effect.optString("scaling_attribute", null);
        mScalingValue = effect.optFloat("scaling_value", 0);
    }

    @Override
    public boolean apply(GameModel model, String unitID, Set<String> targetTileIDs) {
        Entity user = getEntityFromID(unitID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int totalCost = (int) calculateCost(unitID, true);

        statisticsComponent.toResource(mBilledAttribute, -totalCost);

        return false;
    }

    public float calculateCost(String unitID, boolean addBase) {
        Entity user = getEntityFromID(unitID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        float cost = 0;

        if (addBase) { cost += mBase; }

        if (mScalingType != null) {
            String type = mScalingType;
            String attribute = mScalingAttribute;
            float scalar = mScalingValue;

            float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(attribute, type);
            float additionalCost = baseModifiedTotalMissingCurrent * scalar;
            cost += additionalCost;
        }

        return cost;
    }

    public float getScalingCost(String unitID) {
        float cost = 0;

        if (!hasScalingCost()) { return cost; }

        String type = mScalingType;
        String attribute = mScalingAttribute;
        float scalar = mScalingValue;

        Entity user = getEntityFromID(unitID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);
        float baseModifiedTotalMissingCurrent = statisticsComponent.getScaling(attribute, type);
        cost = baseModifiedTotalMissingCurrent * scalar;

        return cost;
    }


    public String getBilledAttribute() { return mBilledAttribute; }
    public int getBaseCost() { return mBase; }
    public boolean hasScalingCost() { return mScalingType != null && mScalingAttribute != null; }
    public String getScalingType() { return mScalingType; }
    public String getScalingAttribute() { return mScalingAttribute; }
    public float getScalingValue() { return mScalingValue; }
}
