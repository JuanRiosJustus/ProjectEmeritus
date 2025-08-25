package main.game.components.statistics;

import com.alibaba.fastjson2.JSONObject;

import java.util.Set;

public class Statistics extends JSONObject {

    private static final String UNKNOWN_SOURCE = "unknown";
    private static final String BASES = "bases";
    private static final String BONUSES = "bonuses";
    private static final String CURRENTS = "currents";
    private JSONObject mBases = new JSONObject();
    private JSONObject mBonuses = new JSONObject();
    private JSONObject mCurrents = new JSONObject();

    public Statistics() { this(new JSONObject()); }
    public Statistics(JSONObject bases) {
        mBases = new JSONObject();
        put(BASES, mBases);

        mBonuses = new JSONObject();
        put(BONUSES, mBonuses);

        mCurrents = new JSONObject();
        put(CURRENTS, mCurrents);

        for (String key : bases.keySet()) { float value = bases.getFloat(key); setBase(key, value); }
    }

    public Set<String> getStatistics() { return mBases.keySet(); }
    public void setBase(String key, float value) { mBases.put(key, value); }
    public void setBases(JSONObject bases) {
        for (String key : bases.keySet()) { float value = bases.getFloat(key); setBase(key, value); }
    }
    public float getBase(String key) { return mBases.getFloatValue(key); }
    public void addBonus(String key, float value) { addBonus(key, value, UNKNOWN_SOURCE); }
    public void addBonus(String key, float value, String source) {
        if (!mBases.containsKey(key)) { mBases.put(key, 0f); }

        JSONObject bonusMap = mBonuses.getJSONObject(key);
        if (bonusMap == null) { bonusMap = new JSONObject(); mBonuses.put(key, bonusMap); }

        bonusMap.put(source, value);
    }

    public float getBonus(String key) { return getBonus(key, UNKNOWN_SOURCE); }
    public float getBonus(String key, String source) {
        if (!mBases.containsKey(key)) { mBases.put(key, 0f); }

        JSONObject bonusMap = mBonuses.getJSONObject(key);
        if (bonusMap == null) { bonusMap = new JSONObject(); mBonuses.put(key, bonusMap); }

        float result = bonusMap.getFloatValue(source);

        return result;
    }

    public float getBonuses(String key) {
        if (!mBases.containsKey(key)) { mBases.put(key, 0f); }

        JSONObject bonusMap = mBonuses.getJSONObject(key);
        if (bonusMap == null) { bonusMap = new JSONObject(); mBonuses.put(key, bonusMap); }

        float result = 0f;
        for (String source : bonusMap.keySet()) { float value = bonusMap.getFloatValue(source); result += value; }

        return result;
    }


    private float getTotal(String key) {
        float base = getBase(key);
        float bonuses = getBonuses(key);
        return base + bonuses;
    }

    public float getCurrent(String key) {
        boolean hasCurrent = mCurrents.containsKey(key);
        float currentTotal = getTotal(key);

        float result = 0;
        if (hasCurrent) {
            result = mCurrents.getFloat(key);
        } else {
            result = currentTotal;
        }
        return result;
    }
    public void removeFromCurrent(String key, float value) {
        float total = getTotal(key);
        if (!mCurrents.containsKey(key)) { mCurrents.put(key, total); }

        float currentCurrent = mCurrents.getFloat(key);
        float newCurrent = currentCurrent - value;

        if (newCurrent > total) {
            newCurrent = total;
        } else if (newCurrent < 0) {
            newCurrent = 0;
        }
        mCurrents.put(key, newCurrent);
    }

    public void addToCurrent(String key, float value) {
        float total = getTotal(key);
        if (!mCurrents.containsKey(key)) { mCurrents.put(key, total); }

        float currentCurrent = mCurrents.getFloat(key);
        float newCurrent = currentCurrent + value;

        if (newCurrent > total) {
            newCurrent = total;
        } else if (newCurrent < 0) {
            newCurrent = 0;
        }
        mCurrents.put(key, newCurrent);
    }


    public void setCurrent(String key, float value) {
        float total = getTotal(key);
        if (!mCurrents.containsKey(key)) { mCurrents.put(key, total); }

        float newCurrent = value;

        if (newCurrent > total) {
            newCurrent = total;
        } else if (newCurrent < 0) {
            newCurrent = 0;
        }
        mCurrents.put(key, newCurrent);
    }
}
