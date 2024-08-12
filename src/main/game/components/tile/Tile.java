package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Settings;
import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.map.base.TileMap;
import main.game.stores.pools.asset.AssetPool;

import java.util.*;
import java.util.stream.Collectors;

public class Tile extends Component {

    public static final String SHADOW_COUNT = "shadow_count";
    public final int row;
    public final int column;

    public Entity mUnit;
    private Gem gem;
    private TileMap mTileMap = null;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LIQUID = "liquid";
    public final static String CARDINAL_SHADOW = "cardinal_shadows";
    public final static String DEPTH_SHADOWS = "depth_shadows";
    public final static String OBSTRUCTION = "obstruction";
    public final static String OBSTRUCTION_DESTROYABLE_BLOCKER = "destroyable_blocker_structure";
    public final static String OBSTRUCTION_ROUGH_TERRAIN = "rough_terrain_structure";
    public final static String SPAWN_REGION = "spawn_region";
    protected static final String
            COLLIDER_LAYER = "collider_layer",
            HEIGHT_LAYER = "height_layer",
            LIQUID_LAYER = "liquid_layer",
            TERRAIN_LAYER = "terrain_layer";

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
                .putChain(OBSTRUCTION, null)
                .putChain(SPAWN_REGION, null));
    }
    public Tile(JsonObject jsonData) {
        row = (int) jsonData.get("row");
        column = (int) jsonData.get("column");
        mJsonData.putAll(jsonData);
    }

    /**
     *
     ___ ___ ___ ___ _  _ _____ ___   _   _
     | __/ __/ __| __| \| |_   _|_ _| /_\ | |
     | _|\__ \__ \ _|| .` | | |  | | / _ \| |__
     |___|___/___/___|_|\_| |_| |___/_/ \_\____|
     */
    public int getRow() { return (int) mJsonData.get(ROW); }
    public int getColumn() { return (int) mJsonData.get(COLUMN); }
    public String getCollider() { return (String) mJsonData.get(COLLIDER); }
    public int getHeight() { return getInt(Tile.HEIGHT); }
    public String getTerrain() { return (String) mJsonData.get(TERRAIN); }
    public String getLiquid() { return (String) mJsonData.get(LIQUID); }
    public String getObstruction() { return (String) mJsonData.get(OBSTRUCTION); }

    public void clear(String key) { mJsonData.put(key, null); }
    public Object get(String key) { return mJsonData.get(key); }
    public void set(String key, Object value) { mJsonData.put(key, value); }
    public int getInt(String key) { return (int) get(key); }

    public boolean isPath() { return getCollider() == null; }
    public boolean isWall() { return getCollider() != null; }
    public boolean isOccupied() { return mUnit != null; }
    public void setSpawnRegion(int value) { mJsonData.put(SPAWN_REGION, value); }
    public String getSpawnRegion() { return (String) mJsonData.get(SPAWN_REGION); }

    public Entity getUnit() { return mUnit; }
    public void setTileMap(TileMap tileMap) { mTileMap = tileMap; }

    public void encode(String collider, String height, String terrain, String liquid, String obstruct) {
        // 1.) First number is 1, then this tile is traversable
        mJsonData.put(COLLIDER, collider);

        // 2.) Second number represents the tile height
        mJsonData.put(HEIGHT, height);

        mJsonData.put(TERRAIN, terrain);

        // Set the tiles liquid value
        mJsonData.put(LIQUID, liquid);

        // Set the tiles obstruction value
        mJsonData.put(OBSTRUCTION, obstruct);
    }

//    public void encode(String collider, String height, String terrain, String liquid, String obstruction) {
//        // 1.) First number is 1, then this tile is traversable
//        mJsonData.put(COLLIDER, collider);
//
//        // 2.) Second number represents the tile height
//        mJsonData.put(HEIGHT, height);
//
//        // get appropriate asset sheet
//        String map = AssetPool.TILES_SPRITEMAP;
//
//        mJsonData.put(TERRAIN, terrain);
//        // There is always a terrain asset needed
//        String id = AssetPool.getInstance().createAsset(map, terrain, -1, AssetPool.STATIC_ANIMATION);
//        mAssetMap.put(TERRAIN, id);
//
//        // Set the tiles liquid value
//        mJsonData.put(LIQUID, liquid);
//        if (liquid != null) {
//            id = AssetPool.getInstance().createAsset(map, liquid, -1, AssetPool.FLICKER_ANIMATION);
//            mAssetMap.put(LIQUID, id);
//        }
//
//        // Set the tiles obstruction value
//        mJsonData.put(OBSTRUCTION, obstruction);
//        if (obstruction != null) {
//            if (obstruction.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
//                id = AssetPool.getInstance().createAsset(map, obstruction, -1, AssetPool.SHEARING_ANIMATION);
//            } else {
//                id = AssetPool.getInstance().createAsset(map, obstruction, -1, AssetPool.STATIC_ANIMATION);
//            }
//            mAssetMap.put(OBSTRUCTION, id);
//        }
//    }

    public JsonObject asJson() { return mJsonData; }

    public void removeUnit() {
        if (mUnit != null) {
            MovementManager movementManager = mUnit.get(MovementManager.class);
            movementManager.currentTile = null;
        }
        mUnit = null;
    }

    public void setUnit(Entity unit) {
        MovementManager movementManager = unit.get(MovementManager.class);
        // remove the given unit from its tile and tile from the given unit
        if (movementManager.currentTile != null) {
            Tile occupying = movementManager.currentTile.get(Tile.class);
            occupying.mUnit = null;
            movementManager.currentTile = null;
        }
        // remove this tile from its current unit and the current unit from its til
        if (this.mUnit != null) {
            movementManager = this.mUnit.get(MovementManager.class);
            Tile occupying = movementManager.currentTile.get(Tile.class);
            occupying.mUnit = null;
            movementManager.currentTile = null;
        }

        // reference new unit to this tile and this tile to the new unit
        movementManager = unit.get(MovementManager.class);
        movementManager.currentTile = mOwner;
        this.mUnit = unit;
    }


//    public void setObstruction(String structure) {
//        SpriteSheet spriteSheet = AssetPool.getInstance().getSpriteMap(AssetPool.TILES_SPRITEMAP).get(structure);
//        String name = spriteSheet.getName();
//
//
//        // get appropriate asset sheet
//        String map = AssetPool.TILES_SPRITEMAP;
//
//        String id;
//        if (name.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
//            id = AssetPool.getInstance().createAsset(name, AssetPool.SHEARING_ANIMATION);
//        } else {
//            id = AssetPool.getInstance().createAsset(name, AssetPool.STATIC_ANIMATION);
//        }
//
//        mAssetMap.put(OBSTRUCTION, id);
//        mJsonData.put(OBSTRUCTION, id);
////        mJsonData.put(LIQUID, liquid);
////        if (liquid != null) {
////            id = AssetPool.getInstance().createAsset(map, liquid, -1, AssetPool.FLICKER_ANIMATION);
////            mAssetMap.put(LIQUID, id);
////        }
//    }

    public void removeStructure() {
        mJsonData.put(OBSTRUCTION, "");
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
        return "[row: " + row + ", column: " + column +"]";
    }

    public void setGem(Gem b) {
        gem = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile tile)) return false;
        return row == tile.row && column == tile.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
