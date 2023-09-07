package main.game.components;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import designer.fundamentals.Direction;
import main.game.components.tile.Gem;
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
    public final static String GREATER_OBSTRUCT = "greater_obstruct";
    public Tile(int tileRow, int tileColumn) {
        row = tileRow;
        column = tileColumn;
        properties.putChain(PATH, -1)
                .putChain(HEIGHT, -1)
                .putChain(TERRAIN, -1)
                .putChain(LIQUID, -1)
                .putChain(GREATER_OBSTRUCT, -1)
                .putChain("exit", -1);

        assetIds.put(TERRAIN, -1);
        assetIds.put(LIQUID, -1);
        assetIds.put(GREATER_OBSTRUCT, -1);
    }

    public int getPath() { return (int) properties.get(PATH); }
    public int getHeight() { return (int) properties.get(HEIGHT); }
    public int getTerrain() { return (int) properties.get(TERRAIN); }
    public int getLiquid() { return (int) properties.get(LIQUID); }
    public int getGreaterObstruct() { return (int) properties.get(GREATER_OBSTRUCT); }
    public int getExit() { return (int) properties.get("exit"); }

    public int getLiquidAssetId() { return assetIds.get(LIQUID); }
    public int getTerrainAssetId() { return assetIds.get(TERRAIN); }
    public int getStructureAssetId() { return assetIds.get(GREATER_OBSTRUCT); }

    public void encode(int[] encoding) {
        encode(encoding[0], encoding[1], encoding[2], encoding[3], encoding[4], encoding[5]);
    }

    public void encode(int path, int height, int terrain, int liquid, int greaterObstruct, int exit) {
        // First number is 1, then this tile is traversable
        properties.put(PATH, path);

        // The Second number represents the tiles height\
        properties.put(HEIGHT, height);

        // floor or wall status is derived from path
        properties.put(TERRAIN, terrain);
        assetIds.put(TERRAIN, AssetPool.getInstance().createAsset(terrain,-1, "static"));

        // Set the tiles liquid value
        properties.put(LIQUID, liquid);
        if (liquid >= 0) {
            assetIds.put(LIQUID, AssetPool.getInstance().createAsset(liquid, -1, "flickering"));
        }

        // Set the tiles structure value
        properties.put(GREATER_OBSTRUCT, greaterObstruct);
        if (greaterObstruct >= 0) {
            assetIds.put(GREATER_OBSTRUCT, AssetPool.getInstance().createAsset(greaterObstruct, -1, "shearing"));
        }
//        data.put("exit", exit);
        if (exit != 0) {
            // ???
        }
    }

    public JsonObject toJson() { return properties; }
    public void fromJson(JsonObject object) {
        int greaterObstruct = object.getInteger(Jsoner.mintJsonKey(GREATER_OBSTRUCT, -1));
        int liquid = object.getInteger(Jsoner.mintJsonKey(LIQUID, -1));
        int terrain = object.getInteger(Jsoner.mintJsonKey(TERRAIN, -1));
        int path = object.getInteger(Jsoner.mintJsonKey(PATH, -1));
        int height = object.getInteger(Jsoner.mintJsonKey(HEIGHT, -1));
        encode(path, height, terrain, liquid, greaterObstruct, -1);
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
        Vector position = owner.get(Vector.class);
        Animation animation = unit.get(Animation.class);
        animation.set(position.x, position.y);
    }
    public boolean isPath() { return getPath() != -1; }
    public boolean isWall() { return getPath() == -1; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return getGreaterObstruct() != -1 ; }

    public void removeStructure() {
        properties.put("structure", -1);
        assetIds.remove("structure");
    }

    public boolean isObstructed() { return isWall() || isOccupied() || isStructure(); }
    public Gem getGem() { return gem; }

    public String toString() {
        return "[row: " + row + ", column: " + column +"]";
    }

    public void setGem(Gem b) {
        gem = b;
    }
    public boolean isCardinallyAdjacent(Entity entity) {
        Tile tile = entity.get(Tile.class);
        if (tile == null) { return false; }
        for (Direction direction : Direction.cardinal) {
            int adjacentRow = row + direction.y;
            int adjacentColumn = column + direction.x;
            if (tile.row != adjacentRow || tile.column != adjacentColumn) { continue; }
            return true;
        }
        return false;
    }

    public double distance(Tile target) {
        float columnDiff = Math.abs(column - target.column);
        float rowDiff = Math.abs(row - target.row);
        return Math.sqrt((columnDiff * columnDiff) + (rowDiff * rowDiff));
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
