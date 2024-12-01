package main.game.stats;

import org.json.JSONObject;

public class Modification extends JSONObject {
    public static final String MODIFICATION_SOURCE = "Source";
    public static final String MODIFICATION_VALUE = "Value";
    private static final String MODIFICATION_DURATION = "Duration";
    private static final String MODIFICATION_NAME = "Name";
    public Modification(String source, String name, float value, int duration) {
        put(MODIFICATION_SOURCE, source);
        put(MODIFICATION_NAME, name);
        put(MODIFICATION_VALUE, value);
        put(MODIFICATION_DURATION, duration);
    }

    public String getSource() { return getString(MODIFICATION_SOURCE); }
    public float getValue() { return getFloat(MODIFICATION_VALUE); }
    public String getName() { return getString(MODIFICATION_NAME); }
    public int getDuration() { return getInt(MODIFICATION_DURATION); }

    public void putSource(String source) { put(MODIFICATION_SOURCE, source); }
    public void putValue(float value) { put(MODIFICATION_VALUE, value); }
    public void putName(String name) { put(MODIFICATION_NAME, name); }
    public void putDuration(int duration) { put(MODIFICATION_DURATION, duration); }
}