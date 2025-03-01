package main.game.main.ui;

import main.constants.Quadruple;
import main.constants.SimpleCheckSum;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;
import main.utils.EmeritusUtils;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class UnitStatisticsPanel extends GameUI {
    protected SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    protected JPanel mContentPanel = null;
    protected UnitLevelTypeNameRowPanel mHeaderRow = null;
    protected UnitPortraitAndResourcesRowPanel mUnitAndResourcesRow = null;
    protected UnitStatusEffectRowPanel mStatusEffectRowRow = null;
    protected UnitKeyAndValuePair mLeftStatisticRows;
    protected UnitKeyAndValuePair mRightStatisticRows;
    protected UnitKeyAndValuePair mUnitEquipments;
    protected UnitKeyAndValuePair mUnitAbilities;
    protected int mSectionLabelWidth = 0;
    protected int mSectionLabelHeight = 0;
    protected int mContentWidth = 0;
    protected int mContentHeight = 0;
    protected final String STATE_LOCK_KEY = "state_lock_key";
    private String mMonitoredUnitEntityID = null;

    public UnitStatisticsPanel(int x, int y, int width, int height, Color color) {
        super(width, height);


        setBackground(color);

        mContentPanel = new GameUI();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
        mContentPanel.setBackground(color);




        mSectionLabelWidth = (int) (width * 1);
        mSectionLabelHeight = (int) (height * .05);

        mContentWidth = (int) (width * 1);
        mContentHeight = (int) (height * .2);



        Color color1 = color;//ColorPalette.getRandomColor();


        /**
         *
         *
         * ------------ ------------    ------    -----------  ------------
         * ************ ************   ********   ***********  ************
         * ----         ------------  ----------  ----    ---  ------------
         * ************     ****     ****    **** *********        ****
         * ------------     ----     ------------ ---------        ----
         *        *****     ****     ************ ****  ****       ****
         * ------------     ----     ----    ---- ----   ----      ----
         * ************     ****     ****    **** ****    ****     ****
         *
         *
         */
        // CREATE HEADER FOR UNIT STATISTICS PANEL
        int headerRowWidth = mSectionLabelWidth;
        int headerRowHeight = (int) (height * .05);
        mHeaderRow = new UnitLevelTypeNameRowPanel(
                headerRowWidth,
                headerRowHeight,
                color1
        );


        // CREATE UNIT PORTRAIT AND RESOURCES
        int unitAndResourceRowWidth = mSectionLabelWidth;
        int unitAndResourceRowHeight = (int) (height * .2);
        mUnitAndResourcesRow = new UnitPortraitAndResourcesRowPanel(
                unitAndResourceRowWidth,
                unitAndResourceRowHeight,
                mHeaderRow.getLevelButtonWidth() + mHeaderRow.getTypeButtonWidth(),
                ColorPalette.getRandomColor()
        );


        /**
         *
         *
         * ------------ ------------    ------    -----------  ------------
         * ************ ************   ********   ***********  ************
         * ----         ------------  ----------  ----    ---  ------------
         * ************     ****     ****    **** *********        ****
         * ------------     ----     ------------ ---------        ----
         *        *****     ****     ************ ****  ****       ****
         * ------------     ----     ----    ---- ----   ----      ----
         * ************     ****     ****    **** ****    ****     ****
         *
         *
         */

        int statusEffectWidth = mSectionLabelWidth;
        int statusEffectHeight = (int) (mSectionLabelHeight);
        mStatusEffectRowRow = new UnitStatusEffectRowPanel(statusEffectWidth, statusEffectHeight, color1);
        for (int i = 0; i < 15; i++) {
//            mStatusEffectRowRow.putStatusEffect(RandomUtils.createRandomName(3, 6));
        }




        /**
         *
         *
         * ------------ ------------    ------    -----------  ------------
         * ************ ************   ********   ***********  ************
         * ----         ------------  ----------  ----    ---  ------------
         * ************     ****     ****    **** *********        ****
         * ------------     ----     ------------ ---------        ----
         *        *****     ****     ************ ****  ****       ****
         * ------------     ----     ----    ---- ----   ----      ----
         * ************     ****     ****    **** ****    ****     ****
         *
         *
         */
        // STATISTICS PANEL
        int statisticsLabelWidth = (int) (mSectionLabelWidth * .975);
        int statisticsLabelHeight = (int) (mSectionLabelHeight);
        JButton statisticsLabel = new OutlineButton("Statistics");
        statisticsLabel.setFont(FontPool.getInstance().getBoldFontForHeight(statisticsLabelHeight));
        statisticsLabel.setPreferredSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
        statisticsLabel.setMinimumSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
        statisticsLabel.setMaximumSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
        statisticsLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        statisticsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statisticsLabel.setBackground(color1);
        SwingUiUtils.setHoverEffect(statisticsLabel);


        int statisticRowsWidths = (int) (width * .49);
        int statisticRowsHeights = (int) (height * .2);
        int statisticRowsPanelWidth = mContentWidth;
        int statisticRowsPanelHeight = statisticRowsHeights;

        mLeftStatisticRows = new UnitKeyAndValuePair(statisticRowsWidths, statisticRowsHeights, 6, color1);
        mRightStatisticRows = new UnitKeyAndValuePair(statisticRowsWidths, statisticRowsHeights, 6,  color1);
        JPanel mStatisticRowsPanel = new GameUI(statisticRowsPanelWidth, statisticRowsPanelHeight);
        mStatisticRowsPanel.setLayout(new BoxLayout(mStatisticRowsPanel, BoxLayout.X_AXIS));
        mStatisticRowsPanel.add(Box.createHorizontalGlue());
        mStatisticRowsPanel.add(mLeftStatisticRows);
        mStatisticRowsPanel.add(Box.createHorizontalGlue());
        mStatisticRowsPanel.add(mRightStatisticRows);
        mStatisticRowsPanel.add(Box.createHorizontalGlue());
        mStatisticRowsPanel.setBackground(color1);



        statisticsLabel.addActionListener(e -> mStatisticRowsPanel.setVisible(!mStatisticRowsPanel.isVisible()));





        /**
         *
         *
         * ------------ ------------    ------    -----------  ------------
         * ************ ************   ********   ***********  ************
         * ----         ------------  ----------  ----    ---  ------------
         * ************     ****     ****    **** *********        ****
         * ------------     ----     ------------ ---------        ----
         *        *****     ****     ************ ****  ****       ****
         * ------------     ----     ----    ---- ----   ----      ----
         * ************     ****     ****    **** ****    ****     ****
         *
         *
         */


        // ABILITIES PANEL
        int abilitiesLabelWidth = (int) (mSectionLabelWidth * .975);
        int abilitiesLabelHeight = (int) (mSectionLabelHeight);
        JButton abilitiesLabel = new OutlineButton("Abilities");
        abilitiesLabel.setFont(FontPool.getInstance().getBoldFontForHeight(abilitiesLabelHeight));
        abilitiesLabel.setPreferredSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
        abilitiesLabel.setMinimumSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
        abilitiesLabel.setMaximumSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
        abilitiesLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        abilitiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        abilitiesLabel.setBackground(color1);
        SwingUiUtils.setHoverEffect(abilitiesLabel);

        int abilitiesPanelWidth = mContentWidth;
        int abilitiesPanelHeight = mContentHeight;
        mUnitAbilities = new UnitKeyAndValuePair(abilitiesPanelWidth, abilitiesPanelHeight,4, color1);

        abilitiesLabel.addActionListener(e -> mUnitAbilities.setVisible(!mUnitAbilities.isVisible()));




        /**
         *
         *
         * ------------ ------------    ------    -----------  ------------
         * ************ ************   ********   ***********  ************
         * ----         ------------  ----------  ----    ---  ------------
         * ************     ****     ****    **** *********        ****
         * ------------     ----     ------------ ---------        ----
         *        *****     ****     ************ ****  ****       ****
         * ------------     ----     ----    ---- ----   ----      ----
         * ************     ****     ****    **** ****    ****     ****
         *
         *
         */

        // EQUIPMENT PANEL
        int equipmentLabelWidth = (int) (mSectionLabelWidth * .975);
        int equipmentLabelHeight = (int) (mSectionLabelHeight);
        JButton equipmentLabel = new OutlineButton("Equipment");
        equipmentLabel.setFont(FontPool.getInstance().getBoldFontForHeight(equipmentLabelHeight));
        equipmentLabel.setPreferredSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
        equipmentLabel.setMinimumSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
        equipmentLabel.setMaximumSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
        equipmentLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        equipmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        equipmentLabel.setBackground(color1);
        SwingUiUtils.setHoverEffect(equipmentLabel);

        int equipmentPanelWidth = mContentWidth;
        int equipmentPanelHeight = (int) (height * .15);
        mUnitEquipments = new UnitKeyAndValuePair(equipmentPanelWidth, equipmentPanelHeight, 3, color1);
//        mUnitEquipments.setVisible(false);



        equipmentLabel.addActionListener(e -> mUnitEquipments.setVisible(!mUnitEquipments.isVisible()));



        mContentPanel.add(mHeaderRow);
        mContentPanel.add(mUnitAndResourcesRow);
        mContentPanel.add(mStatusEffectRowRow); //statisticsLabel

//        mContentPanel.add(statisticsLabelPanel);
        mContentPanel.add(statisticsLabel);
//        JPanel wrapper = SwingUiUtils.createWrapperJPanel(statisticRowsWidths, statisticRowsHeights, mStatisticRowsPanel);
//        mContentPanel.add(wrapper);
        mContentPanel.add(mStatisticRowsPanel);

        mContentPanel.add(abilitiesLabel);
        mContentPanel.add(mUnitAbilities);

        mContentPanel.add(equipmentLabel);
        mContentPanel.add(mUnitEquipments);
        mContentPanel.setBackground(color1);


//        add(new NoScrollBarPane(mContentPanel, mWidth, mHeight, true, 1));
        add(mContentPanel);
//        setBackground(color1);
        setBounds(x, y, width, height);
    }

//    public UnitStatisticsPanel(int width, int height, Color color) {
//        super(width, height);
//
//        setBackground(color);
//
//        mContentPanel = new JPanel();
//        mContentPanel.setBackground(color);
//        mContentPanel.setPreferredSize(new Dimension(width, height));
//        mContentPanel.setMinimumSize(new Dimension(width, height));
//        mContentPanel.setMaximumSize(new Dimension(width, height));
//
//
//
//        mLabelWidth = (int) (width * 1);
//        mLabelHeight = (int) (height * .05);
//
//        mContentWidth = (int) (width * 1);
//        mContentHeight = (int) (height * .15);
//
//
//        Color color1 = ColorPalette.getRandomColor();
//
//        // TODO First row, .05 height used
//        int headerWidth = (int) (width * 1);
//        int headerHeight = mLabelHeight;
//        mHeaderRow = new UnitLevelTypeNameRowPanel(headerWidth, headerHeight, color1);
//
//        // TODO Second row, .2 height used
//        int unitAndResourceRowWidth = width;
//        int unitAndResourceRowHeight = (int) (height * .2);
//        mUnitAndResourcesRow = new UnitPortraitAndResourcesRowPanel(
//                unitAndResourceRowWidth,
//                unitAndResourceRowHeight,
//                mHeaderRow.getLevelButtonWidth() + mHeaderRow.getTypeButtonWidth(),
//                ColorPalette.getRandomColor()
////                color
//        );
//
//        // TODO third row, .25 height used
//        int statusEffectWidth = width;
//        int statusEffectHeight = mLabelHeight;
//        mStatusEffectRowRow = new UnitStatusEffectRowPanel(statusEffectWidth, statusEffectHeight, color1);
//        for (int i = 0; i < 15; i++) {
////            mStatusEffectRowRow.putStatusEffect(RandomUtils.createRandomName(3, 6));
//        }
//
//
//
//        // STATISTICS PANEL
//        int statisticsLabelPanelWidth = mLabelWidth;
//        int statisticsLabelPanelHeight = mLabelHeight;
//        JPanel statisticsLabelPanel = SwingUiUtils.createVerticalCenteredHoldingPanel(
//                statisticsLabelPanelWidth,
//                statisticsLabelPanelHeight
//        );
//        statisticsLabelPanel.setBackground(color1);
//
//        int statisticsLabelWidth = (int) (statisticsLabelPanelWidth * .975);
//        int statisticsLabelHeight = (int) (statisticsLabelPanelHeight * .9);
//        JButton statisticsLabel = new OutlineButton("Statistics");
//        statisticsLabel.setFont(FontPool.getInstance().getBoldFontForHeight(statisticsLabelHeight));
//        statisticsLabel.setPreferredSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
//        statisticsLabel.setMinimumSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
//        statisticsLabel.setMaximumSize(new Dimension(statisticsLabelWidth, statisticsLabelHeight));
//        statisticsLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//        statisticsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        statisticsLabel.setBackground(color1);
//        SwingUiUtils.setHoverEffect(statisticsLabel);
//
//        statisticsLabelPanel.add(Box.createVerticalGlue());
//        statisticsLabelPanel.add(statisticsLabel);
//        statisticsLabelPanel.add(Box.createVerticalGlue());
//
//        int statisticPanelWidths = (int) (width * .475);
//        int statisticPanelHeights = mContentHeight;
//        int statisticPanelWidth = mContentWidth;
//        int statisticPanelHeight = mContentHeight;
//
//        mLeftStatisticRows = new UnitKeyAndValuePair(statisticPanelWidths, statisticPanelHeights, 3, color1);
//        mRightStatisticRows = new UnitKeyAndValuePair(statisticPanelWidths, statisticPanelHeights, 3,  color1);
//        JPanel mStatisticRows = new GameUI(statisticPanelWidth, statisticPanelHeight);
//        mStatisticRows.setLayout(new BoxLayout(mStatisticRows, BoxLayout.X_AXIS));
//        mStatisticRows.add(Box.createHorizontalGlue());
//        mStatisticRows.add(mLeftStatisticRows);
//        mStatisticRows.add(Box.createHorizontalGlue());
//        mStatisticRows.add(mRightStatisticRows);
//        mStatisticRows.add(Box.createHorizontalGlue());
//        mStatisticRows.setBackground(color1);
//
//
//
//        statisticsLabel.addActionListener(e -> mStatisticRows.setVisible(!mStatisticRows.isVisible()));
//
//
//
//
//
//
//        // ABILITIES PANEL
//        int abilitiesLabelPanelWidth = mLabelWidth;
//        int abilitiesLabelPanelHeight = mLabelHeight;
//        JPanel abilitiesLabelPanel = SwingUiUtils.createVerticalCenteredHoldingPanel(
//                abilitiesLabelPanelWidth,
//                abilitiesLabelPanelHeight
//        );
//        abilitiesLabelPanel.setBackground(color1);
//
//        int abilitiesLabelWidth = (int) (abilitiesLabelPanelWidth * .975);
//        int abilitiesLabelHeight = (int) (abilitiesLabelPanelHeight * .9);
//        JButton abilitiessLabel = new OutlineButton("Abilities");
//        abilitiessLabel.setFont(FontPool.getInstance().getBoldFontForHeight(abilitiesLabelHeight));
//        abilitiessLabel.setPreferredSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
//        abilitiessLabel.setMinimumSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
//        abilitiessLabel.setMaximumSize(new Dimension(abilitiesLabelWidth, abilitiesLabelHeight));
//        abilitiessLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//        abilitiessLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        abilitiessLabel.setBackground(color1);
//        SwingUiUtils.setHoverEffect(abilitiessLabel);
//
//        abilitiesLabelPanel.add(Box.createVerticalGlue());
//        abilitiesLabelPanel.add(abilitiessLabel);
//        abilitiesLabelPanel.add(Box.createVerticalGlue());
//
//        int abilitiesPanelWidth = mContentWidth;
//        int abilitiesPanelHeight = mContentHeight;
//        mUnitAbilities = new UnitKeyAndValuePair(abilitiesPanelWidth, abilitiesPanelHeight,3, color1);
//
//        abilitiessLabel.addActionListener(e -> mUnitAbilities.setVisible(!mUnitAbilities.isVisible()));
//
//
//
//
//        // EQUIPMENT PANEL
//        int equipmentLabelPanelWidth = mLabelWidth;
//        int equipmentLabelPanelHeight = mLabelHeight;
//        JPanel equipmentLabelPanel = SwingUiUtils.createVerticalCenteredHoldingPanel(
//                equipmentLabelPanelWidth,
//                equipmentLabelPanelHeight
//        );
//        equipmentLabelPanel.setBackground(color1);
//
//        int equipmentLabelWidth = (int) (equipmentLabelPanelWidth * .975);
//        int equipmentLabelHeight = (int) (equipmentLabelPanelHeight * .9);
//        JButton equipmentLabel = new OutlineButton("Equipment");
//        equipmentLabel.setFont(FontPool.getInstance().getBoldFontForHeight(equipmentLabelHeight));
//        equipmentLabel.setPreferredSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
//        equipmentLabel.setMinimumSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
//        equipmentLabel.setMaximumSize(new Dimension(equipmentLabelWidth, equipmentLabelHeight));
//        equipmentLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//        equipmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//        equipmentLabel.setBackground(color1);
//        SwingUiUtils.setHoverEffect(equipmentLabel);
//
//        equipmentLabelPanel.add(Box.createVerticalGlue());
//        equipmentLabelPanel.add(equipmentLabel);
//        equipmentLabelPanel.add(Box.createVerticalGlue());
//
//        int equipmentPanelWidth = mContentWidth;
//        int equipmentPanelHeight = mContentHeight;
//        mUnitEquipments = new UnitKeyAndValuePair(equipmentPanelWidth, equipmentPanelHeight, 3, color1);
//
//
//
//        equipmentLabel.addActionListener(e -> mUnitEquipments.setVisible(!mUnitEquipments.isVisible()));
//
//
//
//        mContentPanel.add(mHeaderRow);
//        mContentPanel.add(mUnitAndResourcesRow);
//        mContentPanel.add(mStatusEffectRowRow); //statisticsLabel
//
//        mContentPanel.add(statisticsLabelPanel);
//        mContentPanel.add(mStatisticRows);
//
//        mContentPanel.add(abilitiesLabelPanel);
//        mContentPanel.add(mUnitAbilities);
//
//        mContentPanel.add(equipmentLabelPanel);
//        mContentPanel.add(mUnitEquipments);
//        mContentPanel.setBackground(color1);
//
//
//        add(mContentPanel);
////        setBackground(color1);
//    }

    public void gameUpdate(GameController gameController) {
        if (mMonitoredUnitEntityID == null) { setVisible(false); return; }
        setVisible(true);

        JSONObject response = gameController.getSelectedUnitStatisticsHashState();
        int hash = response.optInt("hash", 0);
        if (!mSimpleCheckSum.isUpdated("hash", hash)|| hash == 0) {
            return;
        }

        response = gameController.getUnitAtSelectedTilesForStandardUnitInfoPanel();
        if (response.isEmpty()) {
            return;
        }

        setVisible(true);
        int level = response.getInt("level");
        String type = response.getString("type");
        String nickname = response.getString("nickname");

        JSONArray abilities = response.getJSONArray("abilities");
        JSONObject statistics = response.getJSONObject("statistics");
        JSONArray tags = response.getJSONArray("tags");



        JSONObject health = statistics.getJSONObject("health");
        int baseHealth = health.getInt("base");
        int bonusHealth = health.getInt("modified");
        int currentHealth = health.getInt("current");
        int totalHealth = baseHealth + bonusHealth;

        JSONObject mana = statistics.getJSONObject("mana");
        int baseMana = mana.getInt("base");
        int bonusMana = mana.getInt("modified");
        int currentMana = mana.getInt("current");
        int totalMana = baseMana + bonusMana;

        JSONObject stamina = statistics.getJSONObject("stamina");
        int baseStamina = stamina.getInt("base");
        int bonusStamina= stamina.getInt("modified");
        int currentStamina = stamina.getInt("current");
        int totalStamina = baseStamina + bonusStamina;

        mHeaderRow.setLevelText(EmeritusUtils.getAbbreviation("level") + " " + level);
        mHeaderRow.setTypeButton(StringUtils.convertSnakeCaseToCapitalized(type));
        mHeaderRow.setNameButton(StringUtils.convertSnakeCaseToCapitalized(nickname));


        var healthRow = mUnitAndResourcesRow.createResourceRow("Health");
        healthRow.getSecond().setText("Health");
        healthRow.getThird().setText(currentHealth + "/" + totalHealth);
        healthRow.getFourth().setMax(totalHealth);
        healthRow.getFourth().setCurrent(currentHealth);

        var manaRow = mUnitAndResourcesRow.createResourceRow("Mana");
        manaRow.getSecond().setText("Mana");
        manaRow.getThird().setText(currentMana + "/" + totalMana);
        manaRow.getFourth().setMax(totalMana);
        manaRow.getFourth().setCurrent(currentMana);

        var staminaRow = mUnitAndResourcesRow.createResourceRow("Stamina");
        staminaRow.getSecond().setText("Stamina");
        staminaRow.getThird().setText(currentStamina + "/" + totalStamina);
        staminaRow.getFourth().setMax(totalStamina);
        staminaRow.getFourth().setCurrent(currentStamina);

        String[] leftArrayValues = new String[]{
                "health", "mana", "physical_attack", "physical_defense", "move", "jump"
        };
        String[] rightArrayValues = new String[]{
                "level", "stamina", "magical_attack", "magical_defense", "speed", "climb"
        };

        UnitKeyAndValuePair array = mLeftStatisticRows;

        //array.clear();
        Font fontToUse = FontPool.getInstance().getFontForHeight((int) (array.getRowHeight() * .8));

        for (String value : leftArrayValues) {
            JSONObject statistic = statistics.getJSONObject(value);
            int base = statistic.getInt("base");
            int modified = statistic.getInt("modified");
            int current = statistic.getInt("current");
            int total = base + modified;

            Quadruple<JPanel, JButton, JLabel, JTextArea> row = array.createTextAreaRow(value);

            String abbreviation = EmeritusUtils.getAbbreviation(value);
            String prettyValue = StringUtils.convertSnakeCaseToCapitalized(value);

            row.getSecond().setText(abbreviation);
            row.getSecond().setToolTipText(prettyValue);

            row.getThird().setToolTipText(prettyValue);
            row.getThird().setHorizontalAlignment(SwingConstants.RIGHT);
            String sign = modified > 0 ? "+" : (modified < 0 ? "-" : "");
            row.getThird().setText(" " + base + "  (  " + sign + " " +  modified + "  )");
            row.getThird().setFont(fontToUse);

            row.getFourth().setText(EmeritusUtils.getDescription(value));

        }


        array = mRightStatisticRows;
        //array.clear();
        for (String value : rightArrayValues) {
            JSONObject statistic = statistics.getJSONObject(value);
            int base = statistic.getInt("base");
            int modified = statistic.getInt("modified");
            int current = statistic.getInt("current");
            int total = base + modified;

            Quadruple<JPanel, JButton, JLabel, JTextArea> row = array.createTextAreaRow(value);

            String abbreviation = EmeritusUtils.getAbbreviation(value);
            String prettyValue = StringUtils.convertSnakeCaseToCapitalized(value);

            row.getSecond().setText(abbreviation);
            row.getSecond().setToolTipText(prettyValue);

            row.getThird().setToolTipText(prettyValue);
            row.getThird().setHorizontalAlignment(SwingConstants.RIGHT);
            String sign = modified > 0 ? "+" : (modified < 0 ? "-" : "");
            row.getThird().setText(" " + base + "  (  " + sign + " " +  modified + "  )");
            row.getThird().setFont(fontToUse);

            row.getFourth().setText(EmeritusUtils.getDescription(value));
        }

        mUnitAbilities.clear();
        for (int index = 0; index < abilities.length(); index++) {
            String ability = abilities.getString(index);
            Quadruple<JPanel, JButton, JLabel, JTextArea> row = mUnitAbilities.createTextAreaRow(ability);
            row.getSecond().setText("" + index);
            row.getThird().setText(StringUtils.convertSnakeCaseToCapitalized(ability));
            row.getThird().setFont(fontToUse);
            row.getThird().setFont(getFontForHeight(mUnitAbilities.getRowHeight()));
        }

        mUnitEquipments.clear();
        for (String key : new String[]{ "Head", "Body", "Hands", "Feet", "Accessory 1", "Accessory 2" }) {
            Quadruple<JPanel, JButton, JLabel, JTextArea> row = mUnitEquipments.createTextAreaRow(key);
            row.getSecond().setText(StringUtils.convertSnakeCaseToCapitalized(key));
            row.getThird().setText(StringUtils.convertSnakeCaseToCapitalized(RandomUtils.createRandomName(4, 7)));
            row.getThird().setFont(fontToUse);
        }

        String assetName = response.getString("unit");
        String id = AssetPool.getInstance().getOrCreateAsset(
                (int) (mUnitAndResourcesRow.getUnitPortraitWidth() * .7),
                (int) (mUnitAndResourcesRow.getUnitPortraitHeight() * .9),
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_standard_unit_panel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mUnitAndResourcesRow.setPortraitIcon(new ImageIcon(asset.getAnimation().toImage()));

        mStatusEffectRowRow.clear();
        for (int index = 0; index < tags.length(); index++) {
            JSONObject tagData = tags.getJSONObject(index);
            String tag = tagData.getString("tag");
            int count = tagData.getInt("count");
            JButton button = mStatusEffectRowRow.putStatusEffect(tag);
            button.setToolTipText(tag);
        }

    }

    private boolean selectedUnitHasDifferentState(GameController gameController) {
        JSONObject response = gameController.getSelectedUnitStatisticsHashState();
        boolean shouldUpdate = false;
        if (!response.isEmpty()) {
            // This can only monitor one entity
            for (String hashKey : response.keySet()) {
                float hashValue = response.getFloat(hashKey);
                if (mSimpleCheckSum.isUpdated(STATE_LOCK_KEY, hashValue)) {
                    shouldUpdate = true;
                }
            }
        }
        return shouldUpdate;
    }

    public void setMonitoredUnitEntityID(String unitId) {
        mMonitoredUnitEntityID = unitId;
    }
}
