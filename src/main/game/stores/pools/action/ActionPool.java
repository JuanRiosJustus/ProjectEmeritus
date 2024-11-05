package main.game.stores.pools.action;

import java.util.*;

import main.constants.Constants;
import main.constants.csv.CsvRow;
import main.constants.csv.CsvTable;
import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.MathUtils;

public class ActionPool {
    private CsvTable mActionData = null;
    private static ActionPool instance = null;
    public static final String RANGE_COLUMN = "Range",
            AREA_COLUMN = "Area",
            TYPE_COLUMN = "Type",
            ACTION_COLUMN = "Action";
    public static ActionPool getInstance() {
        if (instance == null) {
            instance = new ActionPool();
        }
        return instance;
    }

    private ActionPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            mActionData = new CsvTable(Constants.ACTION_DATABASE.replace(".json", ".csv"), "Action");
        } catch (Exception ex) {
            logger.info("Error parsing ability: " + ex.getMessage());
            System.exit(-1);
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public CsvRow getAction(String name) { return mActionData.get(name); }
    public int getArea(String action) { return getAction(action).getInt("Area"); }
    public int getRange(String action) { return getAction(action).getInt("Range"); }

    public String getColumn(String action, String column) {
        return mActionData.get(action).get(column);
    }
    public String getName(String action) { return getAction(action).getString("Action"); }

    public List<String> getColumns() { return mActionData.getHeader(); }

    public Map<String, Float> getDamage(Entity actorUnitEntity, String action, Entity actedOnUnitEntity) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        Map<String, Float> resultMap = new HashMap<>();
        Map<String, Float> damageMap = actionRow.getNumberMap("Damage_Formula");
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            String[] data = entry.getKey().split(" ");
            if (data.length != 4) { continue; }
            String target = data[0];
            String modifier = data[1];
            String attribute = data[2];
            String resource = data[3];
            Float value = entry.getValue();
            int damage = getValue(
                    actorUnitEntity,
                    actedOnUnitEntity,
                    target,
                    modifier,
                    attribute,
                    value
            );
            resultMap.put(resource, resultMap.getOrDefault(resource, 0f) + damage);
        }
        return resultMap;
    }

    public Map<String, Float> getResourceCosts(Entity unitEntity, String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        Map<String, Float> costMap = new HashMap<>();
        Map<String, Float> costKeys = actionRow.getNumberMap("Cost_Formula");
        for (Map.Entry<String, Float> entry : costKeys.entrySet()) {
            String[] key = entry.getKey().split(" ");
            Float value = entry.getValue();
            String calculation = key[0];
            String attribute = key[1];
            float cost = getValue(unitEntity, null, "Self", calculation, attribute, value);
            costMap.put(attribute, cost);
        }
        return costMap;
    }

    private int getValue(Entity actor, Entity acted, String target, String calculation, String attribute, float value) {
        StatisticsComponent stats = null;
        float total = 0;
        if (target.contains("Self")) {
            stats = actor.get(StatisticsComponent.class);
        } else if (target.contains("Other")) {
            stats = acted.get(StatisticsComponent.class);
        }

        if (stats != null) {
            switch (calculation) {
                case "Flat" ->  total = value;
                case "Base" ->  total = stats.getBase(attribute) * value;
                case "Modified" -> total = stats.getModified(attribute) * value;
                case "Total", "Percent_Max" -> total = stats.getTotal(attribute) * value;
                case "Percent_Missing" -> total = (stats.getTotal(attribute) - stats.getCurrent(attribute)) * value;
                case "Percent_Current" -> total = stats.getCurrent(attribute) * value;
            }
        }

        return (int) total;
    }
    public boolean isSuccessful(String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        float successChance = actionRow.getNumber("Accuracy");
        return MathUtils.passesChanceOutOf100(successChance);
    }

    public boolean shouldUsePhysicalDefense(String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        return actionRow.getInt("Range") <= 1;
    }

    public boolean hasSameTypeAttackBonus(Entity actorUnitEntity, String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        return !Collections.disjoint(
                actorUnitEntity.get(StatisticsComponent.class).getType(),
                actionRow.getList("Type"));
    }

    public boolean isDamagingAbility(String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        Map<String, Float> damageMap = actionRow.getNumberMap("Damage_Formula");
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            if (entry.getValue() > 0) { return true; }
        }
        return false;
    }
}
