package main.game.main.ui;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.RandomUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

public class UnitLevelTypeNameRowPanel extends GameUI {

    private JPanel mContentPanel = null;
    private int mContentPanelItemHeight = 0;
    private int mContentPanelSpacing = 0;
    private JButton mLevelButton;
    private JButton mTypeButton;
    private JButton mNameButton;
//    public UnitLevelTypeNameRowPanel(int width, int height) { this(width, height, ColorPalette.BEIGE); }
    public UnitLevelTypeNameRowPanel(int width, int height, Color background) {
        super(width, height);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.X_AXIS));
        mContentPanel.setOpaque(true);
        mContentPanel.setBackground(background);

        mContentPanelItemHeight = (int) (height * .9);

        int levelButtonWidth = (int) (width * .2);
        int levelButtonHeight = mContentPanelItemHeight;
        int levelButtonFontSize = (int) (levelButtonHeight * .8);
        mLevelButton = new OutlineButton("Lv." + new Random().nextInt(99));
        mLevelButton.setPreferredSize(new Dimension(levelButtonWidth, levelButtonHeight));
        mLevelButton.setMinimumSize(new Dimension(levelButtonWidth, levelButtonHeight));
        mLevelButton.setMaximumSize(new Dimension(levelButtonWidth, levelButtonHeight));
        mLevelButton.setFont(FontPoolV1.getInstance().getBoldFontForHeight(levelButtonFontSize));
        mLevelButton.setFocusPainted(false);
        mLevelButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mLevelButton.setBackground(background);
        SwingUiUtils.setHoverEffect(mLevelButton);


        int typeButtonWidth = (int) (width * .2);
        int typeButtonHeight = mContentPanelItemHeight;
        int typeButtonFontSize = (int) (typeButtonHeight * .9);
        mTypeButton = new OutlineButton("Earth");
        mTypeButton.setPreferredSize(new Dimension(typeButtonWidth, typeButtonHeight));
        mTypeButton.setMinimumSize(new Dimension(typeButtonWidth, typeButtonHeight));
        mTypeButton.setMaximumSize(new Dimension(typeButtonWidth, typeButtonHeight));
        mTypeButton.setFont(FontPoolV1.getInstance().getBoldFontForHeight(typeButtonFontSize));
        mTypeButton.setFocusPainted(false);
        mTypeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mTypeButton.setBackground(background);
        SwingUiUtils.setHoverEffect(mTypeButton);


        int nameButtonWidth = (int) (width * .55);
        int nameButtonHeight = mContentPanelItemHeight;
        int nameButtonFontSize = nameButtonHeight;
        mNameButton = new OutlineButton(RandomUtils.createRandomName(5,9));
        mNameButton.setPreferredSize(new Dimension(nameButtonWidth, nameButtonHeight));
        mNameButton.setMinimumSize(new Dimension(nameButtonWidth, nameButtonHeight));
        mNameButton.setMaximumSize(new Dimension(nameButtonWidth, nameButtonHeight));
        mNameButton.setFont(FontPoolV1.getInstance().getBoldFontForHeight(nameButtonFontSize));
        mNameButton.setFocusPainted(false);
        mNameButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mNameButton.setBackground(background);
        SwingUiUtils.setHoverEffect(mNameButton);


        // There should be at least width * .05 spacing left
        mContentPanelSpacing = (int) ((width * .05) / 4);

        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        mContentPanel.add(mLevelButton);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        mContentPanel.add(mTypeButton);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));
        mContentPanel.add(mNameButton);
        mContentPanel.add(Box.createRigidArea(new Dimension(mContentPanelSpacing, 0)));

        setOpaque(false);
        add(new NoScrollBarPane(mContentPanel, width, height, false, 1));
    }

    public void setLevelText(String txt) { mLevelButton.setText(txt); }
    public void setTypeButton(String txt)  { mTypeButton.setText(txt); }
    public void setNameButton(String txt) { mNameButton.setText(txt); }
    public int getLevelButtonWidth() { return (int) mLevelButton.getPreferredSize().getWidth(); }
    public int getTypeButtonWidth() { return (int) mTypeButton.getPreferredSize().getWidth(); }
    public int getNameButtonWidth() { return (int) mNameButton.getPreferredSize().getWidth(); }
}
