package main.game.stores.pools.action.effect;

import main.constants.Tuple;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.ColorPalette;
import org.json.JSONObject;

import java.awt.Color;
import java.util.List;
import java.util.Set;

public class DamageEffect extends Effect {
    protected String mTargetResource = null;
    protected int mBase = 0;
    protected String mScalingMagnitude = null;
    protected String mScalingAttribute = null;
    protected float mScalingValue = 0f;
    private List<Tuple<String, String, Float>> mScalingList = null;
    public DamageEffect(JSONObject jsonObject) {
        super(jsonObject);

        mBase = jsonObject.getInt("base");
        mTargetResource = jsonObject.getString("target");

        mScalingMagnitude = jsonObject.optString("scaling_magnitude", null);
        mScalingAttribute = jsonObject.optString("scaling_attribute", null);
        mScalingValue = jsonObject.optFloat("scaling_value", 0);
    }


//    @Override
//    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
//        float damage = calculateDamage(user, true);
//
//        if (damage != 0) {
//            for (Entity target : targets) {
//                damage = calculateDamage(user, true);
////                float damageAfterBonuses = getDamageAfterBonuses(user, target, damage);
////                float damageAfterDefenses = getDamageAfterDefenses(user, target, damageAfterBonuses);
//                float damageAfterDefenses = 0;
//                int finalDamage = (int) damageAfterDefenses;
//                apply(model, user, finalDamage, target);
//            }
//        }
//
//        return false;
//    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {

        float damage = calculateDamage(userID, true);

        if (damage != 0) {
            for (String targetTileID : targetTileIDs) {
                damage = calculateDamage(userID, true);
                float damageAfterBonuses = getDamageAfterBonuses(userID, targetTileID, damage);
                float damageAfterDefenses = getDamageAfterDefenses(userID, targetTileID, damageAfterBonuses);
                int finalDamage = (int) damageAfterDefenses;
                apply(model, userID, finalDamage, targetTileID);
            }
        }

        return false;
    }

    private void apply(GameModel model, Entity user, int damage, Entity target) {

        Tile tile = target.get(Tile.class);

        // to remove environment
        if (tile.isNotNavigable()) { tile.deleteStructure(); }

        String targetUnitID = tile.getUnitID();
        Entity targetUnit = getEntityFromID(targetUnitID);
//        Entity targetUnit = tile.getUnit();
        if (targetUnit == null) { return; }

        StatisticsComponent defendingStatisticsComponent = targetUnit.get(StatisticsComponent.class);
        defendingStatisticsComponent.toResource(mTargetResource, -damage);

        Color color = damage > 0 ? ColorPalette.TRANSLUCENT_SUNSET_ORANGE : ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;
        announceWithFloatingTextCentered(model, String.valueOf(damage), targetUnit, color);
    }

    private void apply(GameModel model, String userID, int damage, String targetTileID) {

        Entity target = getEntityFromID(targetTileID);
        Tile tile = target.get(Tile.class);

        // to remove environment
        if (tile.isNotNavigable()) { tile.deleteStructure(); }

        String targetUnitID = tile.getUnitID();
        Entity targetUnit = getEntityFromID(targetUnitID);
//        Entity targetUnit = tile.getUnit();
        if (targetUnit == null) { return; }

        StatisticsComponent defendingStatisticsComponent = targetUnit.get(StatisticsComponent.class);
        defendingStatisticsComponent.toResource(mTargetResource, -damage);

        Color color = damage > 0 ? ColorPalette.TRANSLUCENT_SUNSET_ORANGE : ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;
        announceWithFloatingTextCentered(model, String.valueOf(damage), targetUnit, color);
    }

    public float calculateDamage(String userID, boolean addBase) {
        Entity user = EntityStore.getInstance().get(userID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        float damage = 0;

        if (addBase) { damage += mBase; }

        // this action does no damage
        if (getScalingValue() != 0) {
            String magnitude = mScalingMagnitude;
            String attribute = mScalingAttribute;
            float value = mScalingValue;
            float baseModifiedOrTotal = statisticsComponent.getScaling(attribute, magnitude);
            float additionalDamage = (int) (value * baseModifiedOrTotal);
            damage += additionalDamage;
        }

        return damage;
    }

    public float calculateDamage(Entity user, boolean addBase) {
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        float damage = 0;

        if (addBase) { damage += mBase; }

        // this action does no damage
        if (getScalingValue() != 0) {
            String magnitude = mScalingMagnitude;
            String attribute = mScalingAttribute;
            float value = mScalingValue;
            float baseModifiedOrTotal = statisticsComponent.getScaling(attribute, magnitude);
            float additionalDamage = (int) (value * baseModifiedOrTotal);
            damage += additionalDamage;
        }

        return damage;
    }

    private float getDamageAfterDefenses(String userID, String targetID, float damage) {
        return damage;
    }
    private float getDamageAfterBonuses(String userID, String targetID, float damage) { return damage; }
//    private float getDamageAfterDefenses(Entity user, Entity target, float damage) {
//        return damage;
//    }
//    private float getDamageAfterBonuses(Entity user, Entity target, float damage) {
//        if (true) { return damage; }
//
////        float finalDamage = damage;
//
////        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);
////        logger.debug("Base Damage: {}", finalDamage);
//        // 2. Reward units using attacks that are same type as themselves
////        boolean isSameTypeAttackBonus = ActionDatabase.getInstance().hasSameTypeAttackBonus(user, action);
////        if (isSameTypeAttackBonus) {
////            float stabBonus = finalDamage * .5f;
////            finalDamage += stabBonus;
////        }
//
////        // 3. Penalize using attacks against units that share the type as the attack
////        if (hasSameTypeAttackBonus(actedOnUnitEntity, action)) {
////            float stdp = finalDamage * .5f;
////            logger.debug("{}(Current) - {}({}) = {}", finalDamage, stdp, STDP_PENALTY, (finalDamage - stdp));
//////            mDamagePropertiesMap.put(resource + "_" + STDP_PENALTY, stdp);
////            finalDamage -= stdp;
////        }
//
////        if (isAverseToAbilityType(defender, action)) {
////            float aversion = finalDamage * .5f;
////            logger.debug("{}(Current) + {}({}) = {}", finalDamage, aversion, AVERSION_BONUS, (finalDamage + aversion));
//////            mDamagePropertiesMap.put(resource + "_" + AVERSION_BONUS, aversion);
////            finalDamage += aversion;
////        }
//
//        // 4.5 determine if the attack is critical
////        boolean isCrit = MathUtils.passesChanceOutOf100(.05f);
////        if (isCrit) {
////            float cridDamage = finalDamage * 2;
//////            mDamagePropertiesMap.put(resource + "_" + CRIT_BONUS, crit);
////            finalDamage += cridDamage;
////        }
//
////        if (defender.get(TagComponent.class).contains(Constants.NEGATE)) {
////            float ngte = finalDamage * .9f;
////            logger.debug("{}(Current) - {}({}) = {}", finalDamage, ngte, Constants.NEGATE, (finalDamage - ngte));
//////            mDamagePropertiesMap.put(resource + "_" + Constants.NEGATE, ngte);
////            finalDamage -= ngte;
////        }
//        return 0;
//    }


    public String getResourceToTarget() { return mTargetResource; }
    public int getBaseDamage() { return mBase; }
    public String getScalingMagnitude() { return mScalingMagnitude; }
    public String getScalingAttribute() { return mScalingAttribute; }
    public float getScalingValue() { return mScalingValue; }
}
