package main.game.stores.pools.action.effect;

import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import org.json.JSONObject;

import java.awt.Color;
import java.util.Set;

public class DamageEffect extends Effect {
    protected int mBaseBamage = 0;
    protected String mBilledAttribute = null;
    private String mScalingAttribute = null;
    private String mScalingType = null;
    private float mScalingValue = 0f;
    public DamageEffect(JSONObject effect) {
        super(effect);

        mBaseBamage = effect.optInt("base_damage", 0);
        mBilledAttribute = effect.getString("billed_attribute");

        mScalingType = effect.optString("scaling_type", null);
        mScalingAttribute = effect.optString("scaling_attribute", null);
        mScalingValue = effect.optFloat("scaling_value", 0);
    }

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
        defendingStatisticsComponent.toResource(mBilledAttribute, -damage);

        Color color = damage > 0 ? ColorPalette.TRANSLUCENT_SUNSET_ORANGE : ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;
        announceWithFloatingTextCentered(model, String.valueOf(damage), targetUnitID, color);
    }

    public float calculateDamage(String userID, boolean withBase) {
        Entity user = getEntityFromID(userID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        float damage = 0;

        if (withBase) { damage += mBaseBamage; }

        damage += getScalingDamage(userID);

        return damage;
    }

    public float getScalingDamage(String userID) {
        float damage = 0;

        if (!hasScalingDamage()) { return damage; }

        String type = mScalingType;
        String attribute = mScalingAttribute;
        float value = mScalingValue;

        Entity user = getEntityFromID(userID);
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);
        float baseModifiedOrTotal = statisticsComponent.getScaling(attribute, type);
        damage = (int) (value * baseModifiedOrTotal);

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


    public String getBilledAttribute() { return mBilledAttribute; }
    public int getBaseDamage() { return mBaseBamage; }
    public boolean hasScalingDamage() { return mScalingType != null && mScalingAttribute != null; }
    public String getScalingType() { return mScalingType; }
    public String getScalingAttribute() { return mScalingAttribute; }
    public float getScalingValue() { return mScalingValue; }
}
