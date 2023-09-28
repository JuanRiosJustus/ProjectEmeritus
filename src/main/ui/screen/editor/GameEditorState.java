package main.ui.screen.editor;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.engine.Engine;
import main.game.components.tile.Tile;
import main.game.components.Vector;
import main.input.InputController;
import main.ui.presets.EditorTile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;

public class GameEditorState {

    public Size mapSize;
    private int tileSize;
    private Tile[][] editorTileMap = new Tile[0][0];
    private final Vector m_mousePosition = new Vector();
    private final HashMap<Image, Image> imageCache = new HashMap<>();
    private final Set<Tile> m_hoveredTiles = new HashSet<>();
    private final SplittableRandom m_random = new SplittableRandom();

    public void handleRenderAndUpdate(Graphics g, InputController c, GameEditorSidePanel p) {
        update(InputController.getInstance(), p);
        render(g, p);
    }
    private void setupNewTileMap(GameEditorSidePanel panel) {
        mapSize = panel.getSelectedMapSize();
        tileSize = mapSize.tileSize;
        imageCache.clear();
        setupTileMap(mapSize.height, mapSize.width);
    }

    private void setupTileMap(int rows, int cols) {
        editorTileMap = new Tile[rows][cols];
        for (int row = 0; row < editorTileMap.length; row++) {
            for (int col = 0; col < editorTileMap[row].length; col++) {
                editorTileMap[row][col] = new Tile(row, col);
            }
        }
    }
    private void update(InputController c, GameEditorSidePanel p) {
        if (p == null || c == null) { return; }
        if (mapSize != p.getSelectedMapSize()) { setupNewTileMap(p); }
        m_mousePosition.copy(c.getMouse().position);
        if (!c.getMouse().isHeld() && !c.getMouse().isPressed() && !c.getMouse().isReleased()) { return; }
//        if (!c.getMouse().isHeld() && !c.getMouse().isPressed() && !c.getMouse().isReleased()) { return; }
        handleMousePressed(p);

    }
    private void handleMousePressed(GameEditorSidePanel panel) {
        String brushModeValue = panel.getBrushMode();
        if (brushModeValue == null) { return; }

        if (brushModeValue.equals("Fill")) { fill(panel); return; }

        int size = sizeToValue(panel.getSelectedBrushSize());
        Set<Tile> clickedTiles = tryFetchingMousedAt(size);
        if (clickedTiles == null) { return; }
        for (Tile tile : clickedTiles) {
            if (brushModeValue.equals("Erase")) {
//                tile.floorIndex = -1;
//                tile.onTopOfFlooIndex = -1;
//                tile.encode(0, 0, 0);
            } else {
//                String layer = panel.
//                int index = panel.getSelectedPanelItem().panelIndex;
//                BufferedImage image = panel.getSelectedPanelItem().panelImage;
//                tile.set(index, panel.getSelectedLayer());
//                tile.layers.put(panel.getSelectedPanelItem().panelIndex, image);
//                tile.encode();
            }

//            String layer = panel.getSelectedPanelItem().getLayerType();
//            BufferedImage image = panel.getSelectedPanelItem().panelImage;
//            if (panel.isClearing()) {
//                tile.layers.clear();
//            } else {
//                tile.layers.put(layer, image);
//            }
//            System.out.println("Putting on layer " + layer + " " + tile.layers.size() + " ?");
//            tile.baseImage = panel.getSelectedPanelItem().getSelectedImage();
        }
    }

    private void fill(GameEditorSidePanel panel) {
        GameEditorSidePanelItem selected = panel.getSelectedPanelItem();
        for (Tile[] row : editorTileMap) {
            for (Tile tile : row) {
                String layer = panel.getSelectedPanelItem().getLayerType();
                BufferedImage image = panel.getSelectedPanelItem().panelImage;
//                tile.layers.put(layer, image);
//                System.out.println("Filled: " + tile.layers.size() + " ?");
            }
        }
    }

    private Tile tryFetchingTileAt(int row, int col) {
        if (row < 0 || col < 0 || row >= editorTileMap.length) { return null; }
        if (col >= editorTileMap[row].length) { return null; }
        return editorTileMap[row][col];
    }

    private Set<Tile> tryFetchingMousedAt(int size) {
        int column = (int) ((m_mousePosition.x  - tileSize) / tileSize);
        int row = (int) ((m_mousePosition.y - tileSize) / tileSize);
        if (row < 0 || column < 0) { return null; }
        if (row >= editorTileMap.length) { return null; }
        if (column >= editorTileMap[row].length) { return null; }
        m_hoveredTiles.clear();
        Tile tile = editorTileMap[row][column];
        // consider 'brush size'
        row = tile.row;
        int col = tile.column;
        for (int irow = row - size; irow <= row + size; irow++) {
            for (int icol = col - size; icol <= col + size; icol++) {
                Tile itile = tryFetchingTileAt(irow, icol);
                if (itile == null) { continue; }
                m_hoveredTiles.add(itile);
            }
        }
        return m_hoveredTiles;
    }

