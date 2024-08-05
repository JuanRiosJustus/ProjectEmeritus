package main.game.components;

import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets extends Component {
    public static final String UNIT_ASSET = "UNIT_ASSET";
    public static final String DIRECTIONAL_SHADOWS_ASSET = "DIRECTIONAL_SHADOWS_ASSET";
    public static final String DEPTH_SHADOWS_ASSET = "DEPTH_SHADOWS_ASSET";
    public static final String OBSTRUCTION_ASSET = "OBSTRUCTION_ASSET";
    public static final String LIQUID_ASSET = "LIQUID_ASSET";
    public static final String TERRAIN_ASSET = "TERRAIN_ASSET";

    private final Map<String, Asset> mAssets = new HashMap<>();
    public Assets() { }
    public Assets(String key, String id, String sheet, String sprite, int startingFrame, String animationType) {
        mAssets.put(key, new Asset(id, sheet, sprite, startingFrame, animationType));
    }

    public void put(String key, String id, String sheet, String sprite, int startingFrame, String animationType) {
        mAssets.put(key, new Asset(id, sheet, sprite, startingFrame, animationType));
    }

    public Animation getAnimation(String assetType) {
        Asset asset = mAssets.get(assetType);
        if (asset == null) { return null; }
        String assetId = asset.getId();
        return AssetPool.getInstance().getAnimationWithId(assetId);
    }

    public String getAnimationType(String assetType) {
        Asset asset = mAssets.get(assetType);
        if (asset == null) { return null; }
        return asset.getAnimationType();
    }

    public List<Animation> getAnimations(String subString) {
        List<Animation> animations = new ArrayList<>();
        for (String assetName : mAssets.keySet()) {
            if (!assetName.contains(subString)) { continue; }
            String id = mAssets.get(assetName).getId();
            Animation animation = AssetPool.getInstance().getAnimationWithId(id);;
            animations.add(animation);
        }
        return animations;
    }


}
