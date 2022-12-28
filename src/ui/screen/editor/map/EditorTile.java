package engine.views.editor.map;

import constants.Constants;
import io.AssetPool;

import java.awt.image.BufferedImage;
import java.util.*;

public class EditorTile {

    // baseImage or null, aboveImage or null
    public final int row;
    public final int column;
    public int floorIndex;
    public int onTopOfFlooIndex;
    public int[] layers;
//    public BufferedImage floor;
//        public final Map<String, BufferedImage> layers = new HashMap<>();

    public EditorTile(int etRow, int etColumn) {
        row = etRow;
        column = etColumn;
    }

    public boolean hasLayers() { return layers != null && layers.length > 0; }
    public void set(int tileIndex, int tileLayer) {
        if (layers == null) {
            layers = new int[Constants.TILE_LAYERS];
            Arrays.fill(layers, -1);
        }
        layers[tileLayer] = tileIndex;
    }

}
