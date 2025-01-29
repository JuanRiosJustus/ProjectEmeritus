package main.game.components;

import java.util.*;
import java.util.stream.Collectors;

public class AssetComponent extends Component {
    public static final String DIRECTIONAL_SHADOWS_ASSET = "DIRECTIONAL_SHADOWS_ASSET";
    public static final String DEPTH_SHADOWS_ASSET = "DEPTH_SHADOWS_ASSET";
    private static final String MAIN_ASSET = "MAIN_ASSET";

    public AssetComponent() {
        put(DEPTH_SHADOWS_ASSET, "");
        put(MAIN_ASSET, "");
    }

    public String getID(String key) { return getString(key); }
    public String getMainID() { return getString(MAIN_ASSET); }
    public void putMainID(String id) { put(MAIN_ASSET, id); }

    public List<String> getIds(String key) {
        return entrySet()
                .stream()
                .filter(stringObjectEntry -> stringObjectEntry.getKey().contains(key))
                .map(stringObjectEntry -> (String)stringObjectEntry.getValue())
                .collect(Collectors.toList());
    }
}
