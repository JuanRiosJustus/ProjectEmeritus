package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
import main.ui.game.GamePanel;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class GreaterAbilityPanel extends GamePanel {
    private EmeritusLogger mLogger = EmeritusLogger.create(GreaterAbilityPanel.class);
    private Color mColor;
    private VBox mContentPanel;
    private UnitPreviewPanel mUnitPreviewPanel;
    private int mCurrentStatsHash = -1;
    private int mCurrentAbilityHash = -1;
    private String mCurrentID = null;
    private int mRowWidth = 0;
    private int mRowHeight = 0;

    private Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new LinkedHashMap<>();
    private VBox mRowsPanel = new VBox();

    public GreaterAbilityPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        mRowWidth = width;
        mRowHeight = (int) (height * .05);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

//        var rrr = new Insets();
        int targetPanelWidth = mRowWidth;
        int targetPanelHeight = (int) (mRowWidth * .6);
        mUnitPreviewPanel = new UnitPreviewPanel(targetPanelWidth, (int) (targetPanelHeight * 1), mColor);

        // Create statistics panel
        // LABEL
        BeveledButton mStatisticsPanelLabel = new BeveledButton(mRowWidth, mRowHeight, mColor);
        HBox row4 = new HBox(mStatisticsPanelLabel);

        mContentPanel.getChildren().addAll(
                mUnitPreviewPanel,
                row4,
                mRowsPanel
        );


        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
    }

    public void gameUpdate(GameModel gameModel) {
        gameModel.updateIsGreaterAbilityPanelOpen(isVisible());
        // Determine if the panel should be open
        // Check that the current entities state will update the ui
        String entityID = gameModel.getActiveUnitID();
        int statsHash = gameModel.getEntityStatisticsComponentHashCode(entityID);
        int abilityHash = gameModel.getEntityAbilityComponentHashCode(entityID);


        if (statsHash == mCurrentStatsHash && abilityHash == mCurrentAbilityHash && entityID == mCurrentID) { return; }
        mCurrentStatsHash = statsHash;
        mCurrentAbilityHash = abilityHash;
        mCurrentID = entityID;


        mLogger.info("Started updating Greater Ability Information Panel");
        mUnitPreviewPanel.gameUpdate(gameModel, entityID);
        setupAbilityInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating Greater Ability Information Panel");
    }


    public void gameUpdate(GameModel gameModel, AbilitySelectionPanel abilitySelectionPanel) {
        gameModel.updateIsGreaterAbilityPanelOpen(isVisible());
        // Determine if the panel should be open
        // Check that the current entities state will update the ui
        String entityID = abilitySelectionPanel.getCurrentID();
        int statsHash = gameModel.getEntityStatisticsComponentHashCode(entityID);
        int abilityHash = gameModel.getEntityAbilityComponentHashCode(entityID);


        if (statsHash == mCurrentStatsHash && abilityHash == mCurrentAbilityHash && entityID == mCurrentID) { return; }
        mCurrentStatsHash = statsHash;
        mCurrentAbilityHash = abilityHash;
        mCurrentID = entityID;


        mLogger.info("Started updating Greater Ability Information Panel");
        mUnitPreviewPanel.gameUpdate(gameModel, entityID);
        setupAbilityInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating Greater Ability Information Panel");
    }

    public void setupAbilityInformationPanel(GameModel gameModel, String entityID) {

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);

        String selectedAbility = response.getString("selected_ability");
        if (selectedAbility == null) { return; }

        clear();

        Tuple<GridPane, BeveledLabel, BeveledLabel> label = getOrCreateRow(selectedAbility);

        String fancyAbilityText = StringUtils.convertSnakeCaseToCapitalized(selectedAbility);
        label.getSecond().setText("Ability:");
        label.getThird().setText(fancyAbilityText);

        label = getOrCreateRow("Damage Section");
        label.getSecond().setText("Damage");
        label.getThird().setText("");

