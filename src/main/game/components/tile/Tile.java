package main.game.components.tile;

import main.game.main.GameState;
import main.game.stores.factories.EntityStore;
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
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String UNIT = "unit";
    public final static String STRUCTURE = "structure";
    public final static String LAYER_TYPE_BASE = "base";
    public final static String LAYER_FORM_SOLID_TERRAIN = "solid";
    public final static String LAYER_FORM_LIQUID_TERRAIN = "liquid";
    public final static String LAYER_FORM_GAS_TERRAIN = "gas";
    public final static String LAYER_TYPE = "layer";
    public final static String LAYER_ASSET = "asset";
    public final static String LAYER_HEIGHT = "thickness";
    public final static String LIQUID = "liquid";
    public final static String LAYERS = "layers";
    public final static String OBSTRUCTION = "obstruction";
    public final static String SPAWNERS = "spawn_region";
    private static final String ORIGIN_FRAME = "origin_frame";
    private static final String EMPTY_STRING = "";
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
        put(COLLIDER, EMPTY_STRING);
        put(UNIT, EMPTY_STRING);
        put(STRUCTURE, EMPTY_STRING);

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

    public int getRow() { return getInt(ROW); }
    public int getColumn() { return getInt(COLUMN); }

    public void addStructure(String structureID) { put(STRUCTURE, structureID); }
    public void deleteStructure() { put(STRUCTURE, EMPTY_STRING); }
    public String getStructureID() { return getString(STRUCTURE); }

    public void addGas(int amount, String asset) {
        addLayer(TileLayer.LAYER_STATE_GAS, amount, asset);

    }

    public void addSolid(int amount, String asset) {
        addLayer(TileLayer.LAYER_STATE_SOLID, amount, asset);
    }

    public void addLiquid(int amount, String asset) {
        addLayer(TileLayer.LAYER_STATE_LIQUID, amount, asset);
    }

    public void addLayer(String state, int amount, String asset) {

        JSONArray layers = getTileLayers();
        // There must be at least a height of 1 per layer
        amount = Math.max(amount, 1);
        TileLayer topMostLayer = getTopLayer();
        // if there is no asset, just try to extend the top most layers height
        if (asset == null && topMostLayer != null) {
            int currentThickness = topMostLayer.getThickness();
            topMostLayer.putThickness(currentThickness + amount);
        } else if (topMostLayer != null){
            // The case where we add entirely new layer if the layer is different then previous layer
            String topMostLayerType = topMostLayer.getState();
            String topMostLayerAsset = topMostLayer.getSprite();
            if (topMostLayerType.equalsIgnoreCase(state) && topMostLayerAsset.equalsIgnoreCase(asset)) {
                int topMostLayerHeight = topMostLayer.getThickness();

                topMostLayer.putState(state);
                topMostLayer.putThickness(topMostLayerHeight + amount);
                topMostLayer.putSprite(asset);
            } else {
                topMostLayer = new TileLayer(state, asset, amount);
                layers.put(topMostLayer);
            }
        } else {
            topMostLayer = new TileLayer(state, asset, amount);
            layers.put(topMostLayer);
        }
    }

    public void removeLayer() { removeLayer(-1); }
    public void removeLayer(int amount) {
        JSONArray layers = getTileLayers();
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

    private JSONArray getTileLayers() { return (JSONArray) get(LAYERS); }

    public String getCollider() { return (String) get(COLLIDER); }
    public int getHeight() {
        AtomicInteger atomicHeight = new AtomicInteger();
        JSONArray layers = getTileLayers();
        layers.forEach(e -> {
            TileLayer layer = (TileLayer) e;
            int thickness = layer.getThickness();
            atomicHeight.set(atomicHeight.get() + thickness);
        });
        return atomicHeight.get();
    }

    public String getTopLayerType() {
        TileLayer topLayer = getTopLayer();
        String asset = topLayer.getSprite();
        return asset;
    }

    public JSONArray getLayersCopy() {
        JSONArray layers = getTileLayers();
        JSONArray result = new JSONArray();
        layers.forEach(layer -> {
            JSONObject JSONObject = (JSONObject) layer;
            result.put(new JSONObject(JSONObject));
        });
        return result;
    }

    private TileLayer getTopLayer() {
        JSONArray tileLayers = getTileLayers();
        TileLayer tileLayer = null;
        if (!tileLayers.isEmpty()) {
            tileLayer = (TileLayer)  tileLayers.getJSONObject(tileLayers.length() - 1);
        }
        return tileLayer;
    }

    public boolean isTopLayerLiquid() { return getTopLayer().isLiquid(); }
    public boolean isTopLayerSolid() { return getTopLayer().isSolid(); }
    public boolean isTopLayerGas() { return getTopLayer().isGas(); }
    public String getTopLayerSprite() { return getTopLayer().getSprite(); }
//    public boolean isTopLayerBase() { return getTopLayerType().equalsIgnoreCase(LAYER_TYPE_BASE); }
//    public String getLiquid() { return (String) get(LIQUID); }
    public String getObstruction() { return (String) get(OBSTRUCTION); }
    public void clear(String key) { remove(key); }
    public void set(String key, Object value) { put(key, value); }

    public boolean isPath() { return true; }
    public boolean isWall() { return false; }
    public boolean hasUnit() { return !getUnitID().isBlank(); }
    public boolean hasStructure() { return !getStructureID().isBlank(); }
    public boolean isOccupied() { return hasUnit() || hasStructure(); }
    public void setSpawnRegion(int value) { put(SPAWNERS, value); }
    public String getSpawnRegion() { return (String) get(SPAWNERS); }
    public String getUnitID() { return getString(UNIT); }
    private void removeUnit() {
        IdentityComponent identityComponent = mOwner.get(IdentityComponent.class);
        String tileEntityID = identityComponent.getID();

        // Get previous entity and remove the references
        String currentEntityID = getUnitID();
        Entity currentEntity = EntityStore.getInstance().get(currentEntityID);
//        if (currentEntity != null) {
//            MovementComponent movementComponent = currentEntity.get(MovementComponent.class);
//            if (movementComponent != null) {
//                movementComponent.setCurrentTile("");
//            }
//            mLogger.info("Removing {} from {}.", currentEntityID, tileEntityID);
//        }

        put(UNIT, EMPTY_STRING);
    }

    public void setUnit(String unitID) {
        // Set current entity and references
        Entity currentUnit = EntityStore.getInstance().get(unitID);
        if (currentUnit != null) {
            // Remove unit from previous tile
            MovementComponent movementComponent = currentUnit.get(MovementComponent.class);
            String previousTileEntityID = movementComponent.getCurrentTileID();
            Entity previousTileEntity = EntityStore.getInstance().get(previousTileEntityID);
            if (previousTileEntity != null) {
                Tile tile = previousTileEntity.get(Tile.class);
                tile.removeUnit();
            }
            // Set the current unit's tile to this tile
            IdentityComponent identityComponent = mOwner.get(IdentityComponent.class);
            String tileEntityID = identityComponent.getID();
            movementComponent.setCurrentTile(tileEntityID);
            mLogger.info("Setting {} on to {}.", unitID, tileEntityID);
        }

        put(UNIT, unitID);
    }

//    public void setUnit(Entity unitEntity) {
//        put(UNIT, "");
//        if (unitEntity != null) {
//            IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
//            put(UNIT, identityComponent.getID());
//        }
//
//        // Ensure the current associated unit is removed
//        if (mUnit != null) {
//            // Remove the tile reference of the outgoing unit
//            Entity outgoingUnitEntity = mUnit;
//            MovementComponent movementComponent = outgoingUnitEntity.get(MovementComponent.class);
//            movementComponent.setCurrentTile(null);
//        }
//
//        mUnit = unitEntity;
//        if (mUnit == null) { return; }
//
//        // Remove the tile reference of the incoming unit
//        MovementComponent movementComponent = mUnit.get(MovementComponent.class);
//        Entity currentTileEntity = movementComponent.getCurrentTileV1();
//        if (currentTileEntity != null) {
//            Tile outgoingTileEntity = currentTileEntity.get(Tile.class);
////            outgoingTileEntity.setUnit(null);
//        }
//        movementComponent.setCurrentTile(mOwner);
//    }
    public boolean isRoughTerrain() {
        return false;
    }
    public boolean isNotNavigable() { return isWall() || isOccupied(); }
    public void setOriginFrame(int frame) { put(ORIGIN_FRAME, frame); }
    public int getOriginFrame() { return getInt(ORIGIN_FRAME); }


    public Vector3f getLocalVector(GameModel model) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        return new Vector3f(localTileX, localTileY);
    }

    public Vector3f getLocalVector(GameState gameStateV) {
        int spriteWidth = gameStateV.getSpriteWidth();
        int spriteHeight = gameStateV.getSpriteHeight();
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
