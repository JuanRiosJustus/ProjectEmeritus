package main.game.components;

import java.util.*;
import java.util.stream.Collectors;

public class AssetComponent extends Component {
    private static final String MAIN_ASSET = "MAIN_ASSET";
    public AssetComponent() { put(MAIN_ASSET, ""); }
    public String getMainID() { return getString(MAIN_ASSET); }
    public void putMainID(String id) { put(MAIN_ASSET, id); }
    public void removeMainID() { remove(MAIN_ASSET); }

    private static final String SHADOWS = "SHADOWS";
    public void putShadowID(String id) { put(SHADOWS, id); }
    public String getShadowID() { return getString(SHADOWS); }

    private static final String STRUCTURE = "STRUCTURE";
    public void putStructureID(String id) { put(STRUCTURE, id); }
    public String getStructureID() { return getString(STRUCTURE); }
}
