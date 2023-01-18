package game.components;

import com.github.cliftonlabs.json_simple.JsonArray;
import constants.Constants;
import game.collectibles.Gem;
import game.collectibles.Collectable;
import game.entity.Entity;
import game.stores.pools.AssetPool;
import game.stores.pools.AssetReference;

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
    private AssetReference terrainReference;
    private AssetReference liquidReference;
    private AssetReference structureReference;
    private Collectable collectable;
    private final JsonArray representation = new JsonArray();

    public Tile(int tr, int tc) {
        row = tr;
        column = tc;
    }

    public boolean isPath() { return path == 1; }
    public int getPath() { return path; }
    public int getHeight() { return height; }
    public int getTerrain() { return terrainReference != null ? terrainReference.row : 0; }
    public int getLiquid() { return liquidReference != null ? liquidReference.row : 0; }
    public int getStructure() { return structureReference != null ? structureReference.row : 0; }
    public AssetReference getStructureReference() { return structureReference; }
    public AssetReference getTerrainReference() { return terrainReference; }
    public AssetReference getLiquidReference() { return liquidReference; }


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
        if (path != 0) {
            terrainReference = AssetPool.instance()
                    .createStaticAssetReference(Constants.FLOORS_SPRITESHEET_FILEPATH, value);
        } else {
            terrainReference = AssetPool.instance()
                    .createStaticAssetReference(Constants.WALLS_SPRITESHEET_FILEPATH, value);
        }

        value = encoding[3];
        liquidReference = AssetPool.instance()
                .createAnimatedAssetReferenceViaBrightenAndDarken(Constants.LIQUID_SPRITESHEET_FILEPATH, value);

        // Fourth number represents the tile's structure placement
        value = encoding[4];
        structureReference = AssetPool.instance()
                .createAnimatedAssetReferenceViaTopShearing(Constants.STRUCTURE_SPRITESHEET_FILEPATH, value);


        // Refresh the representation
//        representation.delete(0, representation.length());
//        representation.clear();
//        representation
//                .append(path)
//                .append(" ")
//                .append(height)
//                .append(" ")
//                .append(terrainReference.row)
//                .append(" ")
//                .append(liquidReference.row)
//                .append(" ")
//                .append(structureReference.row);

        representation.clear();
        representation.addChain(path)
                .addChain(height)
                .addChain(terrainReference.row)
                .addChain(liquidReference.row)
                .addChain(structureReference.row);
    }

    public String getEncoding() {
        return representation.toString();
    }
    public JsonArray toJson() { return representation; }

    public void removeUnit() {
        if (unit != null) {
            MovementManager movement = unit.get(MovementManager.class);
            movement.tile = null;
        }
        unit = null;
    }

    public void setUnit(Entity unit) {
        MovementManager movement = unit.get(MovementManager.class);
        // remove the given unit from its tile and tile from the given unit
        if (movement.tile != null) {
            Tile occupying = movement.tile.get(Tile.class);
            occupying.unit = null;
            movement.tile = null;
        }
        // remove this tile from its current unit and the current unit from its til
        if (this.unit != null) {
            movement = this.unit.get(MovementManager.class);
            Tile occupying = movement.tile.get(Tile.class);
            occupying.unit = null;
            movement.tile = null;
        }

        // reference new unit to this tile and this tile to the new unit
        movement = unit.get(MovementManager.class);
        movement.tile = owner;
        this.unit = unit;

        // link the animation position to the tile
        Vector position = owner.get(Vector.class);
        Animation animation = unit.get(Animation.class);
        animation.position.copy(position);
    }

    public boolean isWall() { return path == 0; }
    public boolean isOccupied() { return unit != null; }
    public boolean isStructure() { return structureReference != null && structureReference.row != 0; }
    public boolean isStructureUnitOrWall() {
        return isWall() || isOccupied() || isStructure();
    }
    public Collectable getCollectable() { return collectable; }
    public void removeCollectable() { collectable = null; }
    public Collectable consume() {
        Collectable toReturn = collectable;
        collectable = null;
        return toReturn;
    }

    public String toString() {
        return "[row: " + row + ", column: " + column +"]";
    }

    public void setCollectable(Gem b) {
        collectable = b;
    }
}
