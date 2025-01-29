package main.game.components.tile;

import main.game.components.Component;

public class TileLayer extends Component {

    private final static String LAYER_TYPE = "layer";
    private final static String LAYER_SPRITE = "sprite";
    private final static String LAYER_THICKNESS = "thickness";
    private final static String LAYER_ASSET_ID = "id";

    public TileLayer(String type, String asset, int thickness) {

        put(LAYER_TYPE, type);
        put(LAYER_SPRITE, asset);
        put(LAYER_THICKNESS, thickness);
    }

    public String getLayerType() { return getString(LAYER_TYPE); }
    public void putLayerType(String type) { put(LAYER_TYPE, type); }
    public String getLayerSprite() { return getString(LAYER_SPRITE); }
    public void putLayerSprite(String sprite) { put(LAYER_SPRITE, sprite); }
    public int getLayerThickness() { return getInt(LAYER_THICKNESS); }
    public void putLayerThickness(int thickness) { put(LAYER_THICKNESS, thickness); }

    public String getLayerAssetId() { return getString(LAYER_ASSET_ID); }
    public void setLayerAssetId(String id) { put(LAYER_ASSET_ID, id); }
}
