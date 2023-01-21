package ui.panels;

import constants.ColorPalette;
import game.GameModel;
import game.components.Tile;
import game.entity.Entity;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MiniMapPanel extends JScene {
    private JPanel container = new JPanel();
    private JPanel drawingBoard;
    private BufferedImage map;
    public MiniMapPanel(int width, int height) {
        super(width, height, "MiniMapPanel");
        ComponentUtils.setTransparent(this);

        ComponentUtils.setSize(container, width, height);
        ComponentUtils.setTransparent(container);
        container.setLayout(null);

        drawingBoard = new JPanel();
//        drawingBoard.setBackground(Color.RED);
        drawingBoard.setBounds(20, 20, width / 4, height / 4);
        container.add(drawingBoard);

//        add(container);
    }

    public void update(GameModel model) {
        int scale = 10;
        BufferedImage image = new BufferedImage(model.getRows(), model.getColumns(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        for (int row = 0; row < model.getRows(); row += scale) {
            for (int column = 0; column < model.getColumns(); column += scale) {
                Entity currentEntity = model.tryFetchingTileAt(row, column);
                Tile currentTile = currentEntity.get(Tile.class);

//                if (currentTile.unit != null) {
//                    graphics.setColor(Color.WHITE);
//                } else if (currentTile.isStructure()) {
//                    graphics.setColor(Color.RED);
//                } else {
//                    graphics.setColor(Color.GREEN);
//                }
                graphics.setColor(Color.BLUE);
                graphics.fillRect(column, row, scale, scale);
            }
        }

        map = image;
        graphics.dispose();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map == null) { return; }
//        g.drawImage(map, 0, 0, null);
    }
}
