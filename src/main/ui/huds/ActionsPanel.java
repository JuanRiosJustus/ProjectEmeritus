package main.ui.huds;

import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.graphics.JScene;
import main.ui.components.Datasheet;
import main.ui.custom.DatasheetPanel;
import main.ui.custom.ScrollableButtonArray;
import main.ui.custom.SwingUiUtils;
import main.utils.StringUtils;

import java.awt.*;
import java.util.Set;

import javax.swing.*;

public class ActionsPanel extends JScene {

    protected DatasheetPanel mDatasheetPanel;
    protected Entity observing = null;
    protected JPanel container;
    private final ScrollableButtonArray mScrollableButtonGrid;
    private final Color mAdditionalInfoColorPane;
    private boolean mShouldShowAdditionDetailPanel = false;
    private Entity mCurrentUnit = null;
    private String lastSelectedAction = null;


    public ActionsPanel(int width, int height, int x, int y) {
        super(width, height, x, y, ActionsPanel.class.getSimpleName());

        setLayout(new GridBagLayout());
        setBackground(Color.BLUE);

        mAdditionalInfoColorPane = ColorPalette.DARK_RED_V1;

        mDatasheetPanel = new DatasheetPanel(width, height);
        mDatasheetPanel.setPreferredSize(new Dimension(width, height));
        mDatasheetPanel.setMinimumSize(new Dimension(width, height));
        mDatasheetPanel.setMaximumSize(new Dimension(width, height));
        mDatasheetPanel.setOpaque(true);
        mDatasheetPanel.setBackground(ColorPalette.getRandomColor());

        container = new JPanel();

        int buttonContainerWidth = width;
        int buttonContainerHeight = (int) (height * .7);
        mScrollableButtonGrid = new ScrollableButtonArray(buttonContainerWidth, buttonContainerHeight);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(mScrollableButtonGrid, gbc);

        JButton button = getExitButton();

        gbc.gridy = 1;
        add(button, gbc);
    }

    public DatasheetPanel getActionDatasheet() {
        return mDatasheetPanel;
    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        if (mCurrentUnit == entity) { return; }
        mCurrentUnit = entity;
        mScrollableButtonGrid.clearGrid();
        Statistics statistics = entity.get(Statistics.class);
        Set<String> abilities = statistics.getAbilities();
        ActionManager actionManager = entity.get(ActionManager.class);
        for (String ability : abilities) {
            System.out.println("Ability " + ability);
            if (mScrollableButtonGrid.contains(ability)) {
                continue;
            }
            JButton abilityButton = mScrollableButtonGrid.addOutlineButton(ability.trim());
            SwingUiUtils.removeAllActionListeners(abilityButton);
            abilityButton.addActionListener(e -> {
                Ability abilityUsed = AbilityPool.getInstance().getAbility(abilityButton.getText());
                if (abilityUsed == null) { return; }
                mDatasheetPanel.addRow("Name", abilityUsed.name);
                mDatasheetPanel.addRow("Type", abilityUsed.getTypes().toString());
                mDatasheetPanel.addRow("Range", String.valueOf(abilityUsed.range));
                mDatasheetPanel.addRow("Area", String.valueOf(abilityUsed.area));
                mDatasheetPanel.addRow("Accuracy", StringUtils.floatToPercent(abilityUsed.accuracy));


                Datasheet damageDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
                abilityUsed.getDamageKeys().forEach(dmg ->
                        damageDataSheet.addDatasheetItem(dmg + " Damage: " + abilityUsed.getDamage(entity, dmg)));
                damageDataSheet.setToolTipText(abilityUsed.damageExpression);

                Datasheet costDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
                abilityUsed.getCostKeys().forEach(cost ->
                        costDataSheet.addDatasheetItem(cost + " Cost: " + abilityUsed.getCost(entity, cost)));
                costDataSheet.setToolTipText(abilityUsed.costExpression);

                mDatasheetPanel.addRowComponent("damage", damageDataSheet);
                mDatasheetPanel.addRowComponent("cost", costDataSheet);

                mShouldShowAdditionDetailPanel = actionManager.getSelected() != abilityUsed;
                actionManager.setSelected(abilityUsed);
                lastSelectedAction = abilityUsed.name;
            });
        }

//        if (animation != null && mCurrentImage != animation.getFrame(0)) {
//            mCurrentImage = animation.getFrame(0);
//            mUnitTargetFrame.setImage(animation, reference);
//        }

        mShouldShowAdditionDetailPanel = false;
        observing = entity;
//        setup(entity, setupType);
    }
    private Datasheet getDatasheet(int fontSize, Color color) {
        Datasheet ds = new Datasheet();
        ds.setCustomizeDatasheet(FontPool.getInstance().getFont(fontSize), color);
        return ds;
    }

    public boolean shouldShowAdditionalDetailPanel() {
        return lastSelectedAction != null;
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void jSceneUpdate(GameModel model) {
        lastSelected = currentSelected;
        currentSelected = model.getGameState().getSelectedEntity();
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            set(unit);
        }
        model.setGameState(GameState.SHOW_SELECTED_UNIT_ACTION_PATHING, isShowing());
    }
}
