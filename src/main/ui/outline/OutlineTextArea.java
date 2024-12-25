package main.ui.outline;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OutlineTextArea extends JTextArea {
    private Color mOutlineColor = Color.BLACK;
    private Color mInlineColor = Color.WHITE;
    private int mOutlineThickness;
    private int mTextAlignment = SwingConstants.LEFT; // Default to left alignment
    private boolean wrapEnabled = true; // Flag to enable or disable wrapping
    private List<String> cachedWrappedLines = new ArrayList<>(); // Cached wrapped text
    private String cachedText = ""; // Cached original text
    private int cachedWidth = -1; // Cached width for wrapping

    // Default constructor
    public OutlineTextArea() {
        this(1); // Default thickness set to 1
    }

    // Constructor with outline thickness
    public OutlineTextArea(int outlineThickness) {
        super();
        this.mOutlineThickness = outlineThickness;
        setOpaque(true); // Enable background rendering
        setLineWrap(true); // Ensure wrapping
        setWrapStyleWord(true); // Wrap whole words
    }

    public void setOutlineColor(Color outlineColor) {
        mOutlineColor = outlineColor;
        repaint();
    }

    public void setInlineColor(Color inlineColor) {
        mInlineColor = inlineColor;
        repaint();
    }

    public void setOutlineThickness(int outlineThickness) {
        mOutlineThickness = outlineThickness;
        repaint();
    }

    public void setTextAlignment(int alignment) {
        if (alignment != SwingConstants.LEFT && alignment != SwingConstants.CENTER && alignment != SwingConstants.RIGHT) {
            throw new IllegalArgumentException("Invalid alignment. Use SwingConstants.LEFT, CENTER, or RIGHT.");
        }
        mTextAlignment = alignment;
        repaint();
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        repaint();
    }

    /**
     * Enable or disable automatic text wrapping.
     */
    public void setWrapEnabled(boolean enabled) {
        this.wrapEnabled = enabled;
        invalidateCache();
        repaint();
    }

    public boolean isWrapEnabled() {
        return wrapEnabled;
    }

    private void invalidateCache() {
        cachedWrappedLines = new ArrayList<>(); // Use a mutable list
        cachedText = "";
        cachedWidth = -1;
    }

    private List<String> getWrappedLines(FontMetrics metrics, int width) {
        String currentText = getText();

        // If cached values are valid, return them
        if (currentText.equals(cachedText) && width == cachedWidth && !cachedWrappedLines.isEmpty()) {
            return cachedWrappedLines;
        }

        // Otherwise, compute and cache the wrapped lines
        cachedText = currentText;
        cachedWidth = width;

        if (wrapEnabled) {
            cachedWrappedLines = wrapText(currentText, metrics, width);
        } else {
            cachedWrappedLines = new ArrayList<>(List.of(currentText.split(System.lineSeparator()))); // Ensure a mutable list
        }

        return cachedWrappedLines;
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background color explicitly
        if (getBackground() != null) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Custom text rendering with alignment and wrapping
        String text = getText();
        if (text != null && !text.isEmpty()) {
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int lineHeight = metrics.getHeight();
            int width = getWidth();
            int y = getInsets().top + metrics.getAscent();
            int padding = getInsets().left;
            int availableWidth = width - getInsets().left - getInsets().right;

            List<String> lines = getWrappedLines(metrics, availableWidth);

            for (String line : lines) {
                int lineWidth = metrics.stringWidth(line);
                int x;
                // Determine X based on alignment
                if (mTextAlignment == SwingConstants.CENTER) {
                    x = (availableWidth - lineWidth) / 2 + padding;
                } else if (mTextAlignment == SwingConstants.RIGHT) {
                    x = availableWidth - lineWidth + padding;
                } else {
                    x = padding; // Left alignment
                }

                // Draw outline
                g2.setColor(mOutlineColor);
                for (int xOffset = -mOutlineThickness; xOffset <= mOutlineThickness; xOffset++) {
                    for (int yOffset = -mOutlineThickness; yOffset <= mOutlineThickness; yOffset++) {
                        if (xOffset != 0 || yOffset != 0) {
                            g2.drawString(line, x + xOffset, y + yOffset);
                        }
                    }
                }

                // Draw inline text
                g2.setColor(mInlineColor);
                g2.drawString(line, x, y);

                y += lineHeight;
            }
        }

        g2.dispose();
    }

    /**
     * Breaks the input text into lines that fit within the given width.
     */
    private List<String> wrapText(String text, FontMetrics metrics, int width) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int lineWidth = metrics.stringWidth(testLine);

            if (lineWidth > width) {
                // If the word doesn't fit, add the current line to the list
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word); // Start a new line
            } else {
                currentLine.append(currentLine.isEmpty() ? word : " " + word);
            }
        }

        // Add the last line
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());
        int lineHeight = metrics.getHeight();
        int width = getWidth() - getInsets().left - getInsets().right;
        List<String> lines = getWrappedLines(metrics, width);
        int height = (lineHeight * lines.size()) + getInsets().top + getInsets().bottom;
        return new Dimension(super.getPreferredSize().width, height);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            // Custom outline thickness
            OutlineTextArea textArea = new OutlineTextArea(2);
            textArea.setText("This is a long line of text that should automatically wrap based on the available width and word boundaries.");
            textArea.setBackground(Color.YELLOW); // Set custom background color
            textArea.setOutlineColor(Color.RED); // Set outline color for debugging
            textArea.setInlineColor(Color.BLACK); // Set text color
            textArea.setFont(new Font("Arial", Font.PLAIN, 20));
            textArea.setTextAlignment(SwingConstants.CENTER); // Change alignment to CENTER
            textArea.setWrapEnabled(true); // Enable automatic wrapping

            frame.add(textArea);
            frame.setVisible(true);
        });
    }
}