package game.components;

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
    private int terrain = 0;
    private BufferedImage terrainImage;
    private AssetReference terrainImage2;
    private int structure = 0;
    private BufferedImage structureImage;
    private int special = 0;
    private SpriteAnimation specialAnimation;
    private AssetReference specialAnimation2;
    private AssetReference structureImage2;
    private Collectable collectable;

    private final StringBuilder representation = new StringBuilder();

    public Tile(int tr, int tc) {
        row = tr;
        column = tc;
    }

    public boolean isPath() { return path == 1; }
    public int getPath() { return path; }
    public int getHeight() { return height; }
    public int getTerrain() { return terrain; }
    public int getSpecial() { return special; }
    public int getStructure() { return structure; }
    public AssetReference getStructureImage2() { return structureImage2; }
    public AssetReference getTerrainImage2() { return terrainImage2; }
    public AssetReference getSpecialAnimation2() { return specialAnimation2; }
    public BufferedImage getTerrainImage() { return terrainImage; }
    public BufferedImage getStructureImage() { return structureImage; }
    public SpriteAnimation getSpecialAnimation() { return specialAnimation; }


    private void setSpecial(BufferedImage image, int index) {
        BufferedImage[] rawAnime = AssetPool.instance().getImageAsGlowingAnimation(Constants.SPECIAL_SPRITESHEET_FILEPATH, index);
        specialAnimation = image == null ? null : new SpriteAnimation(rawAnime);
        String spritesheet = Constants.SPECIAL_SPRITESHEET_FILEPATH;
        int column = AssetPool.instance().getSpritesheet(spritesheet).columns(index);
        specialAnimation2 = image == null ? null : new AssetReference(spritesheet, index, column);
        special = index;
    }

    public void setHeight(BufferedImage image, int tileHeight) {
        height = tileHeight;
    }
    private void setStructure(BufferedImage image, int index) {
        String spritesheet = Constants.STRUCTURE_SPRITESHEET_FILEPATH;
        int column = AssetPool.instance().getSpritesheet(spritesheet).columns(index);
        structureImage2 = index == 0 ? null : new AssetReference(spritesheet, index, column);
        structureImage = image;
    }
    public void setTerrain(BufferedImage image, int index) {
        String spritesheet = Constants.TERRAIN_SPRITESHEET_FILEPATH;
        int column = AssetPool.instance().getSpritesheet(spritesheet).columns(index);
        terrainImage2 = index == 0 ? null : new AssetReference(spritesheet, index, column);
        terrainImage = image;
    }

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
        BufferedImage image;
        terrain = encoding[2];
        if (path == 0) {
            image = AssetPool.instance().getImage(Constants.WALLS_SPRITESHEET_FILEPATH, terrain);
        } else {
            image = AssetPool.instance().getImage(Constants.FLOORS_SPRITESHEET_FILEPATH, terrain);
        }
        setTerrain(image, terrain);

        special = encoding[3];
        image = special == 0 ? null : AssetPool.instance().getImage(Constants.SPECIAL_SPRITESHEET_FILEPATH, special);
        setSpecial(image, special);

        // Fourth number represents the tile's structure placement
        structure = encoding[4];
        image = structure == 0 ? null : AssetPool.instance().getImage(Constants.STRUCTURE_SPRITESHEET_FILEPATH, structure);
        setStructure(image, structure);

        // Refresh the representation
        representation.delete(0, representation.length());
        representation
                .append(path)
                .append(" ")
                .append(height)
                .append(" ")
                .append(terrain)
                .append(" ")
                .append(special)
                .append(" ")
                .append(structure);
    }

    public String getEncoding() {
        return representation.toString();
    }

    public void removeUnit() {
        if (unit != null) {
            ActionManager manager = unit.get(ActionManager.class);
            manager.tileOccupying = null;
        }
        unit = null;
    }

    public void setUnit(Entity unit) {
        ActionManager manager = unit.get(ActionManager.class);
        // remove the given unit from its tile and tile from the given unit
        if (manager.tileOccupying != null) {
            Tile occupying = manager.tileOccupying.get(Tile.class);
            occupying.unit = null;
            manager.tileOccupying = null;
        }
        // remove this tile from its current unit and the current unit from its til
        if (this.unit != null) {
            manager = this.unit.get(ActionManager.class);
            Tile occupying = manager.tileOccupying.get(Tile.class);
            occupying.unit = null;
            manager.tileOccupying = null;
        }

        // reference new unit to this tile and this tile to the new unit
        manager = unit.get(ActionManager.class);
        manager.tileOccupying = owner;
        this.unit = unit;

        // link the animation position to the tile
        Vector position = owner.get(Vector.class);
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        spriteAnimation.position.copy(position);
    }

    public boolean isWall() { return path == 0; }

    public boolean isOccupied() { return unit != null; }

    public boolean isStructure() { return structureImage != null; }

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
