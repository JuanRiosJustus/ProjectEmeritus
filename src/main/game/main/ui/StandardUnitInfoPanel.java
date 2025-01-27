package main.game.main.ui;

import main.constants.Quadruple;
import main.constants.StateLock;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.utils.EmeritusUtils;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;

public class StandardUnitInfoPanel extends GameUI {
    protected StateLock mStateLock = new StateLock();
    protected JPanel mContentPanel = null;
    protected UnitLevelTypeNameRowPanel mHeaderRow = null;
    protected UnitPortraitAndResourcesRowPanel mUnitAndResourcesRow = null;
    protected UnitStatusEffectRowPanel mStatusEffectRowRow = null;
    protected UnitLetAndRightKeyValuePairs mStatisticValueRows;
    protected UnitKeyAndValuePair mUnitEquipments;
    protected UnitKeyAndValuePair mUnitActions;
    protected int mGenericRowHeight = 0;
    protected final String STATE_LOCK_KEY = "state_lock_key";
    public StandardUnitInfoPanel(int width, int height, Color color) {
        super(width, height);

//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(color);

        mContentPanel = new JPanel();
//        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
        mContentPanel.setBackground(color);
        mContentPanel.setPreferredSize(new Dimension(width, height));
        mContentPanel.setMinimumSize(new Dimension(width, height));
        mContentPanel.setMaximumSize(new Dimension(width, height));


        mGenericRowHeight = (int) (height * .05);

        // TODO First row, .05 height used
        int headerWidth = (int) (width * 1);
        int headerHeight = mGenericRowHeight;
        mHeaderRow = new UnitLevelTypeNameRowPanel(headerWidth, headerHeight, ColorPalette.getRandomColor());

        // TODO Second row, .2 height used
        int unitAndResourceRowWidth = width;
        int unitandResourceRowHeight = mGenericRowHeight * 3;
        mUnitAndResourcesRow = new UnitPortraitAndResourcesRowPanel(
                unitAndResourceRowWidth,
                unitandResourceRowHeight,
                mHeaderRow.getLevelButtonWidth() + mHeaderRow.getTypeButtonWidth(),
                ColorPalette.getRandomColor()
        );

        // TODO third row, .25 height used
        int statusEffectWidth = width;
        int statusEffectHeight = mGenericRowHeight;
        mStatusEffectRowRow = new UnitStatusEffectRowPanel(statusEffectWidth, statusEffectHeight, ColorPalette.getRandomColor());
        for (int i = 0; i < 15; i++) {
//            mStatusEffectRowRow.putStatusEffect(RandomUtils.createRandomName(3, 6));
        }


        int statsPanelWidth = width;
        int statsPanelHeight = mGenericRowHeight * 5;
        mStatisticValueRows = new UnitLetAndRightKeyValuePairs(
                statsPanelWidth,
                statsPanelHeight,
                statsPanelWidth / 2 / 3,
                4,
                ColorPalette.getRandomColor()
        );

        mUnitActions = new UnitKeyAndValuePair(
                statsPanelWidth,
                statsPanelHeight,
                ColorPalette.getRandomColor()
        );

        mUnitEquipments = new UnitKeyAndValuePair(
                statsPanelWidth,
                (int) (mGenericRowHeight * 4.5),
                (int) (statsPanelWidth * .3),
                4,
                ColorPalette.getRandomColor()
        );



        mContentPanel.add(mHeaderRow);
        mContentPanel.add(mUnitAndResourcesRow);
        mContentPanel.add(mStatusEffectRowRow);

        mContentPanel.add(mStatisticValueRows);
        mContentPanel.add(mUnitActions);
        mContentPanel.add(mUnitEquipments);


        add(mContentPanel);
    }

    public void gameUpdate(GameController gameController) {
        JSONObject response = gameController.getSelectedUnitStatisticsHashState();
        int hash = response.optInt("hash", 0);
        if (!mStateLock.isUpdated("hash", hash)|| hash == 0) {
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

        UnitKeyAndValuePair array = mStatisticValueRows.getLeftArray();
        //array.clear();
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
            row.getThird().setHorizontalAlignment(SwingConstants.LEFT);
            String sign = modified > 0 ? "+" : (modified < 0 ? "-" : "");
            row.getThird().setText(" " + base + "  (  " + sign + " " +  modified + "  )");

            row.getFourth().setText(EmeritusUtils.getDescription(value));

        }


        array = mStatisticValueRows.getRightArray();
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
            row.getThird().setHorizontalAlignment(SwingConstants.LEFT);
            String sign = modified > 0 ? "+" : (modified < 0 ? "-" : "");
            row.getThird().setText(" " + base + "  (  " + sign + " " +  modified + "  )");

            row.getFourth().setText(EmeritusUtils.getDescription(value));
        }

        mUnitActions.clear();
        for (int index = 0; index < abilities.length(); index++) {
            String ability = abilities.getString(index);
            Quadruple<JPanel, JButton, JLabel, JTextArea> row = mUnitActions.createTextAreaRow(ability);
            row.getSecond().setText("" + index);
            row.getThird().setText(StringUtils.convertSnakeCaseToCapitalized(ability));
        }

        mUnitEquipments.clear();
        for (String key : new String[]{ "Head", "Body", "Hands", "Feet", "Accessory 1", "Accessory 2" }) {
            Quadruple<JPanel, JButton, JLabel, JTextArea> row = mUnitEquipments.createTextAreaRow(key);
            row.getSecond().setText(StringUtils.convertSnakeCaseToCapitalized(key));
            row.getThird().setText(StringUtils.convertSnakeCaseToCapitalized(RandomUtils.createRandomName(4, 7)));
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
                if (mStateLock.isUpdated(STATE_LOCK_KEY, hashValue)) {
                    shouldUpdate = true;
                }
            }
        }
        return shouldUpdate;
    }
//    private boolean selectedUnitHasDifferentState(GameController gameController) {
//        JSONObject response = gameController.getSelectedUnitStatisticsHashState();
//        boolean result = false;
//        if (response.isEmpty()) {
//            return true;
//        }
//
//        // This can only monitor one entity
//        boolean shouldUpdate = false;
//        for (String hashKey : response.keySet()) {
//            float hashValue = response.getFloat(hashKey);
//            if (mStateLock.isUpdated(STATE_LOCK_KEY, hashValue)) {
//                shouldUpdate = true;
//            }
//        }
//        if (!shouldUpdate) {
//            return true;
//        }
//        return false;
//    }
}
