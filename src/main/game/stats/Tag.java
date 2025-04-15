package main.game.stats;

import org.json.JSONObject;

public class Tag {
    private String mSource = "";
    private String mName = "";
    private int mDuration = 0;
    private int mAge = 0;
    public Tag(String name, int duration) { this(name, "", duration); }
    public Tag(String name, String source, int duration) {
        mName = name;
        mDuration = duration;
    }

    public String getName() { return mName; }
    public int getDuration() { return mDuration; }
}
