package main.game.components.tile;

import main.constants.json.JSONStack;
import main.game.components.Component;
import main.game.components.MovementComponent;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class TileComponent extends Component {

    public int row;
    public int column;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String BASE_ELEVATION = "base_elevation";
    public final static String TOTAL_ELEVATION = "total_elevation";
    public final static String TERRAIN = "terrain";
    public final static String UNIT = "unit";
    public final static String STRUCTURE = "structure";
    public final static String LAYER_TYPE_BASE = "base";
    public final static String LAYER_TYPE = "layer";
    public final static String LAYER_HEIGHT = "thickness";
    public final static String LIQUID = "liquid";
    public final static String LAYERS = "layers";
    public final static String OBSTRUCTION = "obstruction";
    public final static String SPAWN_REGION = "spawn_region";
    private static final String ORIGIN_FRAME = "origin_frame";
    private static final String EMPTY_STRING = "";
    private JSONStack mLayers = null;


    public TileComponent(int row, int column) { this(row, column, 0); }

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

        mLayers = new JSONStack();
        put(SPAWN_REGION, null);
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

    public int getRow() { return getIntValue(ROW); }
    public int getColumn() { return getIntValue(COLUMN); }
    public void addStructure(String structureID) { put(STRUCTURE, structureID); }
    public void deleteStructure() { put(STRUCTURE, EMPTY_STRING); }
    public String getStructureID() { return getString(STRUCTURE); }


    public void addGas(String asset, int amount) { addLayer(asset, Layer.STATE_GAS, amount); }
    public void addSolid(String asset, int amount) {
        addLayer(asset, Layer.STATE_SOLID, amount);
    }
    public void addLiquid(String asset, int amount) { addLayer(asset, Layer.STATE_LIQUID, amount); }
    public void addLayer(String asset, String state, int depth) {
        if (asset == null || state == null || depth <= 0) { return; }

        Layer newLayer = null;
        Layer topLayer = (Layer) mLayers.peek();
        // If the current top layer is the same asset and state, merge the layers
        if (topLayer != null && topLayer.getAsset().equals(asset) && topLayer.getState().equals(state)) {
            topLayer = (Layer) mLayers.pop();
            newLayer = new Layer(asset, state, topLayer.getDepth() + depth);
        } else {
            newLayer = new Layer(asset, state, depth);
        }

        mLayers.push(newLayer);
        put(TOTAL_ELEVATION, getTotalElevation());
    }
    public void removeLayer() { removeLayer(-1); }
    public void removeLayer(int amount) {
        for (int i = 0; i < amount; i++) {
            Layer topLayer = (Layer) mLayers.peek();
            if (topLayer == null) { continue; }
            if (topLayer.getDepth() > 1) {
                topLayer.setDepth(topLayer.getDepth() - 1);
            } else {
                mLayers.pop();
            }
        }
    }

    public JSONObject getStructure() {
        JSONObject structure = new JSONObject();
        structure.put("asset", getStructureID());
        return structure;
    }
    public JSONArray getLayers() {
        JSONArray layering = new JSONArray();
        int range = getBaseElevation();
        for (JSONObject raw : mLayers) {
            Layer layer = (Layer) raw;
            JSONObject obj = new JSONObject();
            obj.put("asset", layer.getAsset());
            obj.put("state", layer.getState());
            obj.put("depth", layer.getDepth());
            obj.put("lowest", range);
            range += layer.getDepth();
            obj.put("highest", range);
            range += 1;
            layering.add(obj);
        }
        return layering;
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



    public String getCollider() { return (String) get(COLLIDER); }

    public int getBaseElevation() { return getIntValue(BASE_ELEVATION); }
    public int getTotalElevation() { return getBaseElevation() + getModifiedElevation(); }

    public int getModifiedElevation() {
        int sum = 0;
        for (JSONObject raw : mLayers) {
            Layer layer = (Layer) raw;
            sum += layer.getDepth();
        }
        return sum;
    }

    public boolean hasNoLayers() { return mLayers.isEmpty(); }
    private Layer getTopLayer() { return (Layer) mLayers.peek(); }
    public boolean isTopLayerLiquid() { if (getTopLayer() == null) { return false; } return getTopLayer().isLiquid(); }
    public boolean isTopLayerSolid() { if (getTopLayer() == null) { return false; } return getTopLayer().isSolid(); }
    public boolean isTopLayerGas() { if (getTopLayer() == null) { return false; } return getTopLayer().isGas(); }
    public String getTopLayerAsset() { if (getTopLayer() == null) { return null; } return getTopLayer().getAsset(); }
    public int getTopLayerDepth() { if (getTopLayer() == null) { return -1; } return getTopLayer().getDepth();  }
    public String getTopLayerState() { if (getTopLayer() == null) { return null; } return getTopLayer().getState(); }

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
    public void setSpawnRegion(Object value) { put(SPAWN_REGION, String.valueOf(value)); }
    public String getSpawnRegion() { return getString(SPAWN_REGION); }
    public String getUnitID() { return getString(UNIT); }
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
    public int getOriginFrame() { return getIntValue(ORIGIN_FRAME); }


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
