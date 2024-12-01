package main.game.main.ui;

import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineButton;
import main.ui.swing.NoScrollBarPane;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;

public class PokemonStyleBattleUI extends GameUI {

    private JPanel mContentPanel = new GameUI();
    private JPanel mButtonPanel = new GameUI();

    private String[] mButtons = null;
    public PokemonStyleBattleUI(int width, int height, Color color) {
        super(width, height);


        int contentPanelWidth = (int) (width * .75);
        int contentPanelHeight = height;
        mContentPanel = new GameUI();
        mContentPanel.setPreferredSize(new Dimension(contentPanelWidth, contentPanelHeight));
        mContentPanel.setBackground(ColorPalette.getRandomColor(100));

        int buttonPanelWidth = width - contentPanelWidth;
        int buttonPanelHeight = height;
        mButtonPanel = new GameUI();
        mButtonPanel.setLayout(new BoxLayout(mButtonPanel, BoxLayout.Y_AXIS));
//        mButtonPanel.setPreferredSize(new Dimension(buttonPanelWidth, buttonPanelHeight));
        mButtonPanel.setBackground(ColorPalette.getRandomColor());

        mButtons = new String[]{ "Home", "Actions", "Movement", "Status", "Settings", "Exit" };
        for (String button : mButtons) {
            OutlineButton outlineButton = new OutlineButton();
            int outlineButtonWidth = buttonPanelWidth;
            int outlineButtonHeight = buttonPanelHeight / 3;
            int fontHeight = (int) (outlineButtonHeight * .8);
//            outlineButton.setPreferredSize(new Dimension(outlineButtonWidth, outlineButtonHeight));
            outlineButton.setMinimumSize(new Dimension(outlineButtonWidth, outlineButtonHeight));
            outlineButton.setMaximumSize(new Dimension(outlineButtonWidth, outlineButtonHeight));
            outlineButton.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
            outlineButton.setHorizontalAlignment(SwingConstants.CENTER);
            outlineButton.setBackground(color);
            SwingUiUtils.setHoverEffect(outlineButton);
            outlineButton.setText(button);
            mButtonPanel.add(outlineButton);
        }

        mContentPanel.setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
        add(mContentPanel);
//        add(mButtonPanel);
        add(new NoScrollBarPane(mButtonPanel, buttonPanelWidth, buttonPanelHeight, true, 1));

//        SwingUiUtils.setStylizedRaisedBevelBorder(this, 0);
//        setBorder(BorderFactory.createEmptyBorder());
//        setBackground(ColorPalette.TRANSLUCENT_BLACK_V3);
    }
}
