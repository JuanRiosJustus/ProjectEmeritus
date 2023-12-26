package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.game.components.Animation;
import main.game.components.Component;
import main.game.components.MovementManager;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.stores.pools.AssetPool;
import main.graphics.SpriteSheet;

import java.util.*;
import java.util.stream.Collectors;

public class Tile extends Component {

    public final int row;
    public final int column;

    public Entity unit;
    private Gem gem;
    private final JsonObject mPropertyMap = new JsonObject();
    private final Map<String, String> mAssetMap = new HashMap<>();
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LIQUID = "liquid";
    public final static String SHADOW = "shadow";
    public final static String SPAWN = "Spawn";
    public final static String OBSTRUCTION = "obstruction";
    public final static String OBSTRUCTION_DESTROYABLE_BLOCKER = "destroyable_blocker_structure";
    public final static String OBSTRUCTION_ROUGH_TERRAIN = "rough_terrain_structure";
    public Tile(int tileRow, int tileColumn) {
        row = tileRow;
        column = tileColumn;
        mPropertyMap.putChain(COLLIDER, -1)
                .putChain(HEIGHT, -1)
                .putChain(TERRAIN, -1)
                .putChain(LIQUID, -1)
                .putChain(OBSTRUCTION, "");

        mAssetMap.put(TERRAIN, "");
        mAssetMap.put(LIQUID, "");
        mAssetMap.put(OBSTRUCTION, "");
    }

    public int getCollider() { return (int) mPropertyMap.get(COLLIDER); }
    public int getHeight() { return (int) mPropertyMap.get(HEIGHT); }
    public int getTerrain() { return (int) mPropertyMap.get(TERRAIN); }
    public int getLiquid() { return (int) mPropertyMap.get(LIQUID); }
    public String getObstruction() { return (String) mPropertyMap.get(OBSTRUCTION); }
    public boolean hasCollider() { return getCollider() >= 0; }
    public boolean isPath() { return !hasCollider(); }
    public boolean isWall() { return hasCollider(); }
    public boolean isOccupied() { return unit != null; }
    public void setProperty(String key, Object value) { mPropertyMap.put(key, value); }
    public String getAsset(String key) { return mAssetMap.get(key); }
    public void putAsset(String key, String id) { mAssetMap.put(key, id); }
    public Set<String> getAssets(String key) {
        return mAssetMap.keySet().stream().filter(e -> e.contains(key)).collect(Collectors.toSet());
    }

    public void encode(int[] encoding) {
        encode(encoding[0], encoding[1], encoding[2], encoding[3]);
    }

    public void encode(int path, int height, int terrain, int liquid) {
        // First number is 1, then this tile is traversable
        mPropertyMap.put(COLLIDER, path);

        // The Second number represents the tiles height\
        mPropertyMap.put(HEIGHT, height);

        // get appropriate asset sheet
        String map = AssetPool.TILES_SPRITEMAP;
        // floor or wall status is derived from path
        mPropertyMap.put(TERRAIN, terrain);
        mAssetMap.put(TERRAIN,
                AssetPool.getInstance().createAsset(map, terrain, -1, AssetPool.STATIC_ANIMATION));

        // Set the tiles liquid value
        mPropertyMap.put(LIQUID, liquid);
        if (liquid >= 0) {
            mAssetMap.put(LIQUID,
                    AssetPool.getInstance().createAsset(map, liquid, -1, AssetPool.FLICKER_ANIMATION));
        }

//        // Set the tiles structure value
//        mPropertyMap.put(OBSTRUCTION, obstruction);
//        if (obstruction >= 0) {
//            String assetName = AssetPool.getInstance().getAssetName(map, obstruction);
//            if (assetName.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
//                mAssetMap.put(OBSTRUCTION, AssetPool.getInstance().createAsset(obstruction, AssetPool.SHEARING_ANIMATION));
//            } else {
//                mAssetMap.put(OBSTRUCTION, AssetPool.getInstance().createAsset(obstruction, AssetPool.STATIC_ANIMATION));
//            }
//            obstructionAssetName = assetName;
//        }
    }

    public JsonObject toJson() { return mPropertyMap; }
    public void fromJson(JsonObject object) {
        int liquid = object.getInteger(Jsoner.mintJsonKey(LIQUID, -1));
        int terrain = object.getInteger(Jsoner.mintJsonKey(TERRAIN, -1));
        int collider = object.getInteger(Jsoner.mintJsonKey(COLLIDER, -1));
        int height = object.getInteger(Jsoner.mintJsonKey(HEIGHT, -1));
        encode(collider, height, terrain, liquid);
    }

    public void removeUnit() {
        if (unit != null) {
            MovementManager movementManager = unit.get(MovementManager.class);
            movementManager.currentTile = null;
        }
        unit = null;
    }

    public void setUnit(Entity unit) {
        MovementManager movementManager = unit.get(MovementManager.class);
        // remove the given unit from its tile and tile from the given unit
        if (movementManager.currentTile != null) {
            Tile occupying = movementManager.currentTile.get(Tile.class);
            occupying.unit = null;
            movementManager.currentTile = null;
        }
        // remove this tile from its current unit and the current unit from its til
        if (this.unit != null) {
            movementManager = this.unit.get(MovementManager.class);
            Tile occupying = movementManager.currentTile.get(Tile.class);
            occupying.unit = null;
            movementManager.currentTile = null;
        }

        // reference new unit to this tile and this tile to the new unit
        movementManager = unit.get(MovementManager.class);
        movementManager.currentTile = owner;
        this.unit = unit;

        // link the animation position to the tile
        main.game.components.Vector position = owner.get(Vector.class);
        Animation animation = unit.get(Animation.class);
        animation.set(position.x, position.y);
    }

    public void setStructure(String structure) {
        SpriteSheet spriteSheet = AssetPool.getInstance().getSpriteMap(AssetPool.TILES_SPRITEMAP).get(structure);
        int index = AssetPool.getInstance().getSpriteMap(AssetPool.TILES_SPRITEMAP).indexOf(structure);
        String name = spriteSheet.getName();


        String id = "";
        if (name.contains(OBSTRUCTION_DESTROYABLE_BLOCKER)) {
            id = AssetPool.getInstance().createAsset(name, AssetPool.SHEARING_ANIMATION);
        } else {
            id = AssetPool.getInstance().createAsset(name, AssetPool.STATIC_ANIMATION);
        }

        mAssetMap.put(OBSTRUCTION, id);
        mPropertyMap.put(OBSTRUCTION, id);
    }

    public void removeStructure() {
        mPropertyMap.put(OBSTRUCTION, "");
        mAssetMap.remove(OBSTRUCTION);
    }

    public boolean isRoughTerrain() {
        return hasObstruction();
    }

    public boolean isDestroyableBlocker() {
        return hasObstruction();
    }
    public boolean hasObstruction() {
        return !mAssetMap.getOrDefault(OBSTRUCTION, "").isBlank();
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
