package main.game.stats;

import org.json.JSONObject;

public class Tag extends JSONObject {
    private static final String NAME = "name";
    private static final String DURATION = "duration";
    private static final String SOURCE = "source";
    private static final String COUNT = "count";
    private static final String AGE = "age";
    public Tag(String source, String name, int duration) {
        put(NAME, name);
        put(DURATION, duration);
        put(SOURCE, source);
        put(COUNT, 0);
        put(AGE, 0);
    }

    public String getName() { return getString(NAME); }
    public int getAge() { return getInt(AGE); }
    public int getDuration() { return getInt(DURATION); }
    public String getSource() { return getString(SOURCE); }
    public int getCount() { return getInt(COUNT); }
}
