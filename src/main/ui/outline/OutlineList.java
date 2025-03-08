package main.ui.outline;

import main.game.stores.pools.ColorPaletteV1;
import main.game.stores.pools.FontPoolV1;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OutlineList extends JPanel {
    private final Color mainColor;
    private final JPanel mListPanel;
    private final int mListRowWidths;
    private final int mListRowHeights;
    private final int mAlignment;
    private final Map<String, JComponent> mLabelMap = new HashMap<>();
    private static final float NON_HEADER_ROWS_ALWAYS_VISIBLE = 4f;

    public OutlineList(int width, int height) {
        this(ColorPaletteV1.getRandomColor(), width, height, SwingConstants.CENTER);
    }

    public OutlineList(Color mainColor, int width, int height, int alignment) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));
        setOpaque(true);
        this.mainColor = mainColor;

        mAlignment = alignment;

        mListRowWidths = width;
        mListRowHeights = (int) (height / NON_HEADER_ROWS_ALWAYS_VISIBLE);

        // Set up left inner panel with dynamic layout
        mListPanel = new JPanel();
        mListPanel.setLayout(new BoxLayout(mListPanel, BoxLayout.Y_AXIS));
        mListPanel.setBackground(mainColor);

        // Add components to main tile UI panel
        int mScrollPaneWidth = width;
        int mScrollPaneHeight = height;
        NoScrollBarPane scrollPane = new NoScrollBarPane(mListPanel, mScrollPaneWidth, mScrollPaneHeight, false, 5);

        // Add components to the main tile UI panel
        add(scrollPane, BorderLayout.CENTER);
        setBackground(mainColor);
    }

    public OutlineLabel updateRow(String key, String value) {
        int width = mListRowWidths;
        int height = mListRowHeights;
        OutlineLabel label = (OutlineLabel) mLabelMap.get(key);

        if (label == null) {
            label = new OutlineLabel();
            label.setBackground(mainColor);
            label.setText(value);
//        label.setPreferredSize(new Dimension(width, height));
            label.setMinimumSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setHorizontalAlignment(mAlignment);

            mLabelMap.put(key, label);
            mListPanel.add(label);
        } else {
            label.setText(value);
        }

        // Revalidate and repaint to ensure the new label is displayed
        mListPanel.revalidate();
        mListPanel.repaint();
        return label;
    }
    public OutlineLabelToLabelRow updateRowV2(String key, String left, String right) {
        int width = mListRowWidths;
        int height = mListRowHeights;
        OutlineLabelToLabelRow label = (OutlineLabelToLabelRow) mLabelMap.get(key);

        if (label == null) {
            label = new OutlineLabelToLabelRow(width, height);
            label.setBackground(mainColor);
//        label.setPreferredSize(new Dimension(width, height));
            label.setMinimumSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setFont(FontPoolV1.getInstance().getFontForHeight(height));

            label.setLeftLabel(left);
            label.setRightLabel(right);

            mLabelMap.put(key, label);
            mListPanel.add(label);
        } else {
            label.setLeftLabel(left);
            label.setRightLabel(right);
        }

        // Revalidate and repaint to ensure the new label is displayed
        mListPanel.revalidate();
        mListPanel.repaint();
        return label;
    }

    public OutlineImageToLabelToLabel updateRowV3(String key, String left, String right) {
        int width = mListRowWidths;
        int height = mListRowHeights;
        OutlineImageToLabelToLabel label = (OutlineImageToLabelToLabel) mLabelMap.get(key);

        if (label == null) {
            label = new OutlineImageToLabelToLabel("", mainColor, width, height);
            label.setBackground(mainColor);

            label.setMinimumSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setFont(FontPoolV1.getInstance().getFontForHeight(height));

            label.setLeftLabel(left);
            label.setRightLabel(right);
            label.setFont(FontPoolV1.getInstance().getFontForHeight((int) (height * .75)));

            mLabelMap.put(key, label);
            mListPanel.add(label);
        } else {
            label.setLeftLabel(left);
            label.setRightLabel(right);
        }

        // Revalidate and repaint to ensure the new label is displayed
        mListPanel.revalidate();
        mListPanel.repaint();
        return label;
    }

    public void clear()  { mLabelMap.clear(); mListPanel.removeAll(); mListPanel.revalidate(); mListPanel.repaint(); }
}