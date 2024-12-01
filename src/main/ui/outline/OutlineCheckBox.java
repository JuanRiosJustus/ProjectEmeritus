package main.ui.outline;

import main.game.stores.pools.ColorPalette;
import main.ui.custom.SwingUiUtils;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class OutlineCheckBox extends JToggleButton {

    private Color outlineColor = Color.BLACK; // Color for the outline
    private boolean isPaintingOutline = false; // Flag to indicate outline painting state
    private boolean forceTransparent = false; // Flag to force transparency
    private final int mThickness; // Thickness of the outline

    private BufferedImage[] frames = new BufferedImage[0]; // Animation frames
    private final ImageIcon imageIcon = new ImageIcon(); // Image icon for animation
    private Timer animationTimer = new Timer(0, null); // Timer to handle frame updates
    private int currentIndex = 0; // Current animation frame index

    // Constructors


    public OutlineCheckBox() {
        this(SwingConstants.CENTER, 2, null);
    }

    public OutlineCheckBox(int horizontalAlignment) {
        this(horizontalAlignment, 2, null);
    }

    public OutlineCheckBox(int horizontalAlignment, int thickness) {
        this(horizontalAlignment, thickness, null);
    }

    public OutlineCheckBox(int horizontalAlignment, int thickness, BufferedImage[] animation) {
        this.mThickness = thickness;
        initializeCheckBox(horizontalAlignment);

        if (animation != null) {
            setAnimatedImage(animation);
        }
    }

    public void setText(String txt) { /* DONT USE FOR CHECK BOX */ }

    // Initialization
    private void initializeCheckBox(int horizontalAlignment) {
        setFocusPainted(false);
        setOutlineColor(Color.BLACK);
        setForeground(Color.WHITE);
        setHorizontalAlignment(horizontalAlignment);
        setOpaque(true);
        setContentAreaFilled(false);

        setBorder(mThickness);
        setBackground(ColorPalette.CONTROLLER_BUTTON_HIGHLIGHT);
        addActionListener(e -> { setSelected(isSelected()); });
    }

    public void setSelected(boolean b) {
        if (b) {
            super.setText("X");
        } else {
            super.setText(" ");
        }
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
        // Set the background color based on selection state
        Color backgroundColor = getBackground();

        // Paint the background manually
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Call super.paint for additional rendering
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