package main.ui.presets.editor;

import main.game.components.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.factories.TileFactory;
import main.game.stores.pools.AssetPool;
import main.utils.ImageUtils;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Set;

public class EditorTile extends JButton {

    private Entity entity;

    public EditorTile(Entity custom) {
        entity = custom;
    }

    public Tile getTile() {
        if (entity == null) { return null; }
        return entity.get(Tile.class);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Tile tile = getTile();
        if (tile == null) { return; }

        int tileX = 0;
        int tileY = 0;

        if (tile.getLiquid() >= 0) {
            Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
            if (animation != null) {
                sync(animation);
                g.drawImage(animation.toImage(), tileX, tileY, null);
                animation.update();
            }
        } else if (tile.getTerrain() >= 0) {
            Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
            if (animation != null) {
                sync(animation);
                g.drawImage(animation.toImage(), tileX, tileY, null);
            }
        }

        if (!tile.getAssets(Tile.SHADOW).isEmpty()) {
            Set<String> shadowAssets = tile.getAssets(Tile.SHADOW);
            for (String asset : shadowAssets) {
                String id = tile.getAsset(asset);
                Animation animation = AssetPool.getInstance().getAnimation(id);
                if (animation != null) {
                    sync(animation);
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                }
            }
        }

        if (tile.getObstruction() != null) {
            Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.OBSTRUCTION));
            if (animation != null) {
                sync(animation);
                g.drawImage(animation.toImage(), tileX, tileY, null);
                animation.update();
            }
        }

//        g.setColor(ColorPalette.TRANSLUCENT_BLACK_V3);
//        int x = (int) (getPreferredSize().getWidth() / 2);
//        int y = (int) (getPreferredSize().getHeight() / 2);
////        g.fillRect(x / 2, y / 2, x, y);
//////
//        g.setColor(Color.WHITE);
//        g.setFont(FontPool.getInstance().getBoldFont(6));
//        x = (int) (getPreferredSize().getWidth() / 3);
//        y = (int) (getPreferredSize().getHeight() / 2);
//        g.drawString(tile.getHeight() + "", x, y);
    }

    private void sync(Animation animation) {
        // check if the animation frames are equal to size of the tile
        Dimension dimension = getPreferredSize();
        BufferedImage sample = animation.toImage();
        if (dimension.width == sample.getWidth() && dimension.height == sample.getHeight()) { return; }
        // animation frames are not equal to the size of the tile. Correct the frame size
        ImageUtils.resizeImages(animation.getContent(), dimension.width, dimension.height);
    }

    public void reset() {
        entity = TileFactory.create(getTile().row, getTile().column);
    }
}
