package main.ui.outline.production.core;

import main.game.stores.pools.ColorPalette;
import main.ui.custom.SwingUiUtils;
import main.ui.custom.mouse.MouseHoverEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OutlineButton extends JButton {

    private Color outlineColor = Color.BLACK; // Color for the outline
    private boolean isPaintingOutline = false; // Flag to indicate outline painting state
    private boolean forceTransparent = false; // Flag to force transparency
    private final int mThickness; // Thickness of the outline

    private BufferedImage[] frames = new BufferedImage[0]; // Animation frames
    private final ImageIcon imageIcon = new ImageIcon(); // Image icon for animation
    private Timer animationTimer = new Timer(0, null); // Timer to handle frame updates
    private int currentIndex = 0; // Current animation frame index

    // Constructors
    public OutlineButton() {
        this("");
    }

    public OutlineButton(int outlineThickness) {
        this("", SwingConstants.CENTER, outlineThickness, null);
    }

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
        this.mThickness = thickness;
        initializeButton(horizontalAlignment);
        if (animation != null) {
            setAnimatedImage(animation);
        }
    }

    // Initialization
    private void initializeButton(int horizontalAlignment) {
        setFocusPainted(false);
        setOutlineColor(Color.BLACK);
        setForeground(Color.WHITE);
        setHorizontalAlignment(horizontalAlignment);
        setOpaque(true);
        setBorder(mThickness);
        setBackground(ColorPalette.CONTROLLER_BUTTON_HIGHLIGHT);
    }

    // Animation Setup
    public void setAnimatedImage(BufferedImage[] frames) {
        if (frames == null) {
            setIcon(null);
            return;
        }

        this.frames = frames;
        this.currentIndex = 0;
        this.imageIcon.setImage(this.frames[this.currentIndex]);
        setIcon(this.imageIcon);

        // Reset and configure the animation timer
        this.animationTimer.stop();
        this.animationTimer = new Timer(100, e -> {
            this.currentIndex = (this.currentIndex + 1) % this.frames.length;
            this.imageIcon.setImage(this.frames[this.currentIndex]);
        });
        this.animationTimer.start();
    }

    public void setText(String txt) {
        if (getText() == null || txt.equalsIgnoreCase(getText())) {
            return;
        }
        super.setText(txt);
    }
    // Border Customization
    private void setBorder(int thickness) {
        SwingUiUtils.setStylizedRaisedBevelBorder(this, thickness);
    }

    public void setBorderWithThickness() {
        SwingUiUtils.setStylizedRaisedBevelBorder(this, this.mThickness);
    }

    // Outline Color Management
    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
        this.invalidate();
    }

    @Override
    public Color getForeground() {
        return isPaintingOutline ? outlineColor : super.getForeground();
    }

    @Override
    public boolean isOpaque() {
        return forceTransparent ? false : super.isOpaque();
    }

//    @Override
//    public Dimension getPreferredSize() {
//        // Get font metrics for the button's font
//        FontMetrics metrics = getFontMetrics(getFont());
//
//        // Calculate the width and height required for the text
//        int textWidth = metrics.stringWidth(getText());
//        int textHeight = metrics.getHeight();
//
//        // Include padding for the outline thickness and additional margins
//        int width = textWidth + mThickness * 4; // Add padding for the outline
//        int height = textHeight + mThickness * 4; // Add padding for the outline
//
//        return new Dimension(width, height);
//    }

    // Painting Logic
    @Override
    public void paint(Graphics g) {
        String text = getText();
        if (text == null || text.isEmpty()) {
            super.paint(g);
            return;
        }

        if (isOpaque()) {
            super.paint(g);
        }

        forceTransparent = true;
        isPaintingOutline = true;

        // Paint the outline by translating the graphics context
        for (int xOffset = -mThickness; xOffset <= mThickness; xOffset++) {
            for (int yOffset = -mThickness; yOffset <= mThickness; yOffset++) {
                if (xOffset == 0 && yOffset == 0) continue; // Skip the center
                g.translate(xOffset, yOffset);
                super.paint(g);
                g.translate(-xOffset, -yOffset);
            }
        }

        isPaintingOutline = false;

        // Paint the original component
        super.paint(g);
        forceTransparent = false;
    }
}