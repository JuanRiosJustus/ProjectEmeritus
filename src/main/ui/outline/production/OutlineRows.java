package main.ui.outline.production;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.swing.NoScrollBarPane;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class OutlineRows extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, JComponent> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    public OutlineRows(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineRows(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        add(new NoScrollBarPane(mRowPanel, width, height, true, 10));
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
        component.setFont(FontPoolV1.getInstance().getFontForHeight(fontHeight));
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
}