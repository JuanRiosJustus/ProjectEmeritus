package main.game.components.tile;

import main.game.main.GameState;
import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Tile extends Component {

    public int row;
    public int column;

    public Entity mUnit;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LAYER_TYPE_BASE = "base";
    public final static String LAYER_TYPE_SOLID_TERRAIN = "terrain.solid";
    public final static String LAYER_TYPE_LIQUID_TERRAIN = "terrain.liquid";
    public final static String LAYER_TYPE_GAS_TERRAIN = "terrain.liquid";
    public final static String LAYER_TYPE = "layer";
    public final static String LAYER_ASSET = "asset";
    public final static String LAYER_HEIGHT = "height";
    public final static String LIQUID = "liquid";
    public final static String LAYERS = "layers";
    public final static String OBSTRUCTION = "obstruction";
    public final static String SPAWNERS = "spawn_region";
    public final static String STRUCTURE = "structure";
    public Tile(int row, int column) {
        this(new JSONObject());
        put(ROW, row);
        put(COLUMN, column);
    }

    public Tile(JSONObject jsonObject) {

        // Ensure these fields are available
        put(ROW, -1);
        put(COLUMN, -1);
        put(HEIGHT, 0);
        put(LAYERS, new JSONArray());
        put(SPAWNERS, new JSONArray());
        put(STRUCTURE, new JSONObject());
        put(COLLIDER, "");

        for (String key : jsonObject.keySet()) {
            put(key, jsonObject.get(key));
        }
    }

    /**
     *
     ___ ___ ___ ___ _  _ _____ ___   _   _
     | __/ __/ __| __| \| |_   _|_ _| /_\ | |
     | _|\__ \__ \ _|| .` | | |  | | / _ \| |__
     |___|___/___/___|_|\_| |_| |___/_/ \_\____|
     */
//    public int getRow() { return (int) getOrDefault(ROW, -1); }
//    public int getColumn() { return (int) getOrDefault(COLUMN, -1); }

    public int getRow() { return getInt(ROW); }
    public int getColumn() { return getInt(COLUMN); }

    public static final String STRUCTURE_ASSET = "asset";
    public static final String STRUCTURE_HEALTH = "health";
    public void addStructure(String asset, int health) {
        JSONObject structure = (JSONObject) get(STRUCTURE);
        structure.put(STRUCTURE_ASSET, asset);
        structure.put(STRUCTURE_HEALTH, health);
    }

    public void deleteStructure() {
        JSONObject structure = (JSONObject) get(STRUCTURE);
        structure.clear();
    }
    public String getTopStructure() {
        JSONObject structure = (JSONObject) get(STRUCTURE);
        return (String) structure.opt(STRUCTURE_ASSET);
    }

    public void addLayer(String type, int amount) { addLayer(type, amount, null); }
    public void addLayer(String type, int amount, String asset) {
        JSONArray layers = getLayers();
        // There must be at least a height of 1 per layer
        amount = Math.max(amount, 1);
        JSONObject topMostLayer = getTopLayer();
        // if there is no asset, just try to extend the top most layers height
        if (asset == null && topMostLayer != null) {
            int currentHeight = (int)topMostLayer.get(LAYER_HEIGHT);
            topMostLayer.put(LAYER_HEIGHT, currentHeight + amount);
        } else if (topMostLayer != null){
            // The case where we add entirely new layer if the layer is different then previous layer
            String topMostLayerType = (String) topMostLayer.get(LAYER_TYPE);
            String topMostLayerAsset = (String) topMostLayer.get(LAYER_ASSET);
            if (topMostLayerType.equalsIgnoreCase(type) && topMostLayerAsset.equalsIgnoreCase(asset)) {
                int topMostLayerHeight = (int) topMostLayer.get(LAYER_HEIGHT);
                topMostLayer.put(LAYER_TYPE, type);
                topMostLayer.put(LAYER_HEIGHT, topMostLayerHeight + amount);
                topMostLayer.put(LAYER_ASSET, asset);
            } else {
                topMostLayer = new JSONObject();
                topMostLayer.put(LAYER_TYPE, type);
                topMostLayer.put(LAYER_HEIGHT, amount);
                topMostLayer.put(LAYER_ASSET, asset);
                layers.put(topMostLayer);
            }
        } else {
            topMostLayer = new JSONObject();
            topMostLayer.put(LAYER_TYPE, type);
            topMostLayer.put(LAYER_HEIGHT, amount);
            topMostLayer.put(LAYER_ASSET, asset);
            layers.put(topMostLayer);
        }
    }

    public void removeLayer() { removeLayer(-1); }
    public void removeLayer(int amount) {
        JSONArray layers = getLayers();
        // Cannot remove layering if there is only 1 left
        if (layers.length() <= 1) { return; }
        if (amount <= 0) {
            // The case where we remove an entire layer, regardless of height
            layers.remove(layers.length() - 1);
        } else {
            // The case for shortening the current, top most layer. Must have at least 1 height
            JSONObject topMostLayer = getLayer(layers.length() - 1);
            int layerHeight = (int) topMostLayer.get(LAYER_HEIGHT);
            // Only shorten the layer if the height is greater than 1
            if (layerHeight > amount) {
                layerHeight -= amount;
                topMostLayer.put(LAYER_HEIGHT, layerHeight);
            }
        }
    }
    public List<String> getSpawners() {
        JSONArray spawners = (JSONArray) get(SPAWNERS);
        return spawners.toList().stream().map(e -> (String)e).toList();
    }
    public void addSpawner(String spawn) {
        JSONArray spawners = (JSONArray) get(SPAWNERS);

//        if (spawners.(spawn)) { return; }
        spawners.put(spawn);
    }
    public void deleteSpawner(String spawn) {
        JSONArray spawners = (JSONArray) get(SPAWNERS);

        int index = (int)spawners.toList().stream().filter(e -> e == spawn).findFirst().orElse(-1);
        spawners.remove(index);
    }


    // Get layers
    public int getLayerCount() {
        JSONArray layers = (JSONArray) get(LAYERS);
        return layers.length();
    }

    public String getLayerAsset(int index) {
        JSONArray layers = (JSONArray) get(LAYERS);
        JSONObject layer = (JSONObject) layers.get(index);
        String asset = (String) layer.get(LAYER_ASSET);
        return asset;
    }

    public int getLayerHeight(int index) {
        JSONArray layers = (JSONArray) get(LAYERS);
        JSONObject layer = (JSONObject) layers.get(index);
//        int height = (int) layer.get(LAYER_HEIGHT);
        int height = layer.getInt(LAYER_HEIGHT);
        return height;
    }

    public String getLayerType(int index) {
        JSONArray layers = (JSONArray) get(LAYERS);
        JSONObject layer = (JSONObject) layers.get(index);
        String type = (String) layer.get(LAYER_TYPE);
        return type;
    }

    private JSONObject getLayer(int index) {
        JSONArray layers = (JSONArray) get(LAYERS);

        if (index >= layers.length()) { return null; }
        return (JSONObject) layers.get(index);
    }

    private JSONArray getLayers() { return (JSONArray) get(LAYERS); }

    public String getCollider() { return (String) get(COLLIDER); }
    public int getHeight() {
        AtomicInteger atomicHeight = new AtomicInteger();
        JSONArray layers = getLayers();
        layers.forEach(layer -> {
            JSONObject JSONObject = (JSONObject) layer;
            int height = JSONObject.getInt(HEIGHT);
            atomicHeight.set(atomicHeight.get() + height);
        });
        return atomicHeight.get();
    }
    public String getTopLayerAsset() {
        JSONArray layers = getLayers();
        JSONObject topLayer = (JSONObject) layers.get(layers.length() - 1);
        String asset = (String) topLayer.get(LAYER_ASSET);
        return asset;
    }
    public String getTopLayerType() {
        JSONArray layers = getLayers();
        JSONObject topLayer = (JSONObject) layers.get(layers.length() - 1);
        String asset = (String) topLayer.get(LAYER_TYPE);
        return asset;
    }

    public JSONArray getLayersCopy() {
        JSONArray layers = getLayers();
        JSONArray result = new JSONArray();
        layers.forEach(layer -> {
            JSONObject JSONObject = (JSONObject) layer;
            result.put(new JSONObject(JSONObject));
        });
        return result;
    }

    private JSONObject getTopLayer() {
        JSONArray layers = getLayers();
        if (layers.isEmpty()) { return null; }
        JSONObject topLayer = layers.getJSONObject(layers.length() - 1);
        return topLayer;
    }

    public String isLiquid() {
        JSONObject topLayer = getTopLayer();
        String topLayerType = topLayer.getString(LAYER_TYPE);
        boolean topLayerIsLiquid = topLayerType.equalsIgnoreCase(LAYER_TYPE_LIQUID_TERRAIN);
        String liquid = null;
        if (topLayerIsLiquid) {
            liquid = topLayer.getString(LAYER_ASSET);
        }
        return liquid;
    }
    public boolean isTopLayerLiquid() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_LIQUID_TERRAIN); }
    public boolean isTopLayerSolid() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_SOLID_TERRAIN); }
    public boolean isTopLayerBase() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_BASE); }
    public String getLiquid() { return (String) get(LIQUID); }
    public String getObstruction() { return (String) get(OBSTRUCTION); }
    public void clear(String key) { remove(key); }
    public void set(String key, Object value) { put(key, value); }

