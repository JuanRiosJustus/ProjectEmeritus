package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.constants.Tuple;
import main.game.main.GameController;
import main.game.stores.pools.action.AbilityDatabase;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
// import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextArea;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbilityInformationPanel extends GameUI {
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private Map<String, Tuple<JPanel, JLabel, JComponent>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mMonitoredEntity = null;
    private int mRowWidth = -1;
    private int mRowHeight = -1;
    private JPanel mContentPanel = null;
    private JButton mBannerBackButton = null;
    private JTextField mBannerTextField = null;
    private AbilitySelectionPanel mSelectionPanel = null;
    private static final float mFontMultiplier = .7f;
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

    public AbilityInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
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
        mBannerBackButton.setText("X");
        mBannerBackButton.setBackground(color);
        mBannerBackButton.addActionListener(e -> setVisible(false));
        SwingUiUtils.setHoverEffect(mBannerBackButton);

        int bannerTextFieldWidth = bannerWidth - bannerBackButtonWidth;
        int bannerTextFieldHeight = bannerHeight;
        mBannerTextField = new OutlineTextField();
        mBannerTextField.setFont(getFontForHeight(bannerTextFieldHeight));
        mBannerTextField.setPreferredSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMinimumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setMaximumSize(new Dimension(bannerTextFieldWidth, bannerTextFieldHeight));
        mBannerTextField.setText("Ability Info");
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
        mContentPanel.setBackground(color);


//        int row
//        getOrCreateLabelRow("Name", mRowWidth, mRowHeight, .4f));
//        getOrCreateLabelRow("Description", mRowWidth, mRowHeight, .5f);
//        getOrCreateLabelRow("Lore", mRowWidth, mRowHeight, .4f);
//        getOrCreateLabelRow("Damage", mRowWidth, mRowHeight, .4f);
//        getOrCreateLabelRow("Formula", mRowWidth, mRowHeight, .4f);

//        getOrCreateRow("Pillar of Ice");
//        getOrCreateRow("Scaled");
//        getOrCreateRow("Aqua Jet");
//        getOrCreateRow("Defensive Stance");
//        getOrCreateRow("Viva Trance");
        add(new NoScrollBarPane(mContentPanel, contentWidth, contentHeight, true, 1));
        setBounds(x, y, width, height);
    }



//    public Tuple<JPanel, JLabel, JLabel> getOrCreateLabelRow(String row) {
//    }

    public Tuple<JPanel, JLabel, JComponent> getOrCreateCompleteTextAreaRow(String row) {
        return getOrCreateCompleteTextAreaRow(row, mRowWidth, mRowHeight);
    }
    public Tuple<JPanel, JLabel, JComponent> getOrCreateCompleteTextAreaRow(String row, int rowWidth, int rowHeight) {
        Tuple<JPanel, JLabel, JComponent> currentRow = mRows.get(row);
        if (currentRow != null) { return currentRow; }
        if (rowWidth < 0 || rowHeight < 0) { return  null; }

        int containerWidth = (int) (rowWidth * .9);
        int containerHeight = rowHeight;
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(containerWidth, containerHeight));
        container.setMinimumSize(new Dimension(containerWidth, containerHeight));
        container.setMaximumSize(new Dimension(containerWidth, containerHeight));
        container.setBackground(getBackground());

        Font font = getFontForHeight((int) (containerHeight * mFontMultiplier));

        // Create Button (Right Side);
        int rightLabelWidth = containerWidth;
        int rightLabelHeight = (int) (containerHeight);
        JTextArea rightLabel = new OutlineTextArea();
        rightLabel.setBackground(getBackground());
        rightLabel.setFont(font);
        rightLabel.setPreferredSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMinimumSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMaximumSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setText("???");


//        container.add(Box.createHorizontalGlue());
        container.add(rightLabel);
