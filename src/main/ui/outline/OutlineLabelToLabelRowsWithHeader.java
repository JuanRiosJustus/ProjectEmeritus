package main.ui.outline;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class OutlineLabelToLabelRowsWithHeader extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, OutlineLabelToLabelRow> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected JPanel mControlsHeader = null;
    protected OutlineButton mControlsHeaderButton = null;
    protected OutlineTextField mControlsHeaderLabel = null;

    public OutlineLabelToLabelRowsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineLabelToLabelRowsWithHeader(int width, int height, Color color, int visibleRows) {
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
        mControlsHeaderButton = new OutlineButton("<");
        mControlsHeaderButton.setBackground(color);
        mControlsHeaderButton.setPreferredSize(new Dimension(controlsHeaderButtonWidth, controlsHeaderButtonHeight));
        SwingUiUtils.setHoverEffect(mControlsHeaderButton);
        mControlsHeader.add(mControlsHeaderButton, BorderLayout.WEST);

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

    public OutlineLabelToLabelRow createRow(String id) {
        OutlineLabelToLabelRow row = mRowMap.get(id);
        if (row != null) { return row; }

        int outlineButtonPanelWidth = (int) getPreferredSize().getWidth();
        int outlineButtonPanelHeight = (int) (getPreferredSize().getHeight() / mVisibleRows);

        int rowWidth = outlineButtonPanelWidth;
        int rowHeight = outlineButtonPanelHeight;
        row = new OutlineLabelToLabelRow(rowWidth, rowHeight);

        row.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        row.setPreferredSize(new Dimension(rowWidth, rowHeight));
        row.setFont(FontPool.getInstance().getFontForHeight(fontHeight));

        row.setBackground(mColor);
        SwingUiUtils.setHoverEffect(row);

        row.setLeftLabel(id);

        mRowMap.put(id, row);
        mRowPanel.add(row);
        return row;
    }

    public OutlineLabelToLabelRow getRow(String value) {
        return mRowMap.get(value);
    }

    public OutlineButton getReturnButton() {
        return mControlsHeaderButton;
    }

    public OutlineTextField getTextField() {
        return mControlsHeaderLabel;
    }

    public void clear() {
        mRowMap.clear();
        mRowPanel.removeAll();
    }

    public boolean isEmpty() { return mRowMap.isEmpty(); }

}