package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.swing.NoScrollBarPane;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class OutlineRowsWithHeader extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, JComponent> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected OutlineButtonAndOutlineField mHeader = null;
    protected OutlineRows mRows = null;

    public OutlineRowsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        int headerWidth = width;
        int headerHeight = (int) (height * 0.2);
        mHeader = new OutlineButtonAndOutlineField(headerWidth, headerHeight, color);

        int rowsWidth = width;
        int rowsHeight = height - headerHeight;
        mRows = new OutlineRows(rowsWidth, rowsHeight, color);

        add(mHeader);
        add(new NoScrollBarPane(mRowPanel, rowsWidth, rowsHeight, true, 10));
    }


    public JComponent createRow(String id, JComponent component) {
        JComponent row = mRowMap.get(id);
        if (row != null) {
            return row;
        }

        int outlineButtonPanelWidth = getRowWidth();
        int outlineButtonPanelHeight = getRowHeight();

        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        if (component.getParent() != null) {
            component.getParent().remove(component);
        }
        component.setPreferredSize(new Dimension(outlineButtonPanelWidth, outlineButtonPanelHeight));
        component.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        component.setBackground(mColor);

        mRowMap.put(id, component);
        mRowPanel.add(component);

        // Update mRowPanel's preferred size
        int totalHeight = mRowMap.size() * outlineButtonPanelHeight;
        mRowPanel.setPreferredSize(new Dimension(outlineButtonPanelWidth, totalHeight));

        // Revalidate and repaint
        mRowPanel.revalidate();
        mRowPanel.repaint();

        return component;
    }

    public int getRowWidth() { return mWidth; }
    public int getRowHeight() { return (mHeight / mVisibleRows); }

    public void clear() {
        mRowMap.clear();
        mRowPanel.removeAll();
    }

    public boolean isEmpty() { return mRowMap.isEmpty(); }
    public JButton getReturnButton() { return mHeader.getButton(); }
    public JTextField getHeaderField() { return mHeader.getTextField(); }
}