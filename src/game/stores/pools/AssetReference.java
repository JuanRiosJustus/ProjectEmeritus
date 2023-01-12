package game.stores.pools;

import java.util.Objects;

public class AssetReference {
    public final String spritesheet;
    public final int row;
    public final int column;

    public AssetReference(String arSpritesheet, int arRow, int arColumn) {
        spritesheet = arSpritesheet;
        row = arRow;
        column = arColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AssetReference that)) return false;
        return row == that.row && column == that.column && Objects.equals(spritesheet, that.spritesheet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spritesheet, row, column);
    }
}
