package main.game.components.tile;

import com.alibaba.fastjson2.JSONObject;

public class Layer extends JSONObject {
    private final static String STATE = "state";
    private final static String DEPTH = "depth";
    private final static String ASSET = "asset";
    public final static String STATE_SOLID = "solid";
    public final static String STATE_LIQUID = "liquid";
    public final static String STATE_GAS = "gas";
    public Layer(String asset, String state, int depth) {
        setState(state);
        setAsset(asset);
        setDepth(depth);
    }
    public String getState() { return getString(STATE); }
    public void setState(String state) { put(STATE, state); }
    public String getAsset() { return getString(ASSET); }
    public void setAsset(String asset) { put(ASSET, asset); }
    public int getDepth() { return getInteger(DEPTH); }
    public void setDepth(int depth) { put(DEPTH, depth); }
    public boolean isSolid() { return getState().equalsIgnoreCase(STATE_SOLID); }
    public boolean isLiquid() { return getState().equalsIgnoreCase(STATE_LIQUID); }
    public boolean isGas() { return getState().equalsIgnoreCase(STATE_GAS); }
}
