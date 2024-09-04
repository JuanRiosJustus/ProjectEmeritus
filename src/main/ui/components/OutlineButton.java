package main.ui.components;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class OutlineButton extends JButton {


    private Color mOutlineColor = Color.BLACK;
    private boolean mIsPaintingOutline = false;
    private boolean mForceTransparent = false;
    private int mThickness = 2;

    private BufferedImage[] mFrames = new BufferedImage[0];
    private ImageIcon mImageIcon = new ImageIcon();
    private Timer mAnimationTimer = new Timer(0, null);
    private int mCurrentIndex = 0;

    public OutlineButton() { this(""); }
    public OutlineButton(String text) {
        this(text, SwingConstants.CENTER, 2, null);
    }
    public OutlineButton(String text, int horizontalAlignment) {
        this(text, horizontalAlignment, 2, null);
    }
    public OutlineButton(String text, int horizontalAlignment, int thickness) {
        this(text, horizontalAlignment, thickness, null);
    }

    public OutlineButton(String text, int horizontalAlignment, int thickness, BufferedImage[] animation) {
        super(text);
        mThickness = thickness;
        setOutlineColor(Color.black);
        setForeground(Color.white);
        setHorizontalAlignment(horizontalAlignment);
        setOpaque(true);
        setBorder(thickness);
        setBackground(ColorPalette.CONTROLLER_BUTTON_HIGHLIGHT);
    }

    public void setAnimatedImage(BufferedImage[] frames) {
        // if the same array, ignore
        // if null, remove image icon
        if (frames == null) {
            setIcon(null);
            return;
        }

        mFrames = frames;
        mCurrentIndex = 0;
        mImageIcon.setImage(mFrames[mCurrentIndex]);
        setIcon(mImageIcon);
        mAnimationTimer.stop();
        mAnimationTimer = new Timer(100, e -> {
            mCurrentIndex = (mCurrentIndex + 1) % mFrames.length;
            mImageIcon.setImage(mFrames[mCurrentIndex]);
        });
        mAnimationTimer.start();
    }

//    private Asset mAsset;
//    private int x; private int y;
//    public void setAsset(GameModel model, Asset asset) {
//        mAsset = asset;
//    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (mAsset != null) {
//            Animation animation = mAsset.getAnimation();
//            g.drawImage(animation.toImage(), x, y, null);
//
//            String animationType = mAsset.getAnimationType();
//            if (animationType.equalsIgnoreCase(AssetPool.STRETCH_Y_ANIMATION)) {
//                y += (int) (getPreferredSize().getHeight() - mAsset.getAnimation().toImage().getHeight());
//                if (y > 100) {
//                    y = 0;
//                }
//            }
//            mAsset.getAnimation().update();
//        }
//    }
//
//    @Override
//    public void update(Graphics g) {
//        if (mAsset != null) {
//            String animationType = mAsset.getAnimationType();
//            if (animationType.equalsIgnoreCase(AssetPool.STRETCH_Y_ANIMATION)) {
//                y += getHeight() - mAsset.getAnimation().toImage().getHeight();
//            }
//        }
//    }


    private void setBorder(int thickness) {
        SwingUiUtils.setStylizedRaisedBevelBorder(this, thickness);
    }
    public void setBorderWithThickness() {
        SwingUiUtils.setStylizedRaisedBevelBorder(this, mThickness);
    }

    public Color getOutlineColor() {
        return mOutlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        mOutlineColor = outlineColor;
        this.invalidate();
    }

    @Override
    public Color getForeground() {
        if (mIsPaintingOutline) {
            return mOutlineColor;
        } else {
            return super.getForeground();
        }
    }

    @Override
    public boolean isOpaque() {
        if (mForceTransparent) {
            return false;
        } else {
            return super.isOpaque();
        }
    }

    @Override
    public void paint(Graphics g) {
        String text = getText();
        if (text == null || text.isEmpty()) {
            super.paint(g);
            return;
        }

        // 1 2 3
        // 8 9 4
        // 7 6 5

        if (isOpaque()) {
            super.paint(g);
        }

        mForceTransparent = true;
        mIsPaintingOutline = true;
        g.translate(-mThickness, -mThickness);
        super.paint(g); // 1
        g.translate(mThickness, 0);
        super.paint(g); // 2
        g.translate(mThickness, 0);
        super.paint(g); // 3
        g.translate(0, mThickness);
        super.paint(g); // 4
        g.translate(0, mThickness);
        super.paint(g); // 5
        g.translate(-mThickness, 0);
        super.paint(g); // 6
        g.translate(-mThickness, 0);
        super.paint(g); // 7
        g.translate(0, -mThickness);
        super.paint(g); // 8
        g.translate(mThickness, 0); // 9
        mIsPaintingOutline = false;

        super.paint(g);
        mForceTransparent = false;
    }
}
