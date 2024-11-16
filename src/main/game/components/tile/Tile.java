package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
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
    private Gem gem;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LAYER_TYPE_BASE = "base";
    public final static String LAYER_TYPE_TERRAIN_SOLID = "terrain.solid";
    public final static String LAYER_TYPE_TERRAIN_LIQUID = "terrain.liquid";
    public final static String LAYER_TYPE = "layer";
    public final static String LAYER_ASSET = "asset";
    public final static String LAYER_HEIGHT = "height";
    public final static String LIQUID = "liquid";
    public final static String LAYERS = "layers";
    public final static String OBSTRUCTION = "obstruction";
    public final static String SPAWNERS = "spawn_region";

    public Tile(int tileRow, int tileColumn) {
        this(tileRow, tileColumn, null, null, null, null);
    }

    public Tile(int tileRow, int tileColumn, Object collider, Object height, Object terrain, Object liquid) {
        this(new JsonObject()
                .putChain(ROW, tileRow)
                .putChain(COLUMN, tileColumn)
                .putChain(COLLIDER, collider)
                .putChain(HEIGHT, height)
                .putChain(TERRAIN, terrain)
                .putChain(LIQUID, liquid)
                .putChain(LAYERS, new JsonArray())
                .putChain(SPAWNERS, new JsonArray())
                .putChain(OBSTRUCTION, null));
    }
    public Tile(JsonObject jsonData) {
        putAll(jsonData);

        // Ensure these fields are available
        putIfAbsent(ROW, -1);
        putIfAbsent(COLUMN, -1);
        putIfAbsent(HEIGHT, 0);
        putIfAbsent(LAYERS, new JsonArray());
        putIfAbsent(SPAWNERS, new JsonArray());
//        addLayering(Tile.LAYER_TYPE_SOLID, 3, "obsidian_floor");
    }

    /**
     *
     ___ ___ ___ ___ _  _ _____ ___   _   _
     | __/ __/ __| __| \| |_   _|_ _| /_\ | |
     | _|\__ \__ \ _|| .` | | |  | | / _ \| |__
     |___|___/___/___|_|\_| |_| |___/_/ \_\____|
     */
    public int getRow() { return (int) getOrDefault(ROW, -1); }
    public int getColumn() { return (int) getOrDefault(COLUMN, -1); }
    public void addLayer(String type, int amount) { addLayer(type, amount, null); }
    public void addLayer(String type, int amount, String asset) {
        JsonArray layers = getLayers();
        amount = Math.max(amount, 1);
        JsonObject topMostLayer = null;
        if (!layers.isEmpty()) {
            topMostLayer = getLayer(layers.size() - 1);
        }
        if (asset == null && topMostLayer != null) {
            // if there is no asset, just try to extend the top most layers height
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
                topMostLayer = new JsonObject();
                topMostLayer.put(LAYER_TYPE, type);
                topMostLayer.put(LAYER_HEIGHT, amount);
                topMostLayer.put(LAYER_ASSET, asset);
                layers.add(topMostLayer);
            }
        } else {
            topMostLayer = new JsonObject();
            topMostLayer.put(LAYER_TYPE, type);
            topMostLayer.put(LAYER_HEIGHT, amount);
            topMostLayer.put(LAYER_ASSET, asset);
            layers.add(topMostLayer);
        }
    }

    public void removeLayer() { removeLayer(-1); }
    public void removeLayer(int amount) {
        JsonArray layers = getLayers();
        // Cannot remove layering if there is only 1 left
        if (layers.size() <= 1) { return; }
        if (amount <= 0) {
            // The case where we remove an entire layer, regardless of height
            layers.remove(layers.size() - 1);
        } else {
            // The case for shortening the current, top most layer. Must have at least 1 height
            JsonObject topMostLayer = getLayer(layers.size() - 1);
            int layerHeight = (int) topMostLayer.get(LAYER_HEIGHT);
            // Only shorten the layer if the height is greater than 1
            if (layerHeight > amount) {
                layerHeight -= amount;
                topMostLayer.put(LAYER_HEIGHT, layerHeight);
            }
        }
    }
    public List<String> getSpawners() {
        JsonArray spawners = (JsonArray) get(SPAWNERS);
        return spawners.stream().map(e -> (String)e).toList();
    }
    public void addSpawner(String spawn) {
        JsonArray spawners = (JsonArray) get(SPAWNERS);

        if (spawners.contains(spawn)) { return; }
        spawners.add(spawn);
    }
    public void deleteSpawner(String spawn) {
        JsonArray spawners = (JsonArray) get(SPAWNERS);

        spawners.remove(spawn);
    }


    // Get layers
    public int getLayerCount() {
        JsonArray layers = (JsonArray) get(LAYERS);
        return layers.size();
    }

    public String getLayerAsset(int index) {
        JsonArray layers = (JsonArray) get(LAYERS);
        JsonObject layer = (JsonObject) layers.get(index);
        String asset = (String) layer.get(LAYER_ASSET);
        return asset;
    }

    public int getLayerHeight(int index) {
        JsonArray layers = (JsonArray) get(LAYERS);
        JsonObject layer = (JsonObject) layers.get(index);
        int height = (int) layer.get(LAYER_HEIGHT);
        return height;
    }

    public String getLayerType(int index) {
        JsonArray layers = (JsonArray) get(LAYERS);
        JsonObject layer = (JsonObject) layers.get(index);
        String type = (String) layer.get(LAYER_TYPE);
        return type;
    }

    private JsonObject getLayer(int index) {
        JsonArray layers = (JsonArray) get(LAYERS);

        if (index >= layers.size()) { return null; }
        return (JsonObject) layers.get(index);
    }

    private JsonArray getLayers() { return (JsonArray) get(LAYERS); }

    public String getCollider() { return (String) get(COLLIDER); }
    public int getHeight() {
        AtomicInteger atomicHeight = new AtomicInteger();
        JsonArray layers = getLayers();
        layers.forEach(layer -> {
            JsonObject jsonObject = (JsonObject) layer;
            int height = (int) jsonObject.get(HEIGHT);
            atomicHeight.set(atomicHeight.get() + height);
        });
        return atomicHeight.get();
    }
    public String getTopLayerAsset() {
        JsonArray layers = getLayers();
        JsonObject topLayer = (JsonObject) layers.get(layers.size() - 1);
        String asset = (String) topLayer.get(LAYER_ASSET);
        return asset;
    }
    public String getTopLayerType() {
        JsonArray layers = getLayers();
        JsonObject topLayer = (JsonObject) layers.get(layers.size() - 1);
        String asset = (String) topLayer.get(LAYER_TYPE);
        return asset;
    }

    public JsonArray getLayersCopy() {
        JsonArray layers = getLayers();
        JsonArray result = new JsonArray();
        layers.forEach(layer -> {
            JsonObject jsonObject = (JsonObject) layer;
            result.add(new JsonObject(jsonObject));
        });
        return result;
    }
    public boolean isTopLayerLiquid() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_TERRAIN_LIQUID); }
    public boolean isTopLayerSolid() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_TERRAIN_SOLID); }
    public boolean isTopLayerBase() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_BASE); }
    public String getLiquid() { return (String) get(LIQUID); }
    public String getObstruction() { return (String) get(OBSTRUCTION); }

    public void clear(String key) { put(key, null); }
    public void set(String key, Object value) { put(key, value); }

    public boolean isPath() { return getCollider() == null; }
    public boolean isWall() { return getCollider() != null; }
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
        put(OBSTRUCTION, "");
    }

    public boolean isRoughTerrain() {
        return false;
    }

    public boolean isDestroyableBlocker() {
        return hasObstruction();
    }

    public boolean hasObstruction() {
        return get(OBSTRUCTION) != null;
    }

    public boolean isNotNavigable() {
        return isWall() || isOccupied() || hasObstruction();
    }
    public Gem getGem() { return gem; }

    public String toString() {
        return "[row: " + getRow() + ", column: " + getColumn() +"]";
    }


    public Vector3f getLocalVector(GameModel model) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        return new Vector3f(localTileX, localTileY);
    }

    public Vector3f getWorldVector(GameModel model) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        int globalTileX = model.getCamera().globalX(localTileX);
        int globalTileY = model.getCamera().globalY(localTileY);
        return new Vector3f(globalTileX, globalTileY);
    }

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
