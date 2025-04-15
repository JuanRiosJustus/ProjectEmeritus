package main.game.components;

import org.json.JSONArray;
import org.json.JSONObject;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TileComponent extends Component {

    public int row;
    public int column;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String BASE_ELEVATION = "height";
    public final static String TERRAIN = "terrain";
    public final static String UNIT = "unit";
    public final static String STRUCTURE = "structure";
    public final static String LAYER_TYPE_BASE = "base";
    public final static String LAYER_STATE_SOLID = "solid";
    public final static String LAYER_STATE_LIQUID = "liquid";
    public final static String LAYER_STATE_GAS = "gas";
    public final static String LAYER_TYPE = "layer";
    public final static String LAYER_HEIGHT = "thickness";
    public final static String LIQUID = "liquid";
    public final static String LAYERS = "layers";
    public final static String OBSTRUCTION = "obstruction";
    public final static String SPAWNERS = "spawn_region";
    private static final String ORIGIN_FRAME = "origin_frame";
    private static final String EMPTY_STRING = "";
    private Deque<Layer> mLayers = null;

    private final static String LAYER_STATE = "state";
    private final static String LAYER_DEPTH = "thickness";
    public final static String LAYER_ASSET = "asset";

    public static class Layer {
        private String mState = null;
        private String mAsset = null;
        private int mDepth = -1;
        public Layer(String asset, String state, int depth) {
            mState = state;
            mAsset = asset;
            mDepth = depth;
        }
        public String getState() { return mState; }
        public void setState(String state) { mState = state; }
        public String getAsset() { return mAsset; }
        public void setAsset(String asset) { mAsset = asset; }
        public int getDepth() { return mDepth; }
        public void setDepth(int depth) { mDepth = depth; }
        public boolean isSolid() { return getState().equalsIgnoreCase(LAYER_STATE_SOLID); }
        public boolean isLiquid() { return getState().equalsIgnoreCase(LAYER_STATE_LIQUID); }
        public boolean isGas() { return getState().equalsIgnoreCase(LAYER_STATE_GAS); }
    }

//    public static class Builder {
//        public static Tile newTile() {
//            Tile tile = new Tile(new JSONObject());
//            return tile;
//        }
//    }


    public TileComponent(int row, int column) {
        this(new JSONObject());
        put(ROW, row);
        put(COLUMN, column);
        put(BASE_ELEVATION, 0);
    }

    public TileComponent(int row, int column, int elevation) {
        this(new JSONObject());
        put(ROW, row);
        put(COLUMN, column);
        put(BASE_ELEVATION, elevation);
    }

    public TileComponent(JSONObject jsonObject) {

        // Ensure these fields are available
        put(ROW, -1);
        put(COLUMN, -1);
        put(BASE_ELEVATION, 0);

        mLayers = new LinkedList<>();
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

    public void addGas(String asset, int amount) { addLayer(asset, LAYER_STATE_GAS, amount); }
    public void addSolid(String asset, int amount) {
        addLayer(asset, LAYER_STATE_SOLID, amount);
    }
    public void addLiquid(String asset, int amount) { addLayer(asset, LAYER_STATE_LIQUID, amount); }
    public void addLayer(String asset, String state, int depth) {
        if (asset == null || state == null || depth <= 0) { return; }

        Layer newLayer = null;
        Layer topLayer = mLayers.peek();
        // If the current top layer is the same asset and state, merge the layers
        if (topLayer != null && topLayer.getAsset().equals(asset) && topLayer.getState().equals(state)) {
            topLayer = mLayers.removeLast();
            newLayer = new Layer(asset, state, topLayer.getDepth() + depth);
        } else {
            newLayer = new Layer(asset, state, depth);
        }

        mLayers.addLast(newLayer);
    }
    public void removeLayer() { removeLayer(-1); }
    public void removeLayer(int amount) {
        for (int i = 0; i < amount; i++) {
            Layer topLayer = mLayers.peek();
            if (topLayer == null) { continue; }
            if (topLayer.getDepth() > 1) {
                topLayer.setDepth(topLayer.getDepth() - 1);
            } else {
                mLayers.removeLast();
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
    public int getLayerCount() { return mLayers.size(); }

//    public String getLayerAsset(int index) {
//        JSONArray layers = (JSONArray) get(LAYERS);
//        JSONObject layer = (JSONObject) layers.get(index);
//        String asset = (String) layer.get(LAYER_ASSET);
//        return asset;
//    }
//
//    public int getLayerHeight(int index) {
//        JSONArray layers = (JSONArray) get(LAYERS);
//        JSONObject layer = (JSONObject) layers.get(index);
////        int height = (int) layer.get(LAYER_HEIGHT);
//        int height = layer.getInt(LAYER_HEIGHT);
//        return height;
//    }
//
//    public String getLayerType(int index) {
//        JSONArray layers = (JSONArray) get(LAYERS);
//        JSONObject layer = (JSONObject) layers.get(index);
//        String type = (String) layer.get(LAYER_TYPE);
//        return type;
//    }

    private JSONObject getLayer(int index) {
        JSONArray layers = (JSONArray) get(LAYERS);

        if (index >= layers.length()) { return null; }
        return (JSONObject) layers.get(index);
    }

    public String getCollider() { return (String) get(COLLIDER); }

    public int getBaseElevation() { return getInt(BASE_ELEVATION); }
    public int getTotalElevation() { return getBaseElevation() + getModifiedElevation(); }

    public int getModifiedElevation() {
        Deque<Layer> tileLayers = mLayers;

        int sum = tileLayers.stream().mapToInt(Layer::getDepth).sum();
        return sum;
    }

    private Layer getTopLayer() { return mLayers.getLast(); }
    public boolean isTopLayerLiquid() { return getTopLayer().isLiquid(); }
    public boolean isTopLayerSolid() { return getTopLayer().isSolid(); }
    public boolean isTopLayerGas() { return getTopLayer().isGas(); }
    public String getTopLayerAsset() { return getTopLayer().getAsset(); }
    public int getTopLayerDepth() { return getTopLayer().getDepth();  }
    public String getTopLayerState() { return getTopLayer().getState(); }
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
    public String getUnitID() { return optString(UNIT, null); }
    public void removeUnit() {
        put(UNIT, EMPTY_STRING);




//        IdentityComponent identityComponent = mOwner.get(IdentityComponent.class);
//        String tileEntityID = identityComponent.getID();
//
//        // Get previous entity and remove the references
//        String currentEntityID = getUnitID();
//        Entity currentEntity = EntityStore.getInstance().get(currentEntityID);
//
//        MovementComponent movementComponent = currentEntity.get(MovementComponent.class);
//        String previousTileEntityID = movementComponent.getCurrentTileID();
//        Entity previousTileEntity = EntityStore.getInstance().get(previousTileEntityID);
//        if (previousTileEntity == null) { return; }
//        TileComponent tileComponent = currentEntity.get(TileComponent.class);
//        tileComponent.remove()
//        if (currentEntity != null) {
//            MovementComponent movementComponent = currentEntity.get(MovementComponent.class);
//            if (movementComponent != null) {
//                movementComponent.setCurrentTile("");
//            }
//            mLogger.info("Removing {} from {}.", currentEntityID, tileEntityID);
//        }

//        put(UNIT, EMPTY_STRING);
    }


    public void setUnit(String unitID) {
        // Remove unit from previous tile
        Entity currentUnit = getEntityWithID(unitID);
        MovementComponent movementComponent = currentUnit.get(MovementComponent.class);
        String previousTileEntityID = movementComponent.getCurrentTileID();
        Entity previousTileEntity = getEntityWithID(previousTileEntityID);
        if (previousTileEntity != null) {
            TileComponent tile = previousTileEntity.get(TileComponent.class);
            tile.removeUnit();
        }
        // Set the current unit's tile to this tile
//        IdentityComponent identityComponent = mOwner.get(IdentityComponent.class);
//        String tileEntityID = identityComponent.getID();
        put(UNIT, unitID);
//            movementComponent.setStageAndFinalTarget(tileEntityID);
//            movementComponent.stageTarget(tileEntityID);
        mLogger.info("Setting {} on to {}.", unitID, this);


        // Set current entity and references
//        Entity currentUnit = getEntityWithID(unitID);
////        put(UNIT, unitID);
//        if (currentUnit != null) {
//            // Remove unit from previous tile
//            MovementComponent movementComponent = currentUnit.get(MovementComponent.class);
//            String previousTileEntityID = movementComponent.getCurrentTileID();
//            Entity previousTileEntity = EntityStore.getInstance().get(previousTileEntityID);
//            if (previousTileEntity != null) {
//                TileComponent tile = previousTileEntity.get(TileComponent.class);
//                tile.removeUnit();
//            }
//            // Set the current unit's tile to this tile
//            IdentityComponent identityComponent = mOwner.get(IdentityComponent.class);
//            String tileEntityID = identityComponent.getID();
////            movementComponent.setStageAndFinalTarget(tileEntityID);
////            movementComponent.stageTarget(tileEntityID);
//            mLogger.info("Setting {} on to {}.", unitID, tileEntityID);
//        }

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

    public Vector3f getGlobalVector(GameModel model) {
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
        if (!(o instanceof TileComponent tile)) return false;
        return getRow() == tile.getRow() && getColumn() == tile.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }
}