    private int sizeToValue(Size s) {
        int val = 0;
        switch (s) {

            case Gigantic -> val = 8;
            case Large -> val = 4;
            case Medium -> val = 2;
            case Small -> val = 1;
            case Tiny -> val = 0;
            default -> {}
        }
        return val;
    }

//    public Image getCachedImage(EditorTile tile) {
//        if (tile.baseImage == null) { return null; }
//        Image toShow = imageCache.get(tile.baseImage);
//        if (toShow == null) {
//            toShow = tile.baseImage.getScaledInstance(tileSize, tileSize, Image.SCALE_FAST);
//            imageCache.put(tile.baseImage, toShow);
//        }
////        System.out.println(m_imageCache.size() + " large");
//        return toShow;
//        return null;
//    }

    private void displayTiles(Graphics g, Set<Tile> tiles, Color c) {
        if (tiles == null) { return; }
        for (Tile tile : tiles) {
            int row = tile.row;
            int col = tile.column;
            g.setColor(c);
            g.fillRect(
                    (col * tileSize) + tileSize,
                    (row * tileSize) + tileSize,
                    tileSize,
                    tileSize
            );
        }
    }

    private void render(Graphics g, GameEditorSidePanel p) {
        g.setColor(ColorPalette.TRANSPARENT_RED);
        g.drawString(Engine.getInstance().getFPS() + "", 10, 20);
        g.setColor(ColorPalette.BLACK);
        g.fillRect(0, 0, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
        int tcolumn = (int) ((m_mousePosition.x) / tileSize) + tileSize;
        int trow = (int) ((m_mousePosition.y) / tileSize) + tileSize;

        int size = sizeToValue(p.getSelectedBrushSize());
        Set<Tile> hovered = tryFetchingMousedAt(size);
        for (int row = 0; row < editorTileMap.length; row++) {
            for (int col = 0; col < editorTileMap[row].length; col++) {
                Tile current = editorTileMap[row][col];
                if (hovered == current) {
                    g.setColor(ColorPalette.WHITE);
                    g.fillRect(
                            (col * tileSize) + tileSize,
                            (row * tileSize) + tileSize,
                            tileSize,
                            tileSize
                    );
                } else {
                    drawTile(g, current);
//                    drawLayerImage(g, "Floor", current);
//                    drawLayerImage(g, "on Floor", current);
//                    g.drawImage(img,
//                            (col * tileSize) + tileSize,
//                            (row * tileSize) + tileSize,
//                            null
//                    );
//                    Image img = getLayerImage(g, "On Floor", current);
//                    g.drawImage(img,
//                            (col * tileSize) + tileSize,
//                            (row * tileSize) + tileSize,
//                            null
//                    );
//                    Image img = getCachedImage(current);
//                    if (img != null) {
//                        g.drawImage(
//                                img,
//                                (col * tileSize) + tileSize,
//                                (row * tileSize) + tileSize,
//                                null
//                        );
//                    } else {
//                        g.setColor(ColorPalette.WHITE);
//                        g.drawRect(
//                            (col * tileSize) + tileSize,
//                            (row * tileSize) + tileSize,
//                                tileSize,
//                                tileSize
//                        );
//                    }

                }
            }
        }
        displayTiles(g, hovered, ColorPalette.WHITE);
    }

    private void drawTile(Graphics g, Tile tile) {
//        if (tile.layers != null) {
//            for (int i = 0; i < tile.layers.length; i++) {
//                int val = tile.layers[i];
//                if (val == -1) { continue; }
//                BufferedImage toShow = AssetPool.get().getFloorSprites().getSprite(val, 0); //.m_sheet.get(val, 0);
//                Image img = getCachedImage(toShow);
//                g.drawImage(
//                        img,
//                        (tile.column * tileSize) + tileSize,
//                        (tile.row * tileSize) + tileSize,
//                        null
//                );
//            }
//        }
        g.setColor(ColorPalette.TRANSPARENT_BEIGE);
        g.drawRect(
                (tile.column * tileSize) + tileSize,
                (tile.row * tileSize) + tileSize,
                tileSize,
                tileSize
        );
    }

    public void drawLayerImage(Graphics g, String layerToShow, EditorTile tile) {
//        if (tile.hasLayers()) {
////            AssetPool.get().m_sheet.get(tile.row, )
//            BufferedImage toShow = tile.layers.get(layerToShow);
//            Image img = getCachedImage(toShow);
//            g.drawImage(
//                    img,
//                    (tile.column * tileSize) + tileSize,
//                    (tile.row * tileSize) + tileSize,
//                    null
//            );
//        }
//        g.setColor(ColorPalette.TRANS_WHITE);
//        g.drawRect(
//                (tile.column * tileSize) + tileSize,
//                (tile.row * tileSize) + tileSize,
//                tileSize,
//                tileSize
//        );
    }

    public Image getCachedImage(BufferedImage image) {
        if (image == null) { return null; }
        Image toShow = imageCache.get(image);
        if (toShow == null) {
            toShow = image.getScaledInstance(tileSize, tileSize, Image.SCALE_FAST);
            imageCache.put(image, toShow);
        }
        return toShow;
    }
}
