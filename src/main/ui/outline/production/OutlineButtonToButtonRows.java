package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OutlineButtonToButtonRows extends GameUI {

    protected JPanel mRowPanel = null;
    protected final Map<String, OutlineButtonToButtonRow> mRowsMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineButtonToButtonRows(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineButtonToButtonRows(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        mRowPanel = new GameUI();
        mRowPanel.setLayout(new BoxLayout(mRowPanel, BoxLayout.Y_AXIS));
        mRowPanel.setBackground(color);

        add(new NoScrollBarPane(mRowPanel, width, height, true, 10));
    }

    public OutlineButtonToButtonRow createRow(String id) {
        return createRow(id, false);
    }

    public OutlineButtonToButtonRow createRow(String id, boolean isCheckBox) {
        int outlineButtonPanelWidth = mWidth;
        int outlineButtonPanelHeight = mHeight / mVisibleRows;

        OutlineButtonToButtonRow row = mRowsMap.get(id);
        if (row != null) { return row; }

        row = new OutlineButtonToButtonRow(outlineButtonPanelWidth, outlineButtonPanelHeight, mColor);
//        row.setLeftButtonVisible(isCheckBox);

        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        row.getLeftButton().setText("*");

        row.getRightButton().setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        row.getRightButton().setHorizontalAlignment(SwingConstants.CENTER);
        row.setBackground(mColor);
        row.getRightButton().setBackground(mColor);

        mRowsMap.put(id, row);
        mRowPanel.add(row);

        // Update the preferred size of the row panel
        int totalHeight = outlineButtonPanelHeight * mRowsMap.size();
        mRowPanel.setPreferredSize(new Dimension(mWidth, totalHeight));

        // Revalidate and repaint to update layout and scrolling
        mRowPanel.revalidate();
        mRowPanel.repaint();

        return row;
    }

    public OutlineButtonToButtonRow getRow(String value) {
        return mRowsMap.get(value.toLowerCase(Locale.ROOT));
    }
    public OutlineButton getReturnButton() {
        return new OutlineButton();
    }

    public OutlineTextField getTextField() {
        return new OutlineTextField();
    }

    public void clear() {
        mRowsMap.clear();
        mRowPanel.removeAll();
    }

    public boolean isEmpty() { return mRowsMap.isEmpty(); }

}