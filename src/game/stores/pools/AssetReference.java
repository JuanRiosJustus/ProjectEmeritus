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
    
}
