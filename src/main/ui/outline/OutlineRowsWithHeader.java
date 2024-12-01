package main.ui.outline;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.BorderLayout;
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
    protected JPanel mControlsHeader = null;
    protected OutlineButton mControlsHeaderReturnButton = null;
    protected OutlineTextField mControlsHeaderLabel = null;

    public OutlineRowsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        int controlsHeaderHeight = (int) (height * 0.2);
        int controlsHeaderWidth = width;
        mControlsHeader = new GameUI();
        mControlsHeader.setLayout(new BorderLayout());
        mControlsHeader.setPreferredSize(new Dimension(controlsHeaderWidth, controlsHeaderHeight));
        mControlsHeader.setBackground(color);

        // Back button in controls header
        int controlsHeaderButtonWidth = (int) (controlsHeaderWidth * 0.15);
        int controlsHeaderButtonHeight = controlsHeaderHeight;
        mControlsHeaderReturnButton = new OutlineButton("<");
        mControlsHeaderReturnButton.setBackground(color);
        mControlsHeaderReturnButton.setPreferredSize(new Dimension(controlsHeaderButtonWidth, controlsHeaderButtonHeight));
        SwingUiUtils.setHoverEffect(mControlsHeaderReturnButton);
        mControlsHeader.add(mControlsHeaderReturnButton, BorderLayout.WEST);

        // Header label
        int controlsHeaderLabelWidth = controlsHeaderWidth - controlsHeaderButtonWidth;
        int controlsHeaderLabelHeight = controlsHeaderHeight;
        mControlsHeaderLabel = new OutlineTextField();
        mControlsHeaderLabel.setFont(FontPool.getInstance().getFontForHeight(controlsHeaderLabelHeight));
        mControlsHeaderLabel.setPreferredSize(new Dimension(controlsHeaderLabelWidth, controlsHeaderLabelHeight));
        mControlsHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mControlsHeader.add(mControlsHeaderLabel, BorderLayout.CENTER);

        int buttonPanelWidth = width;
        int buttonPanelHeight = height - controlsHeaderHeight;
        mRowPanel = new GameUI();
        mRowPanel.setLayout(new BoxLayout(mRowPanel, BoxLayout.Y_AXIS));
        mRowPanel.setBackground(color);

        add(mControlsHeader);
        add(new NoScrollBarPane(mRowPanel, buttonPanelWidth, buttonPanelHeight, true, 10));
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
    public JButton getReturnButton() { return mControlsHeaderReturnButton; }
    public JTextField getHeaderField() { return mControlsHeaderLabel; }
}