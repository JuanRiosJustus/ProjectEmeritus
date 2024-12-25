package main.game.stores.pools.action.effect;

import main.constants.Tuple;
import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.action.ActionDatabase;
import main.game.systems.texts.FloatingText;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DamageEffect extends Effect {
    private int mBase = 0;
    private String mTargetResource = null;
    private List<Tuple<String, String, Float>> mScalings = null;
    public DamageEffect(JSONObject jsonObject) {
        super(jsonObject);

        mBase = jsonObject.getInt("base");
        mTargetResource = jsonObject.getString("target");
        JSONObject scaling = jsonObject.getJSONObject("scaling");

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
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
        StatisticsComponent statisticsComponent = user.get(StatisticsComponent.class);

        int damage = mBase;
        for (Tuple<String, String, Float> scaling : mScalings) {
            String magnitude = scaling.getFirst();
            String attribute = scaling.getSecond();
            Float value = scaling.getThird();
            int baseModifiedOrTotal = statisticsComponent.getScaling(attribute, magnitude);
            int additionalDamage = (int) (value * baseModifiedOrTotal);
            damage += additionalDamage;
        }

        if (damage != 0) {
            for (Entity target : targets) {

                Tile tile = target.get(Tile.class);

                // to remove environment
                if (tile.isNotNavigable()) { tile.deleteStructure(); }

                Entity targetUnit = tile.getUnit();
                if (targetUnit == null) { continue; }

                StatisticsComponent defendingStatisticsComponent = targetUnit.get(StatisticsComponent.class);
                defendingStatisticsComponent.toResource(mTargetResource, damage);

                Color color = damage > 0 ? ColorPalette.TRANSLUCENT_SUNSET_ORANGE : ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;
                announceWithFloatingTextCentered(model, String.valueOf(damage), targetUnit, color);
            }
        }

        return false;
    }
}