//        Map<String, Float> damageMap = CombatSystem.getDamageMapping(entityID, selectedAbility, null);
//        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
//            String key = entry.getKey();
//            Float value = entry.getValue();
//            int damage = value.intValue();
//
//            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
//            if (!label.getThird().getText().isEmpty()) {
//                label = getOrCreateRow(key);
//            }
//            label.getThird().setText(damage + " " + fancyText);
//        }
//
        JSONObject damageMap = gameModel.getDamages(entityID, selectedAbility);
        for (String key : damageMap.keySet()) {
            float value = damageMap.getFloatValue(key);

            int damage = (int) value;

            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
            if (!label.getThird().getText().isEmpty()) {
                label = getOrCreateRow(key);
            }
            label.getThird().setText(damage + " " + fancyText);
        }


        label = getOrCreateRow("Cost Section");
        label.getSecond().setText("Cost");
        label.getThird().setText("");

//        Map<String, Float> costMap = CombatSystem.getCostMapping(entityID, selectedAbility);
//        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
//            String key = entry.getKey();
//            Float value = entry.getValue();
//            int cost = value.intValue();
//
//            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
//            if (!label.getThird().getText().isEmpty()) {
//                label = getOrCreateRow(key);
//            }
//            label.getThird().setText(cost + " " + fancyText);
//        }


        request = new JSONObject().fluentPut("id", entityID).fluentPut("ability", selectedAbility);
        JSONObject costMap = gameModel.getCosts(entityID, selectedAbility);
        for (String key : costMap.keySet()) {
            float value = costMap.getFloatValue(key);
            int cost = (int) value;

            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
            if (!label.getThird().getText().isEmpty()) {
                label = getOrCreateRow(key);
            }
            label.getThird().setText(cost + " " + fancyText);
        }

        JSONArray type = new JSONArray();//AbilityTable.getInstance().getType(selectedAbility);
        label = getOrCreateRow("type");
        label.getSecond().setText("Type:");
        label.getThird().setText(type.toString());

        float accuracy = AbilityTable.getInstance().getAccuracy(selectedAbility);
        label = getOrCreateRow("accuracy");
        label.getSecond().setText("Accuracy:");
        label.getThird().setText(StringUtils.floatToPercentage(accuracy));

        int range = AbilityTable.getInstance().getRange(selectedAbility);
        label = getOrCreateRow("Range");
        label.getSecond().setText("Range:");
        label.getThird().setText(range + "");

        int area = AbilityTable.getInstance().getArea(selectedAbility);
        label = getOrCreateRow("Area");
        label.getSecond().setText("Area:");
        label.getThird().setText(area + "");


        Map<String, Float> modifier = null;

        label = getOrCreateRow("User tags");
        label.getSecond().setText("User Tags");
        label.getThird().setText("------------------");

        JSONArray tags = AbilityTable.getInstance().getTargetTagObjects(selectedAbility);
        label = getOrCreateRow("Target tags");
        label.getSecond().setText("Target Tags");
        label.getThird().setText("------------------");
        for (int i = 0; i < tags.size(); i++) {
            JSONObject tag = tags.getJSONObject(i);
//            String name = AbilityTable.getInstance().getTargetTagObjectName(tag);
//            float chance = AbilityTable.getInstance().getTargetTagObjectChance(tag);
//
//            label = getOrCreateRow(name + "_tag");
//            label.getSecond().setText(StringUtils.convertSnakeCaseToCapitalized(name));
//            label.getThird().setText(StringUtils.floatToPercentage(chance));
        }
    }

    public Tuple<GridPane, BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Tuple<GridPane, BeveledLabel, BeveledLabel> row = mRows.get(name);
        if (row != null) { return  row; }
        row = JavaFXUtils.createBeveledLabelRow(mRowWidth, mRowHeight);

        mRows.put(name, row);
        mRowsPanel.getChildren().add(row.getFirst());
        mRowsPanel.setAlignment(Pos.CENTER);
        return row;
    }

    private void clear() {
        mRows.clear();
        mRowsPanel.getChildren().clear();
//        mStatisticsPanelMap.clear();
//        mStatisticsPanel.getChildren().clear();
//        mTagsPanel.getChildren().clear();
//        mTagsPanelMap.clear();
    }
}