//    public boolean isPath() { return getCollider() == null; }
//    public boolean isWall() { return getCollider() != null; }
    public boolean isPath() { return true; }
    public boolean isWall() { return false; }
    public boolean isOccupied() { return mUnit != null; }
    public void setSpawnRegion(int value) { put(SPAWNERS, value); }
    public String getSpawnRegion() { return (String) get(SPAWNERS); }
    public Entity getUnit() { return mUnit; }

    public void removeUnit() {
        if (mUnit != null) {
            MovementComponent movementComponent = mUnit.get(MovementComponent.class);
            movementComponent.mCurrentTile = null;
        }
        mUnit = null;
    }

    public void setUnit(Entity incomingUnitEntity) {
        // Ensure the current associated unit is removed
        if (mUnit != null) {
            // Remove the tile reference of the outgoing unit
            Entity outgoingUnitEntity = mUnit;
            MovementComponent movementComponent = outgoingUnitEntity.get(MovementComponent.class);
            movementComponent.setCurrentTile(null);
        }

        mUnit = incomingUnitEntity;
        if (mUnit == null) { return; }

        // Remove the tile reference of the incoming unit
        MovementComponent movementComponent = mUnit.get(MovementComponent.class);
        Entity currentTileEntity = movementComponent.getCurrentTile();
        if (currentTileEntity != null) {
            Tile outgoingTileEntity = currentTileEntity.get(Tile.class);
            outgoingTileEntity.setUnit(null);
        }
        movementComponent.setCurrentTile(mOwner);
    }

    public void removeStructure() {
        deleteStructure();
    }

    public boolean isRoughTerrain() {
        return false;
    }

    public boolean hasObstruction() { return getTopStructure() != null; }

    public boolean isNotNavigable() { return isWall() || isOccupied() || getTopStructure() != null; }
//    public Gem getGem() { return gem; }


    public Vector3f getLocalVector(GameModel model) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        return new Vector3f(localTileX, localTileY);
    }

    public Vector3f getLocalVector(GameState gameStateV2) {
        int spriteWidth = gameStateV2.getSpriteWidth();
        int spriteHeight = gameStateV2.getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        return new Vector3f(localTileX, localTileY);
    }

    public Vector3f getWorldVector(GameModel model) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
//        int globalTileX = model.getCamera().globalX(localTileX);
//        int globalTileY = model.getCamera().globalY(localTileY);
        int globalTileX = model.getGameState().getGlobalX(localTileX);
        int globalTileY = model.getGameState().getGlobalY(localTileY);
        return new Vector3f(globalTileX, globalTileY);
    }

    public String getBasicIdentityString() { return "[" + getRow() + ", " + getColumn() + "]"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile tile)) return false;
        return getRow() == tile.getRow() && getColumn() == tile.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }
}
