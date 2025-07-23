package main.game.stores;

import main.constants.EmeritusDatabase;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.logging.EmeritusLogger;

import java.util.*;

public class UnitTable {

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
            JSONArray rows = EmeritusDatabase.getInstance().execute("SELECT * FROM " + EmeritusDatabase.UNITS_DATABASE);
            for (int row = 0; row < rows.size(); row++) {
                JSONObject data = rows.getJSONObject(row);
                String unit = data.getString("unit");
                mUnitMap.put(unit, data);
            }
            logger.info("Successfully initialized {}", getClass().getSimpleName());
        } catch (Exception ex) {
            logger.info("Error parsing unit: " + ex.getMessage());
            logger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public Collection<String> getAllUnits() {
        return mUnitMap.keySet();
    }

    public JSONObject getUnit(String unit) {
        return mUnitMap.get(unit);
    }
    public JSONObject getStatistics(String unit) {
        return getUnit(unit).getJSONObject("statistics");
    }
    public JSONArray getType(String unit) {
        return getUnit(unit).getJSONArray("type");
    }
    public String getBasicAbility(String unit) {
        return getUnit(unit).getJSONObject("abilities").getString("basic");
    }
    public String getPassiveAbility(String unit) {
        return getUnit(unit).getJSONObject("abilities").getString("passive");
    }
    public JSONArray getOtherAbility(String unit) {
        return getUnit(unit).getJSONObject("abilities").getJSONArray("other");
    }

}
