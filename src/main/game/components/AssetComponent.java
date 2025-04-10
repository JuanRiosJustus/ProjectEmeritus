package main.game.components;

import java.util.*;
import java.util.stream.Collectors;

public class AssetComponent extends Component {
    private static final String MAIN_ASSET = "MAIN_ASSET";
    public AssetComponent() { put(MAIN_ASSET, ""); }
    public String getMainID() { return getString(MAIN_ASSET); }
    public void putMainID(String id) { put(MAIN_ASSET, id); }
}
