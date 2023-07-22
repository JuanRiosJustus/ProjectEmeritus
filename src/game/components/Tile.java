package game.components;

import com.github.cliftonlabs.json_simple.JsonArray;
import constants.Constants;
import game.collectibles.Gem;
import game.entity.Entity;
import game.stores.pools.AssetPool;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tile extends Component {

    public final int row;
    public final int column;
    public Entity unit;
    private Gem gem;
    public final List<BufferedImage> shadows = new ArrayList<>();
    private final JsonArray representation = new JsonArray();
    private final Map<String, Integer> levelMap = new HashMap<>();
    private final Map<String, Integer> referenceMap = new HashMap<>();

    public Tile(int tr, int tc) {
        row = tr;
        column = tc;
    }

    public int getPath() { return levelMap.get("path"); }
    public int getHeight() { return levelMap.get("height"); }
    public int getTerrain() { return levelMap.get("terrain"); }
    public int getLiquid() { return levelMap.get("liquid"); }
    public int getStructure() { return levelMap.get("structure"); }

    public int getLiquidId() { return referenceMap.get("liquid"); }
    public int getTerrainId() { return referenceMap.get("terrain"); }
    public int getStructureId() { return referenceMap.get("structure"); }

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
        int path = encoding[0];
        levelMap.put("path", path);

        // Second number represents the tiles height
        int height = encoding[1];
        levelMap.put("height", height);

        // floor or wall status is derived from path
        int terrain = encoding[2];
        int terrainId;
        if (path != 0) {
            terrainId = AssetPool.instance()
                .createStaticAssetReference(Constants.FLOORS_SPRITESHEET_FILEPATH, terrain);
        } else {
            terrainId = AssetPool.instance()
                .createStaticAssetReference(Constants.WALLS_SPRITESHEET_FILEPATH, terrain);
        }
        levelMap.put("terrain", terrain);
        referenceMap.put("terrain", terrainId);

        // Set the tiles liquid value
        int liquid = encoding[3];
        levelMap.put("liquid", liquid);
        if (liquid != 0) {
            int liquidId = AssetPool.instance()
                .createDynamicAssetReference(Constants.LIQUIDS_SPRITESHEET_FILEPATH, liquid, "flickering");
            referenceMap.put("liquid", liquidId);
        }

        // Set the tiles structure value
        int structure = encoding[4];
        levelMap.put("structure", structure);
        if (structure != 0) {
            int structureId = AssetPool.instance()
                .createDynamicAssetReference(Constants.STRUCTURES_SPRITESHEET_FILEPATH, structure, "shearing");
            referenceMap.put("structure", structureId);
        }

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
    public boolean isPath() { return getPath() != 0; }
    public boolean isWall() { return getPath() == 0; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return getStructure() > 0 ; }
    public boolean isStructureUnitOrWall() { return isWall() || isOccupied() || isStructure(); }
    public Gem getGem() { return gem; }

    public String toString() {
        return "[row: " + row + ", column: " + column +"]";
    }

    public void setGem(Gem b) {
        gem = b;
    }
}
