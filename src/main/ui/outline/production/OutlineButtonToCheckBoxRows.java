package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class OutlineButtonToCheckBoxRows extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, OutlineButtonToCheckBoxRow> mButtonsMap = new LinkedHashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineButtonToCheckBoxRows(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineButtonToCheckBoxRows(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        mRowPanel = new GameUI();
        mRowPanel.setLayout(new BoxLayout(mRowPanel, BoxLayout.Y_AXIS));
        mRowPanel.setBackground(color);

        add(new NoScrollBarPane(mRowPanel, width, height, true, 10));
    }

    public OutlineButtonToCheckBoxRow createRow(String id) { return createRow(id, false); }
    public OutlineButtonToCheckBoxRow createRow(String id, boolean showCheckBox) {
        int outlineButtonPanelWidth = mWidth;
        int outlineButtonPanelHeight = mHeight / mVisibleRows;

        OutlineButtonToCheckBoxRow row = new OutlineButtonToCheckBoxRow(outlineButtonPanelWidth, outlineButtonPanelHeight, mColor);
        SwingUiUtils.setHoverEffect(row.getButton());
        row.setCheckBoxVisible(showCheckBox);

        int fontHeight = (int) (outlineButtonPanelHeight * .9);

        row.getCheckBox().setFont(FontPool.getInstance().getFontForHeight(outlineButtonPanelHeight));
        row.getButton().setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        row.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        row.setBackground(mColor);
        row.getButton().setText(id);
        row.getButton().setBackground(mColor);

        mButtonsMap.put(id, row);
        mRowPanel.add(row);

        // Update the preferred size of the row panel
        int totalHeight = outlineButtonPanelHeight * mButtonsMap.size();
        mRowPanel.setPreferredSize(new Dimension(mWidth, totalHeight));

        // Revalidate and repaint to update layout and scrolling
        mRowPanel.revalidate();
        mRowPanel.repaint();

        return row;
    }

    public JButton getButton(String value) {
        return mButtonsMap.get(value).getButton();
    }
    public void clear() {
        mButtonsMap.clear();
        mRowPanel.removeAll();
    }
}
