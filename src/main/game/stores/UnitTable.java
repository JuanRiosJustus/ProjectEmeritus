package main.game.stores;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.logging.EmeritusLogger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UnitTable {

    private static final String UNIT_TABLE_PATH = "./res/database/unit.json";
    private static final String UNIT_STATISTICS_KEY = "statistic.";
    private static UnitTable mInstance = null;
    public static UnitTable getInstance() {
        if (mInstance == null) {
            mInstance = new UnitTable();
        }
        return mInstance;
    }
    private final Map<String, JSONObject> mUnitMap = new HashMap<>();

    private UnitTable() {
        EmeritusLogger logger = EmeritusLogger.create(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
            String raw = Files.readString(Paths.get(UNIT_TABLE_PATH));
            JSONArray rows = JSONArray.parse(raw);
            for (int row = 0; row < rows.size(); row++) {
                JSONObject data = rows.getJSONObject(row);
                String unit = data.getString("unit").toLowerCase(Locale.ROOT);
                mUnitMap.put(unit, data);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.info("Error parsing unit: " + ex.getMessage());
            System.exit(-1);
        }
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Collection<String> getAllUnits() {
        return mUnitMap.keySet();
    }

    public Set<String> getUnits() { return mUnitMap.keySet(); }
    public JSONObject getUnit(String unit) {
        return mUnitMap.get(unit);
    }
    public JSONObject getStatistics(String unit) {
        JSONObject data = mUnitMap.get(unit);
        JSONObject result = new JSONObject();
        for (String rawKey : data.keySet()) {
            Object value = data.get(rawKey);
            boolean isNumber = value instanceof Number;
            if (!isNumber) { continue; }
            boolean isStatistic = rawKey.startsWith(UNIT_STATISTICS_KEY);
            if (!isStatistic) { continue; }
            String simpleKey = rawKey.substring(rawKey.lastIndexOf(".") + 1);
            result.put(simpleKey, value);
        }
        return result;
    }

    public JSONArray getType(String unit) {
        return getUnit(unit).getJSONArray("type");
    }
//    public String getBasicAbility(String unit) {
//        return getUnit(unit).getJSONObject("abilities").getString("basic");
//    }
//    public String getPassiveAbility(String unit) {
//        return getUnit(unit).getJSONObject("abilities").getString("passive");
//    }
//    public JSONArray getOtherAbility(String unit) {
//        return getUnit(unit).getJSONObject("abilities").getJSONArray("other");
//    }

    public String getBasicAbility(String unit) { return getUnit(unit).getString("ability.basic"); }
    public String getTraitAbility(String unit) { return getUnit(unit).getString("ability.trait"); }
    public String getReactionAbility(String unit) { return getUnit(unit).getString("ability.reaction"); }
    public String getSlot1Ability(String unit) { return getUnit(unit).getString("ability.slot_1"); }
    public String getSlot2Ability(String unit) { return getUnit(unit).getString("ability.slot_2"); }
    public String getSlot3Ability(String unit) { return getUnit(unit).getString("ability.slot_3"); }
    public String getSlot4Ability(String unit) { return getUnit(unit).getString("ability.slot_4"); }
    public List<String> getAllAbilities(String unit) {
        List<String> list = new ArrayList<>();

        list.add(getBasicAbility(unit));
        list.add(getTraitAbility(unit));
        list.add(getReactionAbility(unit));

        list.add(getSlot1Ability(unit));
        list.add(getSlot2Ability(unit));
        list.add(getSlot3Ability(unit));
        list.add(getSlot4Ability(unit));
        return list;
    }
}
