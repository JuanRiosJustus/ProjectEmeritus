package main.ui.custom;

import main.game.stores.pools.ColorPalette;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

public class ButtonGrid extends JPanel {

    private static final int buttonSizeDivider = 2;
    private int mButtonWidth;
    private int mButtonHeight;
    private int mButtonsPerRow;
    private int mButtonsPerColumn;
    private Map<String, JButton> mButtonMap = new HashMap<>();
    private GridBagConstraints mGridBagConstraints = new GridBagConstraints();

    private JPanel mContainer = new JPanel();
    public ButtonGrid(int width, int height, int buttonsPerRow, int buttonsPerColumn) {
        mContainer.setBackground(ColorPalette.TRANSPARENT);
        mContainer.setLayout(new GridBagLayout());
        mGridBagConstraints.fill = GridBagConstraints.BOTH;
        mGridBagConstraints.weightx = 1;
        mGridBagConstraints.weighty = 1;
        mGridBagConstraints.gridx = 0;
        mGridBagConstraints.gridy = 0;

        mButtonWidth = width / buttonSizeDivider;
        mButtonHeight = height / buttonSizeDivider;
        mButtonsPerColumn = buttonsPerColumn;
        mButtonsPerRow = buttonsPerRow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setBackground(ColorPalette.TRANSPARENT);
        add(SwingUiUtils.createBonelessScrollingPane(width, height, mContainer));
    }
    public JButton addButton(String button) {
        JButton b3 = new JButton(button);

        SwingUiUtils.stylizeButtons(b3, Color.WHITE);
        mGridBagConstraints.gridx = mButtonMap.size() % mButtonsPerColumn;
        mGridBagConstraints.gridy = mButtonMap.size() / mButtonsPerRow;
        b3.setBackground(ColorPalette.TRANSPARENT);
        b3.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        b3.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
        b3.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
        mButtonMap.put(button, b3);
        mContainer.add(b3, mGridBagConstraints);

        return b3;
    }

    public boolean contains(String buttonName) {
        return mButtonMap.containsKey(buttonName);
    }

    public void clearGrid() {
        mButtonMap.clear();
        mContainer.removeAll();
    }
}
