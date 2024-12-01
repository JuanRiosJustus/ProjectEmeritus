package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineButton;
import main.ui.outline.OutlineCheckBox;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.swing.NoScrollBarPane;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class OutlineButtonToButtonRowsWithHeader extends GameUI {

    protected JPanel mRowPanel = null;
    protected final Map<String, OutlineButtonToButtonRow> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected JPanel mControlsHeader = null;
    protected OutlineButton mControlsHeaderButton = null;
    protected OutlineTextField mControlsHeaderLabel = null;

    public OutlineButtonToButtonRowsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineButtonToButtonRowsWithHeader(int width, int height, Color color, int visibleRows) {
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

    public OutlineButtonToButtonRow createRow(String id) {
        return createRow(id, false);
    }

    public OutlineButtonToButtonRow createRow(String id, boolean isCheckBox) {
        int outlineButtonPanelWidth = mWidth;
        int outlineButtonPanelHeight = mHeight / mVisibleRows;

        OutlineButtonToButtonRow row = new OutlineButtonToButtonRow(outlineButtonPanelWidth, outlineButtonPanelHeight, mColor);
        row.setCheckBoxVisible(isCheckBox);

        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        row.getLeftButton().setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        row.getLeftButton().setHorizontalAlignment(SwingConstants.CENTER);
        row.setBackground(mColor);
        row.getLeftButton().setBackground(mColor);

        mRowMap.put(id, row);
        mRowPanel.add(row);

        // Update the preferred size of the row panel
        int totalHeight = outlineButtonPanelHeight * mRowMap.size();
        mRowPanel.setPreferredSize(new Dimension(mWidth, totalHeight));

        // Revalidate and repaint to update layout and scrolling
        mRowPanel.revalidate();
        mRowPanel.repaint();

        return row;
    }

    public OutlineButtonToButtonRow getRow(String value) {
        return mRowMap.get(value.toLowerCase(Locale.ROOT));
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