package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Direction;
import main.game.components.Animation;
import main.game.components.Component;
import main.game.components.MovementManager;
import main.game.components.Vector;
import main.game.entity.Entity;
import main.game.stores.pools.AssetPool;

import java.util.*;

public class Tile extends Component {

    public final int row;
    public final int column;

    public Entity unit;
    private Gem gem;
    private final JsonObject properties = new JsonObject();
    private final Map<String, Integer> assetIds = new HashMap<>();
    public final List<Integer> shadowIds = new ArrayList<>();
    public final static String PATH = "path";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LIQUID = "liquid";
    public final static String GREATER_STRUCTURE = "greater_structure";
    public final static String LESSER_STRUCTURE = "lesser_structure";
    public Tile(int tileRow, int tileColumn) {
        row = tileRow;
        column = tileColumn;
        properties.putChain(PATH, -1)
                .putChain(HEIGHT, -1)
                .putChain(TERRAIN, -1)
                .putChain(LIQUID, -1)
                .putChain(GREATER_STRUCTURE, -1)
                .putChain(LESSER_STRUCTURE, -1);

        assetIds.put(TERRAIN, -1);
        assetIds.put(LIQUID, -1);
        assetIds.put(GREATER_STRUCTURE, -1);
        assetIds.put(LESSER_STRUCTURE, -1);
    }

    public int getPath() { return (int) properties.get(PATH); }
    public int getHeight() { return (int) properties.get(HEIGHT); }
    public int getTerrain() { return (int) properties.get(TERRAIN); }
    public int getLiquid() { return (int) properties.get(LIQUID); }
    public int getGreaterStructure() { return (int) properties.get(GREATER_STRUCTURE); }
    public int getLesserStructure() { return (int) properties.get(LESSER_STRUCTURE); }
    public int getExit() { return (int) properties.get("exit"); }

    public int getLiquidAssetId() { return assetIds.get(LIQUID); }
    public int getTerrainAssetId() { return assetIds.get(TERRAIN); }
    public int getGreaterStructureAssetId() { return assetIds.get(GREATER_STRUCTURE); }
    public int getLesserStructureAssetId() { return assetIds.get(LESSER_STRUCTURE); }

    public void encode(int[] encoding) {
        encode(encoding[0], encoding[1], encoding[2], encoding[3], encoding[4], encoding[5]);
    }

    public void encode(int path, int height, int terrain, int liquid, int greaterStructure, int lesserStructure) {
        // First number is 1, then this tile is traversable
        properties.put(PATH, path);

        // The Second number represents the tiles height\
        properties.put(HEIGHT, height);

        // floor or wall status is derived from path
        properties.put(TERRAIN, terrain);
        assetIds.put(TERRAIN, AssetPool.getInstance().createAsset(terrain, AssetPool.STATIC_ANIMATION));

        // Set the tiles liquid value
        properties.put(LIQUID, liquid);
        if (liquid >= 0) {
            assetIds.put(LIQUID, AssetPool.getInstance().createAsset(liquid, AssetPool.FLICKER_ANIMATION));
        }

        // Set the tiles structure value
        properties.put(GREATER_STRUCTURE, greaterStructure);
        if (greaterStructure >= 0) {
            assetIds.put(GREATER_STRUCTURE, AssetPool.getInstance().createAsset(greaterStructure, AssetPool.SHEARING_ANIMATION));
        }

        properties.put(LESSER_STRUCTURE, lesserStructure);
        if (lesserStructure >= 0) {
            assetIds.put(LESSER_STRUCTURE, AssetPool.getInstance().createAsset(lesserStructure, AssetPool.STATIC_ANIMATION));
        }
    }

    public JsonObject toJson() { return properties; }
    public void fromJson(JsonObject object) {
        int greaterStructure = object.getInteger(Jsoner.mintJsonKey(GREATER_STRUCTURE, -1));
        int lesserStructure = object.getInteger(Jsoner.mintJsonKey(LESSER_STRUCTURE, -1));
        int liquid = object.getInteger(Jsoner.mintJsonKey(LIQUID, -1));
        int terrain = object.getInteger(Jsoner.mintJsonKey(TERRAIN, -1));
        int path = object.getInteger(Jsoner.mintJsonKey(PATH, -1));
        int height = object.getInteger(Jsoner.mintJsonKey(HEIGHT, -1));
        encode(path, height, terrain, liquid, greaterStructure, lesserStructure);
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
    public boolean isPath() { return getPath() != -1; }
    public boolean isWall() { return getPath() == -1; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return getGreaterStructure() != -1 ; }
    public boolean isGreaterStructure() { return getGreaterStructure() > -1 ; }
    public boolean isLesserStructure() { return getLesserStructure() > -1; }

    public void removeStructure() {
        properties.put(GREATER_STRUCTURE, -1);
        assetIds.remove(GREATER_STRUCTURE);
    }

    public boolean isObstructed() { return isWall() || isOccupied() || isStructure(); }
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
