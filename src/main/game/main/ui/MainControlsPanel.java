package main.game.main.ui;

import main.constants.Pair;
import main.game.main.GameAPI;
import main.game.main.GameController;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineCheckBox;
import main.ui.outline.OutlineToggleButton;
import main.ui.outline.production.OutlineButtonToCheckBoxRows;
import main.ui.outline.production.OutlineButtonToCheckBoxRow;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainControlsPanel extends GameUI {
    private int mVisibleRows = 3;
    private int mButtonWidth = -1;
    private int mButtonHeight = -1;
    private JPanel mContainerPanel = null;
    private Map<String, Pair<JButton, OutlineCheckBox>> mValueMap = new HashMap<>();
    public MainControlsPanel(int x, int y, int width, int height, Color color) {
        this(x, y, width, height, color, 4);
    }

    public MainControlsPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(width, height);

        mVisibleRows = visibleRows;
        mButtonWidth = width;
        mButtonHeight = height / mVisibleRows;

        mContainerPanel = new GameUI();
        mContainerPanel.setBackground(color);
        mContainerPanel.setLayout(new GridLayout(0, 1));

        add(new NoScrollBarPane(mContainerPanel, width, height, true, 1));
        setBounds(x, y, width, height);
        setBackground(color);
        mVisibleRows = visibleRows;

        getActionsButton();
        getMoveButton();
        getTeamButton();
        getSettingsButton();
        getEndTurnButton();
    }

    public Pair<JButton, OutlineCheckBox> getOrCreateRow(String button) {
//        JButton newButton = mButtonMap.get(button);
//        if (newButton != null) { return newButton; }

        Pair<JButton, OutlineCheckBox> newRow = mValueMap.get(button);
        if (newRow != null) { return newRow; }

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        container.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
        container.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));

        // Create Button (Left Side);
        JButton newButton = new OutlineButton();
        newButton.setBackground(getBackground());
        newButton.setFont(getFontForHeight(mButtonHeight));
        newButton.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.setText(button);

        container.add(newButton);
        mContainerPanel.add(container);

        newRow = new Pair<>(newButton, null);
        mValueMap.put(button, newRow);

        return newRow;
    }


    public Pair<JButton, OutlineCheckBox> getOrCreateRowWithCheckBox(String name) {
        Pair<JButton, OutlineCheckBox> newRow = mValueMap.get(name);
        if (newRow != null) { return newRow; }
//        JButton newButton = mButtonMap.get(button);
//        if (newButton != null) { return newButton; }

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        container.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
        container.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));

        // Create Button (Left Side)
        int leftButtonWidth = (int) (mButtonWidth * 0.8);
        JButton newButton = new OutlineButton();
        newButton.setBackground(getBackground());
        newButton.setFont(getFontForHeight(mButtonHeight));
        newButton.setPreferredSize(new Dimension(leftButtonWidth, mButtonHeight));
        newButton.setMinimumSize(new Dimension(leftButtonWidth, mButtonHeight));
        newButton.setMaximumSize(new Dimension(leftButtonWidth, mButtonHeight));
        newButton.setText(name);

        // Create Checkbox (Right Side)
        int rightButtonWidth = mButtonWidth - leftButtonWidth;
        OutlineCheckBox newCheckBox = new OutlineCheckBox();
        newCheckBox.setBackground(getBackground());
        newCheckBox.setFont(getFontForHeight(mButtonHeight));
        newCheckBox.setPreferredSize(new Dimension(rightButtonWidth, mButtonHeight));
        newCheckBox.setMinimumSize(new Dimension(rightButtonWidth, mButtonHeight));
        newCheckBox.setMaximumSize(new Dimension(rightButtonWidth, mButtonHeight));


        container.add(newButton);
        container.add(newCheckBox);
        mContainerPanel.add(container);

        newRow = new Pair<>(newButton, newCheckBox);
        mValueMap.put(name, newRow);

        return newRow;
    }


