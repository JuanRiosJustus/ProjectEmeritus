package main.ui.outline;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class OutlineButtonsWithHeader extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, OutlineButton> mRowMap = new LinkedHashMap<>();
    protected final Map<String, OutlineCheckBox> mCheckBoxMap = new LinkedHashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected JPanel mControlsHeader = null;
    protected OutlineButton mControlsHeaderButton = null;
    protected OutlineTextField mControlsHeaderLabel = null;

    public OutlineButtonsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineButtonsWithHeader(int width, int height, Color color, int visibleRows) {
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
        mControlsHeaderLabel.setFont(FontPoolV1.getInstance().getFontForHeight(controlsHeaderLabelHeight));
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

    public OutlineButton createRow(String id) {
        return createRow(id, false);
    }

    public OutlineButton createRow(String id, boolean isCheckBox) {
        int outlineButtonPanelWidth = (int) getPreferredSize().getWidth();
        int outlineButtonPanelHeight = (int) (getPreferredSize().getHeight() / mVisibleRows);

        JPanel outlineButtonPanel = new GameUI();
        outlineButtonPanel.setBackground(mColor);
        outlineButtonPanel.setPreferredSize(new Dimension(outlineButtonPanelWidth, outlineButtonPanelHeight));

        int checkBoxWidth = 0;
        int checkBoxHeight = 0;
        OutlineCheckBox checkBox = null;
        if (isCheckBox) {
            checkBoxWidth = outlineButtonPanelHeight;
            checkBoxHeight = outlineButtonPanelHeight;
            checkBox = new OutlineCheckBox();
            checkBox.setBorder(BorderFactory.createLoweredSoftBevelBorder());
            checkBox.setFont(FontPoolV1.getInstance().getFontForHeight(checkBoxHeight));
            checkBox.setPreferredSize(new Dimension(checkBoxWidth, checkBoxHeight));
            checkBox.setBackground(mColor);
        }

        int rowWidth = outlineButtonPanelWidth - checkBoxWidth;
        int rowHeight = outlineButtonPanelHeight;
        OutlineButton row = new OutlineButton();

        row.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        row.setPreferredSize(new Dimension(rowWidth, rowHeight));
        row.setFont(FontPoolV1.getInstance().getFontForHeight(fontHeight));

        row.setBackground(mColor);
//        SwingUiUtils.setHoverEffect(row);

        row.setText(id);

        if (checkBox != null) { outlineButtonPanel.add(checkBox); }
        outlineButtonPanel.add(row);

        mRowMap.put(id.toLowerCase(Locale.ROOT), row);
        if (checkBox != null) { mCheckBoxMap.put(id.toLowerCase(Locale.ROOT), checkBox); }
        mRowPanel.add(outlineButtonPanel);
        return row;
    }

    public OutlineButton getRow(String value) {
        return mRowMap.get(value.toLowerCase(Locale.ROOT));
    }

    public OutlineCheckBox getCheckBox(String value) {
        return mCheckBoxMap.get(value.toLowerCase(Locale.ROOT));
    }

    public OutlineButton getReturnButton() {
        return mControlsHeaderButton;
    }

    public OutlineTextField getTextField() {
        return mControlsHeaderLabel;
    }

    public void clear() {
        mRowMap.clear();
        mCheckBoxMap.clear();
        mRowPanel.removeAll();
    }
}