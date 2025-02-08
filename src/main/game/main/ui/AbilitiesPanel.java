package main.game.main.ui;

import main.constants.Pair;
import main.constants.StateLock;
import main.game.main.GameController;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineCheckBox;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.*;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class AbilitiesPanel extends GameUI {
    private final StateLock mStateLock = new StateLock();
    private Map<String, Pair<JButton, OutlineCheckBox>> mValueMap = new HashMap<>();
    private String mSelectedAction = null;
    private String mMonitoredEntity = null;
    private int mButtonWidth = -1;
    private int mButtonHeight = -1;
    private JPanel mContentPanel = null;
    private JButton mBannerBackButton = null;
    private JTextField mBannerTextField = null;
//    public AbilitiesPanel(int width, int height, Color color) {
//        super(width, height, color, 4);
//
//        createRow("Basic Attack");
//        createRow("Spiral of Destiny");
//        createRow("Fire Blast");
//        createRow("Dig");
//        createRow("Flamethrower");
//
//        mHeader.getTextField().setText("Actions");
//        mHeader.getTextField().setEditable(false);
//    }

    public AbilitiesPanel(int x, int y, int width, int height, Color color, int visibleRows) {
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
        mBannerTextField.setText("Abilities");
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


        getOrCreateRow("Pillar of Ice");
        getOrCreateRow("Scaled");
        getOrCreateRow("Aqua Jet");
        getOrCreateRow("Defensive Stance");
        getOrCreateRow("Viva Trance");

        setBounds(x, y, width, height);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setActionPanelIsOpen(isShowing);

        String unit = gameController.getCurrentUnitOnTurn();
        if (!mStateLock.isUpdated("ACTIONS", unit)) { return; }

        clear();
        JSONArray actions = gameController.getActionsOfUnit(unit);
        for (int index = 0; index < actions.length(); index++) {
            String action = actions.getString(index);
            Pair<JButton, OutlineCheckBox> checkBoxPair = getOrCreateRow(action);
            JButton abilityButton = checkBoxPair.getFirst();
            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(action));

            abilityButton.addActionListener(e -> {
                mSelectedAction = action;
                mMonitoredEntity = unit;

                mEphemeralObject.clear();
                mEphemeralObject.put("id", unit);
                mEphemeralObject.put("action", action);

                gameController.stageActionForUnit(mEphemeralObject);
            });
        }
    }

    public Pair<JButton, OutlineCheckBox> getOrCreateRow(String button) {
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
        mContentPanel.add(container);

        newRow = new Pair<>(newButton, null);
        mValueMap.put(button, newRow);

        return newRow;
    }


    public String getMonitoredAction() { return mSelectedAction; }
    public String getMonitoredEntity() { return mMonitoredEntity; }
    public JButton getBackButton() { return mBannerBackButton; }

    public void clear() {
        mValueMap.clear();
        mContentPanel.removeAll();
    }
}