//        container.add(Box.createHorizontalGlue());
        mContentPanel.add(Box.createVerticalStrut((int) (containerHeight * .1)));
        mContentPanel.add(container);

        currentRow = new Tuple<>(container, null, rightLabel);
        mRows.put(row, currentRow);

        return currentRow;
    }



    public Tuple<JPanel, JLabel, JComponent> getOrCreatePartialTextAreaRow(String row, int rowWidth, int rowHeight, float leftFit) {
        Tuple<JPanel, JLabel, JComponent> currentRow = mRows.get(row);
        if (currentRow != null) { return currentRow; }
        if (rowWidth < 0 || rowHeight < 0 || leftFit > 1) { return  null; }

        int containerWidth = rowWidth;
        int containerHeight = rowHeight;
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(containerWidth, containerHeight));
        container.setMinimumSize(new Dimension(containerWidth, containerHeight));
        container.setMaximumSize(new Dimension(containerWidth, containerHeight));
        container.setBackground(getBackground());

        Font font = getFontForHeight((int) (containerHeight * mFontMultiplier));

        // Create Button (Left Side);
        int leftLabelWidth = (int) (containerWidth * leftFit);
        int leftLabelHeight = (int) containerHeight;
        JLabel leftLabel = new OutlineLabel();
        leftLabel.setBackground(getBackground());
        leftLabel.setFont(font);
        leftLabel.setPreferredSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setMinimumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setMaximumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setBackground(getBackground());
        leftLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftLabel.setText(" " + row);

        // Create Button (Right Side);
        int rightLabelWidth = containerWidth - leftLabelWidth;
        int rightLabelHeight = leftLabelHeight;
        JTextArea rightLabel = new OutlineTextArea();
        rightLabel.setBackground(getBackground());
        rightLabel.setFont(font);
        rightLabel.setPreferredSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMinimumSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMaximumSize(new Dimension(rightLabelWidth, rightLabelHeight));
