package main.ui.outline;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OutlineLabelsList extends GameUI {

    protected JPanel mButtonPanel = new GameUI();
    protected final Map<String, OutlineButton> mButtonsMap = new HashMap<>();
    protected final Map<String, OutlineCheckBox> mCheckBoxMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineLabelsList(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineLabelsList(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        int buttonPanelWidth = width;
        int buttonPanelHeight = height;
        mButtonPanel = new GameUI();
        mButtonPanel.setLayout(new BoxLayout(mButtonPanel, BoxLayout.Y_AXIS));
        mButtonPanel.setBackground(color);

        add(new NoScrollBarPane(mButtonPanel, buttonPanelWidth, buttonPanelHeight, true, 10));
    }

    public JButton createButton(String value) {
        return createButton(value, false);
    }

    public JButton createButton(String value, boolean isCheckBox) {
        int outlineButtonPanelWidth = (int) getPreferredSize().getWidth();
        int outlineButtonPanelHeight = (int) (getPreferredSize().getHeight() / mVisibleRows);

        JPanel outlineButtonPanel = new GameUI();
        outlineButtonPanel.setPreferredSize(new Dimension(outlineButtonPanelWidth, outlineButtonPanelHeight));

        int checkBoxWidth = 0;
        int checkBoxHeight = 0;
        OutlineCheckBox checkBox = null;
        if (isCheckBox) {
            checkBoxWidth = outlineButtonPanelHeight;
            checkBoxHeight = outlineButtonPanelHeight;
            checkBox = new OutlineCheckBox();
            checkBox.setBorder(BorderFactory.createLoweredSoftBevelBorder());
            checkBox.setFont(FontPool.getInstance().getFontForHeight(checkBoxHeight));
            checkBox.setPreferredSize(new Dimension(checkBoxWidth, checkBoxHeight));
            checkBox.setBackground(mColor);

        }

        int outlineButtonWidth = outlineButtonPanelWidth - checkBoxWidth;
        int outlineButtonHeight = outlineButtonPanelHeight;
        OutlineButton outlineButton = new OutlineButton();
        outlineButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        outlineButton.setPreferredSize(new Dimension(outlineButtonWidth, outlineButtonHeight));
        outlineButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        outlineButton.setHorizontalAlignment(SwingConstants.CENTER);
        outlineButton.setBackground(mColor);
        SwingUiUtils.setHoverEffect(outlineButton);
        outlineButton.setText(value);

        if (checkBox != null) { outlineButtonPanel.add(checkBox); }
        outlineButtonPanel.add(outlineButton);

        mButtonsMap.put(value.toLowerCase(Locale.ROOT), outlineButton);
        if (checkBox != null) { mCheckBoxMap.put(value.toLowerCase(Locale.ROOT), checkBox); }
        mButtonPanel.add(outlineButtonPanel);
        return outlineButton;
    }

    public JButton getButton(String value) {
        return mButtonsMap.get(value.toLowerCase(Locale.ROOT));
    }

    public OutlineCheckBox getCheckBox(String value) {
        return mCheckBoxMap.get(value.toLowerCase(Locale.ROOT));
    }

}
