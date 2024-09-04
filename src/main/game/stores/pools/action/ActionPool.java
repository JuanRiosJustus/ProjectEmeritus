package main.game.stores.pools.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Float> getDamage(Entity userUnitEntity, String action) {
        CsvRow actionRow = ActionPool.getInstance().getAction(action);
        Map<String, Float> resultMap = new HashMap<>();
        Map<String, Float> damageMap = actionRow.getNumberMap("Damage_Formula");
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            String[] data = entry.getKey().split(" ");
            String modifier = data[0];
            String node = data[1];
            String resource = data[2];
            Float value = entry.getValue();
            int damage = getValueFromNodeByModification(userUnitEntity, modifier, node, value);
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
            String modifier = key[0];
            String resource = key[1];
            float cost = getValueFromNodeByModification(unitEntity, modifier, resource, value);
            costMap.put(resource, cost);
        }
        return costMap;
    }
    private int getValueFromNodeByModification(Entity unitEntity, String modifier, String resource, float value) {
        StatisticsComponent stats = unitEntity.get(StatisticsComponent.class);
        float total = 0;
        switch (modifier) {
            case "Flat" ->  total = value;
            case "Base" ->  total = stats.getStatBase(resource) * value;
            case "Modified" -> total = stats.getStatModified(resource) * value;
            case "Missing" -> total = (stats.getStatTotal(resource) - stats.getStatCurrent(resource)) * value;
            case "Current" -> total = stats.getStatCurrent(resource) * value;
            case "Total" -> total = stats.getStatTotal(resource) * value;
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
}
