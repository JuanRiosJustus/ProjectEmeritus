package main.game.main.ui;

import main.constants.Pair;
import main.constants.SimpleCheckSum;
import main.game.main.GameControllerV1;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class AbilitySelectionPanelV1 extends GameUI {
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private Map<String, Pair<JButton, JButton>> mValueMap = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private int mRowWidth = -1;
    private int mRowHeight = -1;
    private JPanel mContentPanel = null;
    private JButton mBannerBackButton = null;
    private JTextField mBannerTextField = null;
    public AbilitySelectionPanelV1(int x, int y, int width, int height, Color color, int visibleRows) {
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
        SwingUiUtils.setHoverEffect(mBannerBackButton);

        int bannerTextFieldWidth = bannerWidth - bannerBackButtonWidth;
        int bannerTextFieldHeight = bannerHeight;
        mBannerTextField = new OutlineTextField();
        mBannerTextField.setFont(getFontForHeight(bannerTextFieldHeight));
        mBannerTextField.setPreferredSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMinimumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMaximumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setText("Abilities");
        mBannerTextField.setBackground(color);
        mBannerTextField.setHorizontalAlignment(SwingConstants.CENTER);

        bannerRow.add(mBannerBackButton);
        bannerRow.add(mBannerTextField);

        add(bannerRow);



        mRowWidth = width;
        mRowHeight = (height - bannerHeight) / visibleRows;



        int contentWidth = width;
        int contentHeight = height - bannerHeight;
        mContentPanel = new GameUI();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        mContentPanel.setPreferredSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMinimumSize(new Dimension(contentWidth, contentHeight));
//        mContentPanel.setMaximumSize(new Dimension(contentWidth, contentHeight));
        mContentPanel.setBackground(color);


        add(new NoScrollBarPane(mContentPanel, contentWidth, contentHeight, true, 1));

//
//        getOrCreateRow("Pillar of Ice");
//        getOrCreateRow("Scaled");
//        getOrCreateRow("Aqua Jet");
//        getOrCreateRow("Defensive Stance");
//        getOrCreateRow("Viva Trance");

        setBounds(x, y, width, height);
    }

    @Override
    public void gameUpdate(GameControllerV1 gameControllerV1) {
        boolean isShowing = isShowing();
        gameControllerV1.setActionPanelIsOpen(isShowing);

        if (!isShowing) {
            mSelectedEntity = null;
            mSelectedAction = null;
            return;
        }

        String unit = gameControllerV1.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("ACTIONS", unit)) { return; }

        clear();
        JSONArray actions = gameControllerV1.getActionsOfUnit(unit);
        for (int index = 0; index < actions.length(); index++) {
            String action = actions.getString(index);

            Pair<JButton, JButton> pair = getOrCreateRow(action);
            JButton detailsButton = pair.getFirst();
            detailsButton.setFont(getFontForHeight(mRowHeight));
            detailsButton.setText("<");

            detailsButton.addActionListener(e -> {
                mSelectedAction = action;
                mSelectedEntity = unit;
            });

            JButton abilityButton = pair.getSecond();
            abilityButton.setFont(getFontForHeight(mRowHeight));
            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(action));

            abilityButton.addActionListener(e -> {
                mEphemeralObject.clear();
                mEphemeralObject.put("id", unit);
                mEphemeralObject.put("action", action);
                gameControllerV1.stageActionForUnit(mEphemeralObject);
            });
        }
    }

    public Pair<JButton, JButton> getOrCreateRow(String button) {
        Pair<JButton, JButton> newRow = mValueMap.get(button);
        if (newRow != null) { return newRow; }

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(mRowWidth, mRowHeight));
        container.setMinimumSize(new Dimension(mRowWidth, mRowHeight));
        container.setMaximumSize(new Dimension(mRowWidth, mRowHeight));

        // Create Button (Left Side);
        int leftButtonWidth = (int) (mRowWidth * .2);
        int leftButtonHeight = mRowHeight;
        JButton leftButton = new OutlineButton();
        leftButton.setBackground(getBackground());
        leftButton.setFont(getFontForHeight(leftButtonHeight));
        leftButton.setPreferredSize(new Dimension(leftButtonWidth, leftButtonHeight));
        leftButton.setMinimumSize(new Dimension(leftButtonWidth, leftButtonHeight));
        leftButton.setMaximumSize(new Dimension(leftButtonWidth, leftButtonHeight));
        leftButton.setText("Left");
        SwingUiUtils.setHoverEffect(leftButton);

        int rightButtonWidth = mRowWidth - leftButtonWidth;
        int rightButtonHeight = rightButtonWidth;
        JButton rightButton = new OutlineButton();
        rightButton.setBackground(getBackground());
        rightButton.setFont(getFontForHeight(rightButtonHeight));
        rightButton.setPreferredSize(new Dimension(rightButtonWidth, rightButtonHeight));
        rightButton.setMinimumSize(new Dimension(rightButtonWidth, rightButtonHeight));
        rightButton.setMaximumSize(new Dimension(rightButtonWidth, rightButtonHeight));
        rightButton.setText("Right");
        SwingUiUtils.setHoverEffect(rightButton);

        container.add(leftButton);
        container.add(rightButton);

        mContentPanel.add(container);

        newRow = new Pair<>(leftButton, rightButton);
        mValueMap.put(button, newRow);

        return newRow;
    }


    public String getSelectedAction() { return mSelectedAction; }
    public String getSelectedEntity() { return mSelectedEntity; }
    public JButton getBackButton() { return mBannerBackButton; }

    public void clear() {
        mValueMap.clear();
        mContentPanel.removeAll();
    }
}
