package game.components;

import game.entity.Entity;
import game.stores.pools.AssetPool;
import utils.ImageUtils;

import java.awt.image.BufferedImage;

public class Tile extends Component {

    public final int row;
    public final int column;
    public Entity unit;
    private BufferedImage baseImage;
    private int baseImageIndex;
    private BufferedImage structureImage;
    private int structureImageIndex;

    private SpriteAnimation liquidAnimation;
    private int liquidIndex;

    private final StringBuilder representation = new StringBuilder();

    public Tile(int tileRow, int tileColumn) {
        row = tileRow;
        column = tileColumn;
    }

    public BufferedImage getBaseImage() { return baseImage; }
    public BufferedImage getStructureImage() { return structureImage; }
    public BufferedImage getLiquidImage() {
        if (liquidAnimation == null) { return null; }
        liquidAnimation.update();
        return liquidAnimation.toImage();
    }

    public SpriteAnimation getLiquidAnimation() {
        return liquidAnimation;
    }

    private void setLiquid(BufferedImage image, int index) {
        if (image != null) {
            liquidAnimation = new SpriteAnimation(AssetPool.instance().getLiquidAnimation(index));
        }
        liquidIndex = index;
    }
    public void setStructure(BufferedImage image, int index) {
        structureImage = image;
        structureImageIndex = index;
    }
    public void setBase(BufferedImage image, int index) {
        baseImage = image;
        baseImageIndex = index;
    }

    public void encode(int[] encoding) {
        // if 1st number is negative, the block is solid/wall
        int baseImageIndex = encoding[0];
        BufferedImage baseImage = AssetPool.instance().getTileImage(Math.abs(encoding[0]));
        setBase(baseImage, baseImageIndex);

        // if 2nd number is 0, there is no structure here
        int structureIndex = encoding[1];
        BufferedImage structureImage = encoding[1] == 0 ? null : AssetPool.instance().getStructureImage(encoding[1]);
        setStructure(structureImage, structureIndex);

        int liquidIndex = encoding[2];
        BufferedImage liquidImage = encoding[2] == 0 ? null : AssetPool.instance().getLiquidImage(encoding[2]);
        setLiquid(liquidImage, liquidIndex);

        // Refresh the representation
        representation.delete(0, representation.length());
        representation.append(baseImageIndex)
                .append(" ")
                .append(structureImageIndex)
                .append(" ")
                .append(liquidIndex);
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

    public boolean isWall() { return baseImageIndex < 0; }

    public boolean isOccupied() { return unit != null; }

    public boolean isStructure() { return structureImage != null; }

    public boolean isStructureUnitOrWall() {
        return isWall() || isOccupied() || isStructure();
    }

    public String toString() {
        return "[row: " + row + ", column: " + column +"]";
    }

    public String encode(String encoding) {
//        Integer[] decoding = IntegerUtils.parseInts(encoding, " ");
//        if (decoding.length != 2) { Engine.get().stop("Incorrect decoding"); }
//
//        // if 1st number is negative, the block is solid/wall
//        int baseImageIndex = decoding[0];
//        BufferedImage baseImage = AssetPool.instance().getTileImage(Math.abs(decoding[0]));
//        setBase(baseImage, baseImageIndex);
//
//        // if 2nd number is 0, there is no structure here
//        int structureIndex = decoding[1];
//        BufferedImage structureImage = decoding[1] == 0 ? null : AssetPool.instance().getStructureImage(decoding[2]);
//        setStructure(structureImage, structureIndex);
//        Structure structure = owner.get(Structure.class);
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("[")
//                .append(isWall() ? "-" : "").append(baseImageIndex).append(" ") // 1st number based on tile
//                .append(structure == null ? "0" : structure.index) // 2nd number based on structure
//                .append("]");
//
//        return sb.toString();
        return "";
    }

    public String encoding() {
//        Structure struct = owner.get(Structure.class);
//        boolean isStructure = struct != null;
//        return baseImageIndex + " " + (isStructure ? "TODO" : "0");
        return "";
    }
}
