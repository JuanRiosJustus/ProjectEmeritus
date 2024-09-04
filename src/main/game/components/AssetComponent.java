package main.game.components;

import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.util.*;

public class AssetComponent extends Component {
    public static final String UNIT_ASSET = "UNIT_ASSET";
    public static final String DIRECTIONAL_SHADOWS_ASSET = "DIRECTIONAL_SHADOWS_ASSET";
    public static final String DEPTH_SHADOWS_ASSET = "DEPTH_SHADOWS_ASSET";
    public static final String OBSTRUCTION_ASSET = "OBSTRUCTION_ASSET";
    public static final String LIQUID_ASSET = "LIQUID_ASSET";
    public static final String TERRAIN_ASSET = "TERRAIN_ASSET";

    private final Map<String, Asset> mAssets = new HashMap<>();
    private final Map<String, String> mIdMap = new LinkedHashMap<>();
    public AssetComponent() { }

    public void put(String key, String assetId) {
        mIdMap.put(key, assetId);
    }

    public String getAnimationTypeV2(String assetType) {
        String assetId = mIdMap.get(assetType);
        return AssetPool.getInstance().getAnimationType(assetId);
    }

    public String getId(String key) { return mIdMap.get(key); }

    public List<String> getIds(String key) {
        List<String> ids = new ArrayList<>();
        for (Map.Entry<String, String> entry : mIdMap.entrySet()) {
            if (!entry.getKey().contains(key)) { continue; }
            ids.add(entry.getValue());
        }
        return ids;
    }

    public Animation getAnimation(String assetType) {
        Asset asset = mAssets.get(assetType);
        if (asset == null) { return null; }
        String assetId = asset.getId();
        return AssetPool.getInstance().getAnimation(assetId);
    }
}
