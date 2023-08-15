package main.game.components;

import com.github.cliftonlabs.json_simple.JsonObject;
import designer.fundamentals.Direction;
import main.constants.Constants;
import main.game.components.tile.Gem;
import main.game.entity.Entity;
import main.game.stores.pools.AssetPool;

import java.awt.image.BufferedImage;
import java.util.*;

public class Tile extends Component {

    public final int row;
    public final int column;
    public Entity unit;
    private Gem gem;
    public final List<BufferedImage> shadows = new ArrayList<>();
    private final JsonObject data = new JsonObject();
    private final Map<String, Integer> referenceMap = new HashMap<>();
    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getPath() { return (int) data.get("path"); }
    public int getHeight() { return (int) data.get("height"); }
    public int getTerrain() { return (int) data.get("terrain"); }
    public int getLiquid() { return (int) data.get("liquid"); }
    public int getStructure() { return (int) data.get("structure"); }
    public int getExit() { return (int) data.get("exit"); }

    public int getLiquidId() { return referenceMap.get("liquid"); }
    public int getTerrainId() { return referenceMap.get("terrain"); }
    public int getStructureId() { return referenceMap.get("structure"); }

    public void encode(int[] encoding) {
        encode(encoding[0], encoding[1], encoding[2], encoding[3], encoding[4], encoding[5]);
    }

    public void encode(int path, int height, int terrain, int liquid, int structure, int exit) {
        // First number is 1, then this tile is traversable
        data.put("path", path);

        // Second number represents the tiles height\
        data.put("height", height);

        // floor or wall status is derived from path
        int terrainId;
        if (path != 0) {
            terrainId = AssetPool.getInstance()
                .createStaticAssetReference(Constants.FLOORS_SPRITESHEET_FILEPATH, terrain);
        } else {
            terrainId = AssetPool.getInstance()
                .createStaticAssetReference(Constants.WALLS_SPRITESHEET_FILEPATH, terrain);
        }
        data.put("terrain", terrain);
        referenceMap.put("terrain", terrainId);

        // Set the tiles liquid value
        data.put("liquid", liquid);
        if (liquid != 0) {
            int liquidId = AssetPool.getInstance()
                .createDynamicAssetReference(Constants.LIQUIDS_SPRITESHEET_FILEPATH, liquid, "flickering");
            referenceMap.put("liquid", liquidId);
        }

        // Set the tiles structure value
        data.put("structure", structure);
        if (structure != 0) {
            int structureId = AssetPool.getInstance()
                .createDynamicAssetReference(Constants.STRUCTURES_SPRITESHEET_FILEPATH, structure, "shearing");
            referenceMap.put("structure", structureId);
        }
        data.put("exit", exit);
        if (exit != 0) {
            // ???
        }
    }

    public JsonObject toJson() { return data; }

    public void removeUnit() {
        if (unit != null) {
            MovementManager movement = unit.get(MovementManager.class);
            movement.currentTile = null;
        }
        unit = null;
    }

    public void setUnit(Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        // remove the given unit from its tile and tile from the given unit
        if (movement.currentTile != null) {
            Tile occupying = movement.currentTile.get(Tile.class);
            occupying.unit = null;
            movement.currentTile = null;
        }
        // remove this tile from its current unit and the current unit from its til
        if (this.unit != null) {
            movement = this.unit.get(MovementManager.class);
            Tile occupying = movement.currentTile.get(Tile.class);
            occupying.unit = null;
            movement.currentTile = null;
        }

        // reference new unit to this tile and this tile to the new unit
        movement = unit.get(MovementManager.class);
        movement.currentTile = owner;
        this.unit = unit;

        // link the animation position to the tile
        Vector position = owner.get(Vector.class);
        Animation animation = unit.get(Animation.class);
        animation.position.copy(position);
    }
    public boolean isPath() { return getPath() != 0; }
    public boolean isWall() { return getPath() == 0; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return getStructure() > 0 ; }

    public void removeStructure() {
        data.put("structure", 0);
        referenceMap.remove("structure");
    }

    public boolean isStructureUnitOrWall() { return isWall() || isOccupied() || isStructure(); }
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
