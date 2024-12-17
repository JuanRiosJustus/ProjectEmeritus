package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class OutlineLabelToTextAreaRows extends GameUI {
    protected JPanel mRowsPanel = new GameUI();
    protected final Map<String, OutlineLabelToTextAreaRow> mRowsMap = new LinkedHashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineLabelToTextAreaRows(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineLabelToTextAreaRows(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        mRowsPanel = new GameUI();
        mRowsPanel.setLayout(new BoxLayout(mRowsPanel, BoxLayout.Y_AXIS));
        mRowsPanel.setBackground(color);

        JScrollPane scrollPane = new NoScrollBarPane(mRowsPanel, width, height, true, 10);
        add(scrollPane);
    }

    public OutlineLabelToTextAreaRow createRow(String id) {
        int outlineButtonPanelWidth = mWidth;
        int outlineButtonPanelHeight = mHeight / mVisibleRows;

        OutlineLabelToTextAreaRow row = mRowsMap.get(id);

        if (row != null) { return row; }


        row = new OutlineLabelToTextAreaRow(
                id,
                "",
                mColor,
                outlineButtonPanelWidth,
                outlineButtonPanelHeight
        );

        int fontHeight = (int) (outlineButtonPanelHeight * .8);
        row.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        row.setBorder(BorderFactory.createLineBorder(Color.RED)); // Visualize row
        row.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        row.setBackground(mColor);
        row.setLeftLabel(id);
        row.getTextArea().setTextAlignment(SwingConstants.RIGHT);
//        SwingUiUtils.setHoverEffect(row);

        mRowsMap.put(id, row);
        mRowsPanel.add(row);

        // Update the preferred size of mRowPanel
        int totalHeight = 0;
        for (int i = 0; i < mRowsPanel.getComponentCount(); i++) {
            Component component = mRowsPanel.getComponent(i);
            totalHeight += (int) component.getPreferredSize().getHeight();
        }
        mRowsPanel.setPreferredSize(new Dimension(outlineButtonPanelWidth, totalHeight));

        return row;
    }

    public OutlineLabelToTextAreaRow getRow(String value) {
        return mRowsMap.get(value);
    }

    public void clear() {
        mRowsMap.clear();
        mRowsPanel.removeAll();
        mRowsPanel.setPreferredSize(new Dimension(mWidth, 0)); // Reset size
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, mRowsPanel);
        if (scrollPane != null) {
            mRowsPanel.revalidate();
            mRowsPanel.repaint();
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);

            OutlineLabelToTextAreaRows rows = new OutlineLabelToTextAreaRows(400, 300, Color.YELLOW, 4);
            rows.createRow("Row 1");
            rows.createRow("Row 2");
            rows.createRow("Row 3");
            rows.createRow("Row 4");
            rows.createRow("Row 5");

            frame.add(rows);
            frame.setVisible(true);
        });
    }
}