//        rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightLabel.setText("??? ");


        container.add(leftLabel);
        container.add(rightLabel);

        mContentPanel.add(container);

        currentRow = new Tuple<>(container, leftLabel, rightLabel);
        mRows.put(row, currentRow);

        return currentRow;
    }


    public Tuple<JPanel, JLabel, JComponent> getOrCreateLabelRow(String row, float leftFit) {
        return getOrCreateLabelRow(row, mRowWidth, mRowHeight, leftFit);
    }

    public Tuple<JPanel, JLabel, JComponent> getOrCreateLabelRow(String row, int rowWidth, int rowHeight, float leftFit) {
        Tuple<JPanel, JLabel, JComponent> currentRow = mRows.get(row);
        if (currentRow != null) { return currentRow; }
        if (rowWidth < 0 || rowHeight < 0 || leftFit > 1) { return  null; }

        int containerWidth = (int) (rowWidth * .9);
        int containerHeight = rowHeight;
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setPreferredSize(new Dimension(containerWidth, containerHeight));
        container.setMinimumSize(new Dimension(containerWidth, containerHeight));
        container.setMaximumSize(new Dimension(containerWidth, containerHeight));
        container.setBackground(getBackground());

        Font font = getFontForHeight((int) (containerHeight * mFontMultiplier));

        // Create Button (Left Side);
        int leftLabelWidth = (int) (containerWidth * leftFit);
        int leftLabelHeight = (int) containerHeight;
        JLabel leftLabel = new OutlineLabel();
        leftLabel.setBackground(getBackground());
        leftLabel.setFont(font);
        leftLabel.setPreferredSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setMinimumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setMaximumSize(new Dimension(leftLabelWidth, leftLabelHeight));
        leftLabel.setBackground(getBackground());
        leftLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftLabel.setText(" " + row);

        // Create Button (Right Side);
        int rightLabelWidth = containerWidth - leftLabelWidth;
        int rightLabelHeight = leftLabelHeight;
        JLabel rightLabel = new OutlineLabel();
        rightLabel.setBackground(getBackground());
        rightLabel.setFont(font);
        rightLabel.setPreferredSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMinimumSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setMaximumSize(new Dimension(rightLabelWidth, rightLabelHeight));
        rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightLabel.setText("??? ");


        container.add(leftLabel);
        container.add(rightLabel);

        mContentPanel.add(container);

        currentRow = new Tuple<>(container, leftLabel, rightLabel);
        mRows.put(row, currentRow);

        return currentRow;
    }

    public void gameUpdate(GameController gameController, AbilitySelectionPanel abilitySelectionPanel) {
        mSelectedAction = abilitySelectionPanel.getSelectedAction();
        mSelectionPanel = abilitySelectionPanel;
        setVisible(mSelectedAction != null);
        if (mSelectedAction == null) { setVisible(false); return; }

        gameUpdate(gameController);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setActionPanelIsOpen(isShowing);

//        if (isShowing && gameController.getAbilitySelectedFromUI().isEmpty()) {
//            setVisible(false);
//            gameController.setActionPanelIsOpen(false);
//        }

        String unit = gameController.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("ACTIONS", unit, mSelectedAction)) { return; }

        System.out.println("UPDATING INFORMATION PANEL!");

        String action = mSelectedAction;

        mBannerTextField.setText(StringUtils.convertSnakeCaseToCapitalized(action));

        clear();

        Tuple<JPanel, JLabel, JComponent> row = null;
        row = getOrCreateLabelRow("Type",.4f);
        String text = AbilityDatabase.getInstance().getType(action).toString();
        JLabel label = (JLabel) row.getThird();
        label.setText(text);


        row = getOrCreateCompleteTextAreaRow("Description");
        String descriptionText = AbilityDatabase.getInstance().getDescription(action);
        JTextArea descriptionTextArea = (JTextArea) row.getThird();
        descriptionTextArea.setText(descriptionText);


        row = getOrCreateLabelRow("AreaAndRange",.5f);
        label = row.getSecond();
        label.setText("Area: " + AbilityDatabase.getInstance().getArea(action));
        label = (JLabel) row.getThird();
        label.setText("Range: " + AbilityDatabase.getInstance().getRange(action));


        Set<String> resourcesToTarget = AbilityDatabase.getInstance().getResourcesToDamage(action);
        for (String resource : resourcesToTarget) {
            row = getOrCreateLabelRow(StringUtils.convertSnakeCaseToCapitalized(resource) + " Damage", .8f);

            int totalDamage = AbilityDatabase.getInstance().getTotalDamage(unit, action, resource);
            JLabel damageTypeLabel = (JLabel) row.getThird();
            damageTypeLabel.setText(String.valueOf(totalDamage));

            List<String> calculations = AbilityDatabase.getInstance().getTotalDamageFormula(unit, action, resource);
            for (String calculation : calculations) {
                row = getOrCreateLabelRow(resource + " damage " + calculation,.0f);
                JLabel damageNodeLabel = (JLabel) row.getThird();
                damageNodeLabel.setText(calculation);
            }
        }

        resourcesToTarget = AbilityDatabase.getInstance().getResourcesToCost(action);
        for (String resource : resourcesToTarget) {
            row = getOrCreateLabelRow(StringUtils.convertSnakeCaseToCapitalized(resource) + " Cost",.8f);

            int totalCost = AbilityDatabase.getInstance().getTotalCost(unit, action, resource);
            JLabel costTypeLabel = (JLabel) row.getThird();
            costTypeLabel.setText(String.valueOf(totalCost));

            List<String> calculations = AbilityDatabase.getInstance().getTotalCostFormula(unit, action, resource);
            for (String calculation : calculations) {
                row = getOrCreateLabelRow(resource + " " + calculation,.0f);
                JLabel damageNodeLabel = (JLabel) row.getThird();
                damageNodeLabel.setText(calculation);
            }
        }

//        getOrCreateLabelRow("Lore", mRowWidth, mRowHeight, .4f);
//        getOrCreateLabelRow("Damage", mRowWidth, mRowHeight, .4f);
//        getOrCreateLabelRow("Formula", mRowWidth, mRowHeight, .4f);
    }

//    public Pair<JButton, OutlineCheckBox> getOrCreateRow(String button) {
//        Pair<JButton, OutlineCheckBox> newRow = mValueMap.get(button);
//        if (newRow != null) { return newRow; }
//
//        JPanel container = new JPanel();
//        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
//        container.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//        container.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
//        container.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//
//        // Create Button (Left Side);
//        JButton newButton = new OutlineButton();
//        newButton.setBackground(getBackground());
//        newButton.setFont(getFontForHeight(mButtonHeight));
//        newButton.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setText(button);
//
//        container.add(newButton);
//        mContentPanel.add(container);
//
//        newRow = new Pair<>(newButton, null);
//        mValueMap.put(button, newRow);
//
//        return newRow;
//    }


    public String getMonitoredAction() { return mSelectedAction; }
    public String getMonitoredEntity() { return mMonitoredEntity; }
    public JButton getBackButton() { return mBannerBackButton; }

    public void clear() {
        mRows.clear();
        mContentPanel.removeAll();
    }
}
