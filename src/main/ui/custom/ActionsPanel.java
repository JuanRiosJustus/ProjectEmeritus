package main.ui.custom;

import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.ui.components.Datasheet;
import main.ui.huds.controls.HUD;
import main.ui.huds.controls.v2.AdditionalInfoPane;
import main.utils.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.*;

public class ActionsPanel extends HUD {

    protected DatasheetPanel mDatasheetPanel1;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;
    private TargetFrameTag mUnitTargetFrame = null;
    private ScrollableButtonArray mScrollableButtonGrid;
    private AdditionalInfoPane mInfoPane;
    private final Color mAdditionalInfoColorPane;
    private boolean mShouldShowAdditionDetailPanel = false;


    public ActionsPanel(int width, int height, int x, int y) {
        super(width, height, x, y, ActionsPanel.class.getSimpleName());

        setLayout(new GridBagLayout());
        setBackground(Color.BLUE);

        mAdditionalInfoColorPane = ColorPalette.DARK_RED_V1;

        mDatasheetPanel1 = new DatasheetPanel(width, height);
//        mDatasheetPanel1.setPreferredSize(new Dimension(width, height));
        mDatasheetPanel1.setMinimumSize(new Dimension(width, height));
        mDatasheetPanel1.setMaximumSize(new Dimension(width, height));
        mDatasheetPanel1.setOpaque(true);
        mDatasheetPanel1.setBackground(ColorPalette.GOLD);

        int targetFrameWidth = width;
        int targetFrameHeight = (int) (height * .2);
        mUnitTargetFrame = new TargetFrameTag(targetFrameWidth, targetFrameHeight);

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

        SwingUiUtils.automaticallyStyleButton(button, Color.WHITE);
        gbc.gridy = 1;
        add(button, gbc);
    }


    public DatasheetPanel getActionDatasheet() {
        return mDatasheetPanel1;
    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        if (mCurrentUnit == entity) { return; }
        mCurrentUnit = entity;
        mScrollableButtonGrid.clearGrid();
        Animation animation = null;
        String reference = entity.toString();
        if (entity.get(Animation.class) != null) {

            animation = entity.get(Animation.class);
            Statistics statistics = entity.get(Statistics.class);
            Set<String> abilities = statistics.getAbilities();
            ActionManager actionManager = entity.get(ActionManager.class);
//            mInfoPane.setBackground(ColorPalette.getRandomColor());
            for (String ability : abilities) {
                System.out.println("Ability " + ability);
                if (mScrollableButtonGrid.contains(ability)) {
                    continue;
                }
                JButton abilityButton = mScrollableButtonGrid.addButton(ability.trim());
                SwingUiUtils.automaticallyStyleButton(abilityButton, Color.WHITE);
                SwingUiUtils.removeAllActionListeners(abilityButton);
                abilityButton.addActionListener(e -> {
                    Ability abilityUsed = AbilityPool.getInstance().getAbility(abilityButton.getText());
                    System.out.println(abilityButton.getText() + " ?");
                    if (abilityUsed == null) { return; }
                    mDatasheetPanel1.addRowOutlineLabel("Name", abilityUsed.name);
                    mDatasheetPanel1.addRowOutlineLabel("Type", abilityUsed.getTypes().toString());
                    mDatasheetPanel1.addRowOutlineLabel("Range", String.valueOf(abilityUsed.range));
                    mDatasheetPanel1.addRowOutlineLabel("Area", String.valueOf(abilityUsed.area));
                    mDatasheetPanel1.addRowOutlineLabel("Accuracy", StringUtils.floatToPercent(abilityUsed.accuracy));


                    Datasheet damageDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
                    abilityUsed.getDamageKeys().forEach(dmg ->
                            damageDataSheet.addDatasheetItem(dmg + " Damage: " + abilityUsed.getDamage(entity, dmg)));
                    damageDataSheet.setToolTipText(abilityUsed.damageExpression);

                    Datasheet costDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
                    abilityUsed.getCostKeys().forEach(cost ->
                            costDataSheet.addDatasheetItem(cost + " Cost: " + abilityUsed.getCost(entity, cost)));
                    costDataSheet.setToolTipText(abilityUsed.costExpression);

                    mDatasheetPanel1.addRowComponent("damage", damageDataSheet);
                    mDatasheetPanel1.addRowComponent("cost", costDataSheet);
//                    actionManager.preparing = abilityUsed;

                    mShouldShowAdditionDetailPanel = actionManager.getSelected() != abilityUsed;
                    actionManager.setSelected(abilityUsed);
                });
            }


//            mInfoPane.show(ActionsPanel.class.getSimpleName());
        }

        if (animation != null && mCurrentImage != animation.getFrame(0)) {
            mCurrentImage = animation.getFrame(0);
            mUnitTargetFrame.setImage(animation, reference);
        }

        observing = entity;
//        setup(entity, setupType);
    }
    private Datasheet getDatasheet(int fontSize, Color color) {
        Datasheet ds = new Datasheet();
        ds.setCustomizeDatasheet(FontPool.getInstance().getFont(fontSize), color);
        return ds;
    }

    public boolean shouldShowAdditionalDetailPanel() {
        return mShouldShowAdditionDetailPanel;
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void jSceneUpdate(GameModel model) {
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            set(unit);
        }
        model.setGameState(GameState.SHOW_SELECTED_UNIT_ACTION_PATHING, isShowing());
        if (mInfoPane != null) { mInfoPane.setVisible(this.isVisible()); }
    }
}
