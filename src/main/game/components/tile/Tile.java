package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Settings;
import main.game.components.*;
import main.game.components.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.map.base.TileMap;
import main.game.stores.pools.asset.AssetPool;

import java.util.*;
import java.util.stream.Collectors;

public class Tile extends Component {

    public final int row;
    public final int column;

    public Entity mUnit;
    private Gem gem;
    private final Map<String, String> mAssetMap = new HashMap<>();
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

    public Tile(int tileRow, int tileColumn) {
        this(tileRow, tileColumn, null, null, null, null);
    }

    public Tile(int tileRow, int tileColumn, String collider, String height, String terrain, String liquid) {
        row = tileRow;
        column = tileColumn;
        mJsonData
                .putChain(ROW, tileRow)
                .putChain(COLUMN, tileColumn)
                .putChain(COLLIDER, collider)
                .putChain(HEIGHT, height)
                .putChain(TERRAIN, terrain)
                .putChain(LIQUID, liquid)
                .putChain(OBSTRUCTION, null)
                .putChain(SPAWN_REGION, -1);

        mAssetMap.put(TERRAIN, null);
        mAssetMap.put(LIQUID, null);
        mAssetMap.put(OBSTRUCTION, null);
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
    public int getHeight() { return Integer.parseInt((String) mJsonData.get(HEIGHT)); }
    public String getTerrain() { return (String) mJsonData.get(TERRAIN); }
    public String getLiquid() { return (String) mJsonData.get(LIQUID); }
    public String getObstruction() { return (String) mJsonData.get(OBSTRUCTION); }

    public void reset() {
        mAssetMap.clear();
        mAssetMap.put(TERRAIN, null);
        mAssetMap.put(LIQUID, null);
        mAssetMap.put(OBSTRUCTION, null);
    }

    public boolean isPath() { return getCollider() == null; }
    public boolean isWall() { return getCollider() != null; }
    public boolean isOccupied() { return mUnit != null; }
    public void setSpawnRegion(int value) { mJsonData.put(SPAWN_REGION, value); }
    public int getSpawnRegion() { return (int) mJsonData.get(SPAWN_REGION); }

    public String getAsset(String key) { return getAsset(null, key); }

    public String getAsset(GameModel model, String key) {
        String spriteMap = AssetPool.TILES_SPRITEMAP;
        String tileData = (String) mJsonData.get(key);
        String id;
        switch (key) {
            case TERRAIN -> {
                // Every tile must have a terrain
                id = mAssetMap.get(key);
//                String terrain = (String) mJsonData.get(key);
                if (id == null && tileData != null) {
                    id = AssetPool.getInstance().getOrCreateAsset(
                            AssetPool.getInstance().createID(
                                    spriteMap,
                                    tileData,
                                    AssetPool.STATIC_ANIMATION,
                                    String.valueOf(mJsonData.hashCode())
                            ),
                            spriteMap,
                            tileData,
                            AssetPool.STATIC_ANIMATION
                    );
                    mAssetMap.put(key, id);
                }
            }
            case LIQUID -> {
                // Optional
                id = mAssetMap.get(key);
//                String liquid = (String) mJsonData.get(key);
                if (id == null && tileData != null ) {
                    id = AssetPool.getInstance().getOrCreateAsset(
                            AssetPool.getInstance().createID(
                                    spriteMap,
                                    tileData,
                                    AssetPool.FLICKER_ANIMATION,
                                    String.valueOf(mJsonData.hashCode())
                            ),
                            spriteMap,
                            tileData,
                            AssetPool.FLICKER_ANIMATION
                    );
                    mAssetMap.put(key, id);
                }
            }
            case OBSTRUCTION -> {
                // Optional
                id = mAssetMap.get(key);
                String obstruction = (String) mJsonData.get(key);
                if (id == null && obstruction != null ) {
                    if (obstruction.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
                        id = AssetPool.getInstance().getOrCreateAsset(id, spriteMap, obstruction,  AssetPool.SHEARING_ANIMATION);
                    } else {
                        id = AssetPool.getInstance().getOrCreateAsset(id, spriteMap, obstruction, AssetPool.STATIC_ANIMATION);
                    }
                    mAssetMap.put(key, id);
                }
            }
            case CARDINAL_SHADOW -> {
                // Create cardinal shadows, NOT OPTIONAL
                id = mAssetMap.get(key);
//                boolean noAssetFound = AssetPool.getInstance().getAsset(id) == null;
//                boolean hasNoCardinalShadows = mJsonData.containsKey("NO CARDINAL SHADOWS");
//                if (id == null && noAssetFound && !hasNoCardinalShadows) {
//                    // if weve created this asset b
//                    List<String> cardinalShadows = mJsonData.keySet().stream()
//                            .filter(e -> e.contains(key))
//                            .map(e -> e.replaceAll(key, "").trim())
//                            .toList();
//                    if (cardinalShadows.isEmpty()) {
//                        mJsonData.put("NO CARDINAL SHADOWS", "");
//                    }
//                    List<String> assetIds = new ArrayList<>();
//                    for (String cardinalShadow : cardinalShadows) {
//                        int index = Direction.valueOf(cardinalShadow).ordinal();
//                        String assetId = AssetPool.getInstance()
//                                .getOrCreateAsset(cardinalShadow, AssetPool.MISC_SPRITEMAP, key, index, AssetPool.STATIC_ANIMATION);
//                        assetIds.add(assetId);
//                    }
//                    String mergedAsset = AssetPool.getInstance().mergeAssets(assetIds);
//                    mAssetMap.put(key, mergedAsset);
//                }
            }
            case DEPTH_SHADOWS -> {
                id = mAssetMap.get(key);
//                if (id == null) {
//                    int depth = (int) mJsonData.get(key);
//                    String newId = AssetPool.getInstance()
//                            .getOrCreateAsset(id, AssetPool.MISC_SPRITEMAP, key, depth, AssetPool.STATIC_ANIMATION);
//                    mAssetMap.put(key, newId);
//                }

//                int height = tile.getHeight();
//                int mapped = (int) MathUtils.map(height, min, max, 9, 0); // lights depth is first
//                String id = AssetPool.getInstance()
//                        .createAsset(AssetPool.MISC_SPRITEMAP, Tile.DEPTH_SHADOWS, mapped, AssetPool.STATIC_ANIMATION);
//                tile.putAsset(Tile.DEPTH_SHADOWS, id);
//                tile.putProperty(Tile.DEPTH_SHADOWS, );
//                id = mAssetMap.get(key);
//                String depthShadow = (String) mJsonData.get(key);
//                if (id == null) {
//                    int height = getHeight();
//                    int mapped = (int) MathUtils.map(height, min, max, 9, 0);
//                }
            }
        }
        return mAssetMap.get(key);
    }
    public void putAsset(String key, String id) { mAssetMap.put(key, id); }
    public Set<String> getAssets(String key) {
        return mAssetMap.keySet().stream().filter(e -> e.contains(key)).collect(Collectors.toSet());
    }
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
//    public void fromJson(JsonObject object) {
//        int liquid = object.getInteger(Jsoner.mintJsonKey(LIQUID, -1));
//        int terrain = object.getInteger(Jsoner.mintJsonKey(TERRAIN, -1));
//        int collider = object.getInteger(Jsoner.mintJsonKey(IS_PATH, -1));
//        int height = object.getInteger(Jsoner.mintJsonKey(HEIGHT, -1));
//        encode(collider, height, terrain, liquid);
//    }

    public void fromJson(JsonObject object) {
        String collider = object.getString(Jsoner.mintJsonKey(COLLIDER, null));
        String height = object.getString(Jsoner.mintJsonKey(HEIGHT, null));
        String terrain = object.getString(Jsoner.mintJsonKey(TERRAIN, null));
        String liquid = object.getString(Jsoner.mintJsonKey(LIQUID, null));
        String obstruction = object.getString(Jsoner.mintJsonKey(OBSTRUCTION, null));
        encode(collider, height, terrain, liquid, obstruction);
    }

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

        // link the animation position to the tile
        Animation animation = unit.get(Animation.class);
//        animation.set(position.x, position.y);
        int tileWidth = Settings.getInstance().getSpriteWidth();
        int tileHeight = Settings.getInstance().getSpriteHeight();
        animation.set(column * tileWidth, row * tileHeight);
    }

    public void setObstruction(String obstruction) {
        // get appropriate asset sheet
        String map = AssetPool.TILES_SPRITEMAP;

        String id;
        if (obstruction.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
            id = AssetPool.getInstance().createAsset(map, obstruction, -1, AssetPool.SHEARING_ANIMATION);
        } else {
            id = AssetPool.getInstance().createAsset(map, obstruction, -1, AssetPool.STATIC_ANIMATION);
        }

        mAssetMap.put(OBSTRUCTION, id);
        mJsonData.put(OBSTRUCTION, obstruction);
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
        mAssetMap.remove(OBSTRUCTION);
    }

    public boolean isRoughTerrain() {
        return hasObstruction();
    }

    public boolean isDestroyableBlocker() {
        return hasObstruction();
    }

    public boolean hasObstruction() {
        return mAssetMap.getOrDefault(OBSTRUCTION, null) != null;
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

    @Override
    public JsonObject toJsonObject(JsonObject toWriteTo) {
        return null;
    }
}
