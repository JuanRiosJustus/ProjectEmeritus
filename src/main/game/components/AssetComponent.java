package main.game.components;

import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.util.*;
import java.util.stream.Collectors;

public class AssetComponent extends Component {
    public static final String UNIT_ASSET = "UNIT_ASSET";
    public static final String DIRECTIONAL_SHADOWS_ASSET = "DIRECTIONAL_SHADOWS_ASSET";
    public static final String DEPTH_SHADOWS_ASSET = "DEPTH_SHADOWS_ASSET";
    public static final String OBSTRUCTION_ASSET = "OBSTRUCTION_ASSET";
    public static final String LIQUID_ASSET = "LIQUID_ASSET";
    public static final String TERRAIN_ASSET = "TERRAIN_ASSET";

    public AssetComponent() { }

    public String getId(String key) { return (String) get(key); }

    public List<String> getIds(String key) {
        return entrySet()
                .stream()
                .filter(stringObjectEntry -> stringObjectEntry.getKey().contains(key))
                .map(stringObjectEntry -> (String)stringObjectEntry.getValue())
                .collect(Collectors.toList());
    }
}
