package main.game.components.tile;

import main.game.components.Component;

public class TileLayer extends Component {

    private final static String LAYER_STATE = "state";
    private final static String LAYER_SPRITE = "sprite";
    private final static String LAYER_THICKNESS = "thickness";
    private final static String LAYER_ASSET_ID = "id";
    public static final String LAYER_STATE_SOLID = "solid";
    public static final String LAYER_STATE_LIQUID = "liquid";
    public static final String LAYER_STATE_GAS = "gas";

    public TileLayer(String state, String asset, int thickness) {
        put(LAYER_STATE, state);
        put(LAYER_SPRITE, asset);
        put(LAYER_THICKNESS, thickness);
    }

    public String getState() { return getString(LAYER_STATE); }
    public void putState(String state) { put(LAYER_STATE, state); }
    public boolean isSolid() { return getString(LAYER_STATE).equalsIgnoreCase(LAYER_STATE_SOLID); }
    public boolean isLiquid() { return getString(LAYER_STATE).equalsIgnoreCase(LAYER_STATE_LIQUID); }
    public boolean isGas() { return getString(LAYER_STATE).equalsIgnoreCase(LAYER_STATE_GAS); }
    public String getSprite() { return getString(LAYER_SPRITE); }
    public void putSprite(String sprite) { put(LAYER_SPRITE, sprite); }
    public int getThickness() { return getInt(LAYER_THICKNESS); }
    public void putThickness(int thickness) { put(LAYER_THICKNESS, thickness); }

    public String getLayerAssetId() { return getString(LAYER_ASSET_ID); }
    public void setLayerAssetId(String id) { put(LAYER_ASSET_ID, id); }
}
