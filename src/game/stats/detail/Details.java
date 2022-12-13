package game.stats.detail;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Details {

    private final Map<Detail, String> m_fields = new HashMap<>();

    public Details(JSONObject object) {
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String val = object.getString(key);
            m_fields.put(Detail.from(key), val);
        }
    }

    public String get(String name) {
        return m_fields.get(Detail.from(name));
    }
    public String get(Detail detail) { return m_fields.get(detail); }
}
