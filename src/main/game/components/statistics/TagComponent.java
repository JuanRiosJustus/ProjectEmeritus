package main.game.components.statistics;

import main.constants.CheckSum;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class TagComponent extends JSONObject {

    private static final String TAGS = "tags";
    private Map<String, Integer> mTags = new LinkedHashMap<>();

    private CheckSum mCheckSum = new CheckSum();
    public void addTag(String tag) {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        int currentCount = tags.optInt(tag, 0);
        int newCount = currentCount + 1;
        tags.put(tag, newCount);

//        mCheckSum.set(tag, newCount);
    }

    public void removeTag(String tag) {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        int currentCount = tags.optInt(tag, 0);
        int newCount = currentCount - 1;
        if (newCount <= 0) {
            tags.remove(tag);
        } else {
            tags.put(tag, newCount);
        }

//        mCheckSum.set(tag, newCount);
    }

    public int getTag(String tag) {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);
        return tags.optInt(tag, 0);
    }

    public Map<String, Integer> getTags() {
        JSONObject tags = optJSONObject(TAGS, new JSONObject());
        put(TAGS, tags);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (String key : tags.keySet()) {
            int value = tags.getInt(key);
            result.put(key, value);
        }
        return result;
    }

}
