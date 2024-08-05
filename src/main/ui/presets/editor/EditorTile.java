package main.ui.presets.editor;

import main.constants.Settings;
import main.game.components.Animation;
import main.game.components.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.factories.TileFactory;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.ColorPalette;
import main.utils.ImageUtils;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EditorTile extends JButton {

    private Entity entity;
    private static final Map<Integer, Color> colorMap = new HashMap<>();
    private final Map<Animation, JButton> mSyncdAnimationMap = new HashMap<>();
    private JPanel mCanvas = null;


    private Animation mObstructAnimation = null;
    private JButton mObstructAnimationButton = null;
    private final ImageIcon mObstructAnimationImageHolder = new ImageIcon();


    private Animation mEntityAnimation = null;
    private JButton mEntityAnimationButton = null;
    private final ImageIcon mEntityAnimationButtonImageHolder = new ImageIcon();

//    BufferedImage blank = new BufferedImage(spriteSizes, spriteSizes, BufferedImage.TYPE_INT_ARGB);

    public EditorTile(Entity custom) {
        entity = custom;
    }

    public static void clearColorMap() { colorMap.clear(); }

    public Tile getTile() {
        if (entity == null) { return null; }
        return entity.get(Tile.class);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Tile tile = getTile();
        if (tile == null) { return; }

        int tileSize = Settings.getInstance().getSpriteSize();
        int tileWidth = (int) getPreferredSize().getWidth();
        int tileHeight = (int) getPreferredSize().getHeight();
        int tileX = (tile.column * tileWidth);
        int tileY = (tile.row * tileHeight);

        if (tile.getLiquid() != null) {
//            Animation animation = AssetPool.getInstance().getAnimationWithId(tile.getAsset(Tile.LIQUID));
//            if (animation != null) {
//                sync(animation);
//                g.drawImage(animation.toImage(), 0, 0, null);
//                animation.update();
//            }
        } else if (tile.getTerrain() != null) {
//            Animation animation = AssetPool.getInstance().getAnimationWithId(tile.getAsset(Tile.TERRAIN));
//            if (animation != null) {
//                sync(animation);
//                g.drawImage(animation.toImage(), 0, 0, null);
//            }
        }

//        if (!tile.getAssets(Tile.CARDINAL_SHADOW).isEmpty()) {
//            Set<String> shadowAssets = tile.getAssets(Tile.CARDINAL_SHADOW);
//            for (String asset : shadowAssets) {
//                String id = tile.getAsset(asset);
//                Animation animation = AssetPool.getInstance().getAnimationWithId(id);
//                if (animation != null) {
//                    sync(animation);
//                    g.drawImage(animation.toImage(), 0, 0, null);
//                }
//            }
//        }

        if (tile.getObstruction()  != null) {
//            Animation animation = AssetPool.getInstance().getAnimationWithId(tile.getAsset(Tile.OBSTRUCTION));
//            if (animation != null && mCanvas != null) {
//                if (mObstructAnimation == null || mObstructAnimationButton == null) {
//                    mObstructAnimation = animation.copy();
//                    mObstructAnimationButton = (mObstructAnimationButton == null ? new JButton() : mObstructAnimationButton);
//                    mObstructAnimationButton.setVisible(true);
//                    mObstructAnimationButton.setIcon(mObstructAnimationImageHolder);
//                    Vector3f v = Vector3f.centerLimitOnY(tileSize, tileX, tileY, mObstructAnimation.toImage().getWidth(), mObstructAnimation.toImage().getHeight());
//                    mObstructAnimationButton.setBounds(
//                            (int) (v.x - animation.getAnimatedOffsetX() - 2),
//                            (int) (v.y - animation.getAnimatedOffsetY() - 6),
//                            mObstructAnimation.toImage().getWidth(),
//                            mObstructAnimation.toImage().getHeight());
//                    mObstructAnimationButton.setBorderPainted(false);
//                    mObstructAnimationButton.setFocusPainted(false);
//                    mObstructAnimationButton.setBackground(Color.BLUE);
//                    mCanvas.add(mObstructAnimationButton);
//                }
//                if (mObstructAnimation != null && mObstructAnimationButton != null) {
//                    Vector3f v = Vector3f.centerLimitOnY(tileSize, tileX, tileY, mObstructAnimation.toImage().getWidth(), mObstructAnimation.toImage().getHeight());
//                    // TODO figure out why we need to use these magic numbers. it should work without them
//                    mObstructAnimationButton.setBounds(
//                            (int) (v.x - animation.getAnimatedOffsetX() - 2),
//                            (int) (v.y - animation.getAnimatedOffsetY() - 6),
//                            mObstructAnimation.toImage().getWidth(),
//                            mObstructAnimation.toImage().getHeight());
//                    mObstructAnimationImageHolder.setImage(mObstructAnimation.toImage());
//                    mObstructAnimation.update();
//                }
//            }
        } else {
            if (mObstructAnimationButton != null) {
                mCanvas.remove(mObstructAnimationButton);
                mObstructAnimationButton.setVisible(false);
                mObstructAnimationButton = null;
            }
        }

        int spawnRegion = 3; //tile.getSpawnRegion();
        if (spawnRegion >= 0 && !tile.isNotNavigable()) {
            Color c = colorMap.get(spawnRegion);
            if (c == null) {
                c = ColorPalette.getRandomColor();
                colorMap.put(spawnRegion, new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
            }

            g.setColor(c);
            g.fillRect(0, 0, tileWidth, tileHeight);
            g.setColor(Color.WHITE);
            g.drawString(spawnRegion + "", tileWidth / 2, tileHeight / 2);

//
//            Graphics2D g2s = (Graphics2D)g;
//            Stroke oldStroke = g2s.getStroke();
//            int thicness = 3;
//            g2s.setStroke(new BasicStroke(thicness));
//            g2s.setColor(Color.WHITE);
////            g2s.drawRect(tileX, tileY, tileSize , tileSize );
//            g.drawString(spawnRegion + "", tileWidth / 2, tileHeight / 2);
//            g2s.setStroke(oldStroke);
        }

        if (tile.getUnit() != null) {
            Animation animation = tile.getUnit().get(Animation.class);
            if (animation != null && mCanvas != null) {
                if (mEntityAnimation == null || mEntityAnimationButton == null) {
                    mEntityAnimation = animation.copy();
                    createResizedAnimation(mEntityAnimation, getPreferredSize());
                    mEntityAnimationButton = (mEntityAnimationButton == null ? new JButton() : mEntityAnimationButton);
                    mEntityAnimationButton.setVisible(true);
                    mEntityAnimationButton.setIcon(mEntityAnimationButtonImageHolder);
                    mEntityAnimationButton.setBorderPainted(false);
                    mEntityAnimationButton.setFocusPainted(false);
                    mEntityAnimationButton.setBounds(
                            tileX + mEntityAnimation.getAnimatedOffsetX(),
                            tileY + mEntityAnimation.getAnimatedOffsetY(),
                            mEntityAnimation.toImage().getWidth(),
                            mEntityAnimation.toImage().getHeight());
                    mEntityAnimationButtonImageHolder.setImage(mEntityAnimation.toImage());
                    mCanvas.add(mEntityAnimationButton);
                }
                if (mEntityAnimation != null && mEntityAnimationButton != null) {
                    mEntityAnimationButton.setBounds(
                            tileX + mEntityAnimation.getAnimatedOffsetX(),
                            tileY + mEntityAnimation.getAnimatedOffsetY(),
                            mEntityAnimation.toImage().getWidth(),
                            mEntityAnimation.toImage().getHeight());
                    mEntityAnimationButtonImageHolder.setImage(mEntityAnimation.toImage());
                    mEntityAnimation.update();
                }
            }
        } else {
            if (mEntityAnimationButton != null) {
                mCanvas.remove(mEntityAnimationButton);
                mEntityAnimationButton.setVisible(false);
                mEntityAnimationButton = null;
            }
        }
    }

    private void sync(Animation animation) {
        // check if the animation frames are equal to size of the tile
        Dimension dimension = getPreferredSize();
        BufferedImage sample = animation.toImage();
        if (dimension.width == sample.getWidth() && dimension.height == sample.getHeight()) { return; }
        // animation frames are not equal to the size of the tile. Correct the frame size
//        for (int i = 0 ; i < animation.getContent().length; i++) {
//            BufferedImage image = animation.getFrame(i);
//            System.out.println("w " + image.getWidth() + " h " + image.getHeight());
//        }
        ImageUtils.resizeImages(animation.getContent(), dimension.width, dimension.height);
    }

    private int[] createResizedAnimation(Animation animation, Dimension dimension) {
        // check if this animation is already synced
        // check if the animation frames are equal to size of the tile
//        JButton newlySynced = mSyncdAnimationMap.get(animation);
//        if (newlySynced != null) { return; }
//        mSyncdAnimationMap.put(animation, new JButton());
        // Animation frames are not equal to the size of the tile.
        // Correct the frame size
        // get the difference between the sprites current size and the containers size
        int widthDiff = Math.abs(dimension.width - Settings.getInstance().getSpriteSize());
        int heightDiff = Math.abs(dimension.height - Settings.getInstance().getSpriteSize());

        ImageUtils.resizeImagesWithSubtraction(animation.getContent(), widthDiff, heightDiff);

        return new int[]{ widthDiff, heightDiff };
    }

    public void reset() {
        entity = TileFactory.create(getTile().row, getTile().column);
    }
    public void setCanvas(JPanel canvas) {
        mCanvas = canvas;
    }
}
