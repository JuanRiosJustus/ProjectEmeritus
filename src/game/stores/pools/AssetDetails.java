package game.stores.pools;

import java.awt.image.BufferedImage;

public class AssetDetails {
    public final BufferedImage[] content;
    public final int row;
    public final int column;

    public AssetDetails(BufferedImage[] adContent, int adRow, int adColumn) {
        content = adContent;
        row = adRow;
        column = adColumn;
    }
}
