package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.graphics.GameUI;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MovementPanel extends GameUI {
    private final StateLock mStateLock = new StateLock();
    private Map<String, JLabel> mButtonMap = new HashMap<>();
    private Map<String, JLabel> mValueMap = new HashMap<>();
    private JButton mBannerBackButton = null;
    private JTextField mBannerTextField = null;
    private int mButtonWidth = -1;
    private int mButtonHeight = -1;
    private JPanel mContentPanel = null;
    public MovementPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(width, height);

        setBackground(color);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int bannerWidth = width;
        int bannerHeight = (int) (height * .2);
        JPanel bannerRow = new GameUI();
        bannerRow.setLayout(new BoxLayout(bannerRow, BoxLayout.X_AXIS));
        bannerRow.setPreferredSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setMinimumSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setMaximumSize(new Dimension(bannerWidth, bannerHeight));
        bannerRow.setBackground(color);

        int bannerBackButtonWidth = (int) (bannerWidth * .2);
        int bannerBackButtonHeight = bannerHeight;
        mBannerBackButton = new OutlineButton();
        mBannerBackButton.setFont(getFontForHeight(bannerBackButtonHeight));
        mBannerBackButton.setPreferredSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setMinimumSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setMaximumSize(new Dimension(bannerBackButtonWidth, bannerBackButtonHeight));
        mBannerBackButton.setText("<");
        mBannerBackButton.setBackground(color);

        int bannerTextFieldWidth = bannerWidth - bannerBackButtonWidth;
        int bannerTextFieldHeight = bannerHeight;
        mBannerTextField = new OutlineTextField();
        mBannerTextField.setFont(getFontForHeight(bannerTextFieldHeight));
        mBannerTextField.setPreferredSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMinimumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMaximumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setText("Movement");
        mBannerTextField.setBackground(color);
        mBannerTextField.setHorizontalAlignment(SwingConstants.CENTER);

        bannerRow.add(mBannerBackButton);
        bannerRow.add(mBannerTextField);

        add(bannerRow);



        mButtonWidth = width;
        mButtonHeight = (height - bannerHeight) / visibleRows;



        int contentWidth = width;
        int contentHeight = height - bannerHeight;
        mContentPanel = new GameUI();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        mContentPanel.setPreferredSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMinimumSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMaximumSize(new Dimension(contentWidth, contentHeight));
        mContentPanel.setBackground(color);


        add(new NoScrollBarPane(mContentPanel, contentWidth, contentHeight, true, 1));


        getOrCreateRow("Move");
        getOrCreateRow("Speed");
        getOrCreateRow("Climb");
        getOrCreateRow("Jump");
        getOrCreateRow("Other");

        setBounds(x, y, width, height);
    }

    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameController.getCurrentUnitOnTurn();
        if (!mStateLock.isUpdated("MOVES", currentTurnsUnitID)) { return; }

        System.out.println("cCC " + currentTurnsUnitID);
        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentTurnsUnitID);
        JSONObject response = gameController.getMovementStatsForMovementPanel(mEphemeralObject);

        for (String key : response.keySet()) {
            JSONObject nodedata = response.getJSONObject(key);
            int base = nodedata.getInt("base");
            int modified = nodedata.getInt("modified");

            JLabel button = mValueMap.get(key);
            String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
            button.setText(base + " ( " + modifiedSign + Math.abs(modified) + " ) ");
        }
    }


    public JLabel getOrCreateRow(String button) {
        JLabel newButton = mButtonMap.get(button);
        if (newButton != null) { return newButton; }

        int containerWidth = (int) (mButtonWidth * 1);
        int containerHeight = mButtonHeight;
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(containerWidth, containerHeight));
        container.setMinimumSize(new Dimension(containerWidth, containerHeight));
        container.setMaximumSize(new Dimension(containerWidth, containerHeight));
        container.setBackground(getBackground());

        // Create Button (Left Side);
        int newButtonWidth = (int) (containerWidth * .7);
        int newButtonHeight = (int) containerHeight;

        newButton = new OutlineLabel();
        newButton.setBackground(getBackground());
        newButton.setFont(getFontForHeight(newButtonHeight));
        newButton.setPreferredSize(new Dimension(newButtonWidth, newButtonHeight));
        newButton.setMinimumSize(new Dimension(newButtonWidth, newButtonHeight));
        newButton.setMaximumSize(new Dimension(newButtonWidth, newButtonHeight));
        newButton.setBackground(getBackground());
        newButton.setHorizontalAlignment(SwingConstants.LEFT);
        newButton.setText(" " + button);

        int valueButtonWidth = containerWidth - newButtonWidth;
        int valueButtonHeight = newButtonHeight;
        JLabel newValue = new OutlineLabel();
        newValue.setBackground(getBackground());
        newValue.setFont(getFontForHeight(valueButtonHeight));
        newValue.setPreferredSize(new Dimension(valueButtonWidth, valueButtonHeight));
        newValue.setMinimumSize(new Dimension(valueButtonWidth, valueButtonHeight));
        newValue.setMaximumSize(new Dimension(valueButtonWidth, valueButtonHeight));
        newValue.setHorizontalAlignment(SwingConstants.RIGHT);
        newValue.setText("??? ");


        container.add(newButton);
        container.add(newValue);

        mContentPanel.add(container);

        mButtonMap.put(button.toLowerCase(Locale.ROOT), newButton);
        mValueMap.put(button.toLowerCase(Locale.ROOT), newValue);
        return newButton;
    }

//    public JButton getOrCreateRow(String button) {
//        JButton newButton = mButtonMap.get(button);
//        if (newButton != null) { return newButton; }
//
//        JPanel container = new JPanel();
//        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
//        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//        container.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
//        container.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//
//        // Create Button (Left Side);
//        int newButtonWidth = (int) (mButtonWidth * .8);
//        int newButtonHeight = (int) mButtonHeight;
//
//        newButton = new OutlineButton();
//        newButton.setBackground(getBackground());
//        newButton.setFont(getFontForHeight(mButtonHeight));
//        newButton.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setText(button);
//
//        JButton value = new OutlineButton();
//
//        container.add(newButton);
//        mContentPanel.add(container);
//
//        mButtonMap.put(button, newButton);
//        return newButton;
//    }

    public JButton getBackButton() { return mBannerBackButton; }
}