//    public JButton getOrCreateRowWithCheckBox(String button) {
//        JButton newButton = mButtonMap.get(button);
//        if (newButton != null) { return newButton; }
//
//        // ✅ Fix: Use GridBagLayout for row container instead of BoxLayout
//        JPanel container = new JPanel(new GridBagLayout());
//        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//
//        // Create Button (Left Side)
//        int leftButtonWidth = (int) (mButtonWidth * 0.75);
//        newButton = new OutlineButton();
//        newButton.setBackground(getBackground());
//        newButton.setFont(FontPool.getInstance().getFontForHeight(mButtonHeight));
//        newButton.setPreferredSize(new Dimension(leftButtonWidth, mButtonHeight));
//        newButton.setText(button);
//
//        // Create Checkbox (Right Side)
//        int rightButtonWidth = mButtonWidth - leftButtonWidth;
//        OutlineCheckBox checkbox = new OutlineCheckBox();
//        checkbox.setBackground(getBackground());
//        checkbox.setFont(FontPool.getInstance().getFontForHeight(mButtonHeight));
//        checkbox.setPreferredSize(new Dimension(rightButtonWidth, mButtonHeight));
//
//        // ✅ Fix: Use GridBagConstraints for proper alignment
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.BOTH; // Ensures both components expand
//
//        // Left Button (75%)
//        gbc.gridx = 0;
//        gbc.weightx = 0.75;
//        container.add(newButton, gbc);
//
//        // Right Checkbox (25%)
//        gbc.gridx = 1;
//        gbc.weightx = 0.25;
//        container.add(checkbox, gbc);
//
//        // ✅ Fix: Ensure `mContainerPanel` uses proper layout
//        mContainerPanel.setLayout(new GridBagLayout());
//        gbc = new GridBagConstraints();
//        gbc.gridy = mContainerPanel.getComponentCount();
//        gbc.weightx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        mContainerPanel.add(container, gbc);
//
//        mButtonMap.put(button, newButton);
//        return newButton;
//    }

//    public JButton getOrCreateRowWithCheckBox(String button) {
//
//        JButton newButton = mButtonMap.get(button);
//        if (newButton != null) { return newButton; }
//
//        JPanel container = new GameUI();
//        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
//        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
////        container.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
////        container.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//
//
//        int newLeftButtonWidth = (int) (mButtonWidth * .75);
//        int newLeftButtonHeight = mButtonHeight;
//        newButton = new OutlineButton();
//        newButton.setBackground(getBackground());
//        newButton.setFont(FontPool.getInstance().getFontForHeight(newLeftButtonHeight));
//        newButton.setPreferredSize(new Dimension(newLeftButtonWidth, newLeftButtonHeight));
////        newButton.setMinimumSize(new Dimension(newLeftButtonWidth, newLeftButtonHeight));
////        newButton.setMaximumSize(new Dimension(newLeftButtonWidth, newLeftButtonHeight));
//        newButton.setText(button);
//
//        int newRightButtonWidth = mButtonWidth - newLeftButtonWidth;
//        int newRightButtonHeight = newLeftButtonHeight;
//        OutlineCheckBox checkbox = new OutlineCheckBox();
//        checkbox.setBackground(getBackground());
//        checkbox.setFont(FontPool.getInstance().getFontForHeight(newRightButtonHeight));
//        checkbox.setPreferredSize(new Dimension(newRightButtonWidth, newRightButtonHeight));
////        checkbox.setMinimumSize(new Dimension(newRightButtonWidth, newRightButtonHeight));
////        checkbox.setMaximumSize(new Dimension(newRightButtonWidth, newRightButtonHeight));
//        checkbox.setText(button);
//
//
//        container.add(newButton);
//        container.add(checkbox);
//
//        mContainerPanel.add(container);
////        mContainerPanel.setPreferredSize(new Dimension(mButtonWidth, 200));
////        mContainerPanel.setMinimumSize(new Dimension(mButtonWidth, 200));
////        mContainerPanel.setMaximumSize(new Dimension(mButtonWidth, 200));
//
//        mButtonMap.put(button, newButton);
//
//        return newButton;
//    }


    public void gameUpdate(GameController gameController) {

        JSONObject currentTurnState = gameController.getCurrentUnitTurnStatus();

        boolean hasActed = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED);
//        OutlineCheckBox checkBox = mCheckBoxMap.get("")
//        getActionsButton().setEnabled(!hasActed);
//        mActionsButton.getCheckBox().setSelected(hasActed);

        boolean hasMoved = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED);
//        getMoveButton().set
//        getMoveButton().setEnabled(!hasMoved);
//        mMoveButton.getCheckBox().setSelected(hasMoved);

    }

    public JButton getActionsButton() { return getOrCreateRowWithCheckBox("Abilities").getFirst(); }
    public OutlineCheckBox getActionsCheckBox() { return getOrCreateRowWithCheckBox("Abilities").getSecond(); }

    public JButton getMoveButton() { return getOrCreateRowWithCheckBox("Movement").getFirst(); }

    public JButton getTeamButton() { return getOrCreateRow("Team").getFirst(); }
//
    public JButton getExitButton() { return getOrCreateRow("Exit").getFirst(); }

    public JButton getSettingsButton() { return getOrCreateRow("Settings").getFirst(); }

    public JButton getEndTurnButton() { return getOrCreateRow("End Of Turn").getFirst(); }


}
