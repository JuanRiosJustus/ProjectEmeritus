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
import javax.swing.border.EmptyBorder;

public class SkillsPanel extends HUD {

    protected DatasheetPanel mDatasheetPanel1;
    protected DatasheetPanel mDatasheetPanel2;
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;
    private TargetFrameTag mUnitTargetFrame = null;
    private ButtonGrid mScrollableButtonGrid;
    private int mBoxWidth;
    private int mBoxHeight;

    private AdditionalInfoPane mInfoPane;
    private Color mAdditionalInfoColorPane;

    public SkillsPanel(int width, int height) {
        this(width, height, null);
    }


    public SkillsPanel(int width, int height, AdditionalInfoPane pane) {
        super(width, height, SkillsPanel.class.getSimpleName());

        setLayout(new GridBagLayout());

        mInfoPane = pane;
        mAdditionalInfoColorPane = ColorPalette.DARK_RED_V1;
        mInfoPane.addAdditionalInfoPanel(SkillsPanel.class.getSimpleName(), getInfoPanel(width, height));

        container = new JPanel();
        container.setBorder(new EmptyBorder(0, 5, 0, 5));
        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new GridBagLayout());

        int targetFrameWidth = width;
        int targetFrameHeight = (int) (height * .3);
        mUnitTargetFrame = new TargetFrameTag(targetFrameWidth, targetFrameHeight);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        container.add(mUnitTargetFrame, gbc);

        int spreadSheetWidth = (int) (width * .95);
        int spreadSheetHeight = height - targetFrameHeight;


        gbc.gridy = 1;

        mScrollableButtonGrid = new ButtonGrid(spreadSheetWidth, spreadSheetHeight, 2, 2);

        container.add(mScrollableButtonGrid, gbc);
        container.setOpaque(true);
        container.setBackground(ColorPalette.TRANSPARENT);

        add(SwingUiUtils.createBonelessScrollingPane(width, height, container));
    }

    private JPanel getInfoPanel(int width, int height) {
        JPanel result  = new JPanel();
        result.setBackground(mAdditionalInfoColorPane);
        result.setOpaque(true);
        result.setLayout(new GridBagLayout());

        int panelWidth = width;
        int panelHeight = height;
        mBoxWidth = panelWidth;
        mBoxHeight = panelHeight / 5;

        mDatasheetPanel1 = new DatasheetPanel(panelWidth, panelHeight);
        mDatasheetPanel1.setPreferredSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel1.setMinimumSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel1.setMaximumSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel1.setBackground(ColorPalette.TRANSPARENT);

        mDatasheetPanel2 = new DatasheetPanel(panelWidth, panelHeight);
        mDatasheetPanel2.setPreferredSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel2.setMinimumSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel2.setMaximumSize(new Dimension(panelWidth, panelHeight));
        mDatasheetPanel2.setBackground(ColorPalette.TRANSPARENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = .5;
        gbc.weighty = .5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        result.add(mDatasheetPanel1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
//        result.add(mDatasheetPanel2, gbc);
        return result;
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
//            mInfoPane.setBackground(ColorPalette.getRandomColor());
            for (String ability : abilities) {
                if (mScrollableButtonGrid.contains(ability)) {
                    continue;
                }
                JButton abilityButton = mScrollableButtonGrid.addButton(ability.trim());
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









//                    Datasheet damageDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
//                    abilityUsed.getDamageKeys().forEach(dmg ->
//                            damageDataSheet.addDatasheetItem(dmg + " dmg: " + abilityUsed.getDamage(entity, dmg)));
//
//                    Datasheet costDataSheet = getDatasheet(16, mAdditionalInfoColorPane);
//                    abilityUsed.getCostKeys().forEach(cost ->
//                            costDataSheet.addDatasheetItem(cost + " cost: " + abilityUsed.getCost(entity, cost)));
//
//
//                    mDatasheetPanel2.addRowComponent("damage", damageDataSheet);
//                    mDatasheetPanel2.addRowComponent("cost", costDataSheet);
                });
            }

            mInfoPane.show(SkillsPanel.class.getSimpleName());
        }
//
//        if (animation == null) { return; }

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

        mInfoPane.setVisible(this.isVisible());
    }
}
