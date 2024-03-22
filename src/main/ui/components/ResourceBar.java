package main.ui.components;

import main.game.stores.pools.ColorPalette;
import main.ui.presets.internet.HumanProgressBar;
import main.utils.MathUtils;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

public class ResourceBar extends JProgressBar {
    private static final String DISABLED_PERCENT_STRING = " --- ";

    public static final Color PREFERRED_PROGRESS_COLOR = new Color(232, 0, 0);
    private static final Color gradientEndingColor = new Color(0xc0c0c0);
    private static final Color borderColor = new Color(0x736a60);
    private static final Color disabledBorderColor = new Color(0xbebebe);

    private static final Composite solid = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
    private static final Composite transparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f);
    private static final Composite veryTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);

    private static GradientPaint gradient;

    private final int mStrokeSize = 3;
    private final BasicStroke mOutlineStroke = new BasicStroke(mStrokeSize);

    private int oldWidth;
    private int oldHeight;

    private int displayWidth;
    private int displayHeight;

    private final int[] insets = new int[4];
    private static final int TOP_INSET = 0;
    private static final int LEFT_INSET = 1;
    private static final int BOTTOM_INSET = 2;
    private static final int RIGHT_INSET = 3;

    private boolean mStringVisible;

    private Color progressColor;
    private final String mResource;
    private final boolean mUseRawValues;
    private final boolean mCenterValues;
    private final boolean mShowSingleValue;
    private final boolean mHideName;
    public static final int EXCLUDE_LABEL_SHOW_CENTERED_VALUE = 1;
    public static final int EXCLUDE_LABEL_SHOW_CURRENT_TO_MAX_VALUE = 2;

    private ResourceBar(String resource, boolean displayRawValues, boolean centerCurrentValue, boolean setOpaque,
                        boolean stringIsVisible, boolean showSingleValue, boolean hideName) {
        progressColor = PREFERRED_PROGRESS_COLOR;
        mResource = resource;
        mUseRawValues = displayRawValues;
        mCenterValues = centerCurrentValue;
        mStringVisible = stringIsVisible;
        mShowSingleValue = showSingleValue;
        mHideName = hideName;
        setOpaque(setOpaque);
    }

    // https://stackoverflow.com/questions/13229725/java-awt-font-letter-spacing
    public static ResourceBar createResourceBar(String name, int barType) {

        ResourceBar newResourceBar = null;
        switch (barType) {
            case EXCLUDE_LABEL_SHOW_CENTERED_VALUE ->
                    newResourceBar = new ResourceBar(name, false, true,
                            true, true, true, true);
            case EXCLUDE_LABEL_SHOW_CURRENT_TO_MAX_VALUE ->
                    newResourceBar = new ResourceBar(name, true, true,
                    true, true, false, true);
            default -> {
                newResourceBar = new ResourceBar(name, false, false,
                        true, true, false, false);
            }
        }

        return newResourceBar;
    }

    public void updateGraphics() {
        update(getGraphics());
    }

    public void setResourceValue(int min, int current, int max) {
        setMinimum(min);
        setValue(current);
        setMaximum(max);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = displayWidth != 0 ? displayWidth - 1 : getWidth() - 1;
        int h = displayHeight != 0 ? displayHeight - 1 : getHeight() - 1;

        int x = insets[LEFT_INSET];
        int y = insets[TOP_INSET];
        w -= (insets[RIGHT_INSET] << 1);
        h -= (insets[BOTTOM_INSET] << 1);

        if (gradient == null) {
            gradient = new GradientPaint(0.0f, 0.0f, Color.WHITE, 0.0f, h, gradientEndingColor);
        }
        Graphics2D g2d = (Graphics2D) g;
        // Clean background
        if (isOpaque()) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.translate(x, y);

        // Control Border
        g2d.setColor(isEnabled() ? borderColor : disabledBorderColor);
        g2d.drawLine(1, 0, w - 1, 0);
        g2d.drawLine(1, h, w - 1, h);
        g2d.drawLine(0, 1, 0, h - 1);
        g2d.drawLine(w, 1, w, h - 1);

        // Fill in the progress
        int min = getMinimum();
        int max = getMaximum();
        int total = max - min;
        float dx = (float) (w - 2) / (float) total;
        int value = getValue();
        int progress = 0;
        if (value == max) {
            progress = w - 1;
        } else {
            progress = (int) (dx * getValue());
        }

        g2d.setColor(getForeground());
        g2d.fillRect(1, 1, progress, h - 1);

        // A gradient over the progress fill
        g2d.setPaint(gradient);
        g2d.setComposite(transparent);
        g2d.fillRect(1, 1, w - 1, (h >> 1));
        final float FACTOR = 0.20f;
        g2d.fillRect(1, h - (int) (h * FACTOR), w - 1, (int) (h * FACTOR));

        if (isEnabled()) {
//            for (int i = h; i < w; i += h) {
//                g2d.setComposite(veryTransparent);
//                g2d.setColor(Color.GRAY);
//                g2d.drawLine(i, 1, i, h - 1);
//                g2d.setColor(Color.WHITE);
//                g2d.drawLine(i + 1, 1, i + 1, h - 1);
//            }
        } else {
            for (int i = 0; i < w; i += h) {
                g2d.setComposite(veryTransparent);
                g2d.setColor(Color.RED);
                g2d.drawLine(i, h - 1, i + h, 1);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(i + 1, h - 1, i + 1 + h, 1);
            }
        }

        if (mStringVisible) {
            FontMetrics fm = g.getFontMetrics();
            int stringW = 0;
            int stringH = 0;

            g2d.setComposite(solid);
            g2d.setColor(ColorPalette.WHITE);

            if (isEnabled()) {
                int p = getValue();
                String percent = Integer.toString(p, 10) + "%";
                if (p < 10) {
                    percent = "0" + percent;
                }

//                stringW = fm.stringWidth(String.valueOf(getValue()));
                stringH = ((h - fm.getHeight()) / 2) + fm.getAscent();

                // remember the original settings
                Color originalColor = g2d.getColor();
                Stroke originalStroke = g2d.getStroke();
                RenderingHints originalHints = g2d.getRenderingHints();
                AffineTransform originalTransform = g2d.getTransform();

                // create a glyph vector from your text, then get the shape object
                GlyphVector glyphVector = g.getFont().createGlyphVector(g2d.getFontRenderContext(), mResource);
                Shape textShape = glyphVector.getOutline();

                if (mHideName) {

                    g2d.setColor(ColorPalette.BLACK);
                    g2d.setStroke(mOutlineStroke);
                    g2d.translate(mStrokeSize, stringH); // 5 so its not on the edge
                    g2d.draw(textShape); // draw outline

                    g2d.setColor(ColorPalette.WHITE);
                    g2d.fill(textShape); // fill the shape

                    // reset to original settings after painting
                    g2d.setColor(originalColor);
                    g2d.setStroke(originalStroke);
                    g2d.setRenderingHints(originalHints);
                    g2d.setTransform(originalTransform);

                }

                // remember the original settings
                originalColor = g2d.getColor();
                originalStroke = g2d.getStroke();
                originalHints = g2d.getRenderingHints();
                originalTransform = g2d.getTransform();

                String textToDraw;
                if (mUseRawValues) {
                    textToDraw = getValue() + "/" + getMaximum();
                    if (mShowSingleValue) {
                        textToDraw = String.valueOf(getValue());
                    }
                } else {
                    float currentPercent = (float) ((getValue() * 1.0) / (getMaximum() * 1.0));
                    int percentage = (int) MathUtils.map(currentPercent, 0, 1, 0, 100);
                    textToDraw = percentage + "/100";
                    if (mShowSingleValue) {
                        textToDraw = String.valueOf(percentage);
                    }
                }

                // create a glyph vector from your text, then get the shape object
                glyphVector = g.getFont().createGlyphVector(g2d.getFontRenderContext(), textToDraw);
                textShape = glyphVector.getOutline();

                g2d.setColor(ColorPalette.BLACK);
                g2d.setStroke(mOutlineStroke);
                g2d.translate(w - fm.stringWidth(textToDraw) - mStrokeSize -
                        (mCenterValues ? (w / 2) - (fm.stringWidth(textToDraw) / 2) : 0), stringH);
                g2d.draw(textShape); // draw outline

                g2d.setColor(ColorPalette.WHITE);
                g2d.fill(textShape); // fill the shape

                // reset to original settings after painting
                g2d.setColor(originalColor);
                g2d.setStroke(originalStroke);
                g2d.setRenderingHints(originalHints);
                g2d.setTransform(originalTransform);

            } else {
                stringW = fm.stringWidth(DISABLED_PERCENT_STRING);
                stringH = ((h - fm.getHeight()) / 2) + fm.getAscent();

                g2d.drawString(DISABLED_PERCENT_STRING, w - stringW, stringH);
            }
//            w -= (stringW + PREFERRED_PERCENT_STRING_MARGIN_WIDTH);
        }

    }

    public void setInsets(int top, int left, int bottom, int right) {
        insets[TOP_INSET] = top;
        insets[LEFT_INSET] = left;
        insets[BOTTOM_INSET] = bottom;
        insets[RIGHT_INSET] = right;
    }

    public void setStringVisible(boolean stringVisible) {
        mStringVisible = stringVisible;
    }

    @Override
    public void validate() {
        int w = getWidth();
        int h = getHeight();

        super.validate();
        if (oldWidth != w || oldHeight != h) {
            oldWidth = w;
            oldHeight = h;
            gradient = null;
        }
    }

    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(Color progressColor) {
        this.progressColor = progressColor;
    }

    public static class T extends JFrame {
        public T() {
            super();
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLayout(null);
            this.setSize(350, 75);
            HumanProgressBar p = new HumanProgressBar();
            p.setValue(50);
            p.setBounds(15, 15, 300, 15);
            this.add(p);
            this.setVisible(true);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new T();
                }
            });
        }
    }
}