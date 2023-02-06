package game.components;

import com.github.cliftonlabs.json_simple.JsonArray;
import constants.Constants;
import game.collectibles.Gem;
import game.entity.Entity;
import game.stores.pools.AssetPool;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tile extends Component {

    public final int row;
    public final int column;
    public Entity unit;
    public final List<BufferedImage> shadows = new ArrayList<>();
    private int height = 0;
    private int path = 0;
    private int terrainId;
    private int terrain;
    private int liquidId;
    private int liquid;
    private int structureId;
    private int structure;
    private Gem gem;
    private final JsonArray representation = new JsonArray();

    public Tile(int tr, int tc) {
        row = tr;
        column = tc;
    }
    public int getPath() { return path; }
    public int getHeight() { return height; }

    public int getTerrain() { return terrain; }
    public int getLiquid() { return liquid; }
    public int getStructure() { return structure; }

    public int getLiquidId() { return liquidId; }
    public int getTerrainId() { return terrainId; }
    public int getStructureId() { return structureId; }



    public void encode(int[] encoding) {
        if (encoding.length != 5) {
            try {
                System.out.println("Unable to encode tile");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(-1);
        }

        // First number is 1, then this tile is traversable
        path = encoding[0];

        // Second number represents the tiles height
        height = encoding[1];

        // Third number represent the tile's terrain
        int value = encoding[2];
        terrain = value;
        if (path != 0) {
            terrainId = AssetPool.instance().createStaticAssetReference(Constants.FLOORS_SPRITESHEET_FILEPATH, value);
        } else {
            terrainId = AssetPool.instance().createStaticAssetReference(Constants.WALLS_SPRITESHEET_FILEPATH, value);
        }

        value = encoding[3];
        liquid = value;
        liquidId = AssetPool.instance().createAnimationViaBrighten(Constants.LIQUID_SPRITESHEET_FILEPATH, value);

        value = encoding[4];
        structure = value;
        structureId = AssetPool.instance().createAnimationViaShearing(Constants.STRUCTURE_SPRITESHEET_FILEPATH, value);


        // Refresh the representation
        representation.clear();
        representation.addChain(path)
                .addChain(height)
                .addChain(terrain)
                .addChain(liquid)
                .addChain(structure);
    }
    public JsonArray toJson() { return representation; }

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
    public boolean isPath() { return path != 0; }
    public boolean isWall() { return path == 0; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return structureId > 0 && structure > 0; }
    public boolean isStructureUnitOrWall() {
        return isWall() || isOccupied() || isStructure();
    }
    public Gem getGem() { return gem; }

    public String toString() {
        return "[row: " + row + ", column: " + column +"]";
    }

    public void setGem(Gem b) {
        gem = b;
    }
}
