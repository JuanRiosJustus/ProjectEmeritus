package main.ui.screen.editor.map;

import main.constants.Constants;

import java.util.Arrays;

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
            layers = new int[10];
            Arrays.fill(layers, -1);
        }
        layers[tileLayer] = tileIndex;
    }

}
