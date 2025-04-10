package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.HashSlingingSlasher;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.game.main.GameModel;
import main.game.stores.AbilityTable;
import main.game.systems.CombatSystem;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
import main.ui.game.GamePanel;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class GreaterAbilityInformationPanel extends GamePanel {
    private EmeritusLogger mLogger = EmeritusLogger.create(GreaterAbilityInformationPanel.class);
    private HashSlingingSlasher mHashSlingingSlasher = new HashSlingingSlasher();
    private Color mColor;
    private VBox mContentPanel;
    private TargetPanel mTargetPanel;
    private JSONObject mEphemeralResponseContainer = new JSONObject();
    private int mCurrentHashCode = -1;
    private int mRowWidth = 0;
    private int mRowHeight = 0;

    private Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new LinkedHashMap<>();
    private VBox mRowsPanel = new VBox();

    public GreaterAbilityInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        mRowWidth = width;
        mRowHeight = (int) (height * .05);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        int targetPanelWidth = mRowWidth;
        int targetPanelHeight = (int) (mRowWidth * .6);
        mTargetPanel = new TargetPanel(targetPanelWidth, (int) (targetPanelHeight * 1), mColor);

        // Create statistics panel
        // LABEL
        BeveledButton mStatisticsPanelLabel = new BeveledButton(mRowWidth, mRowHeight, "Ability", mColor);
        HBox row4 = new HBox(mStatisticsPanelLabel);

        mContentPanel.getChildren().addAll(
                mTargetPanel,
                row4,
                mRowsPanel
        );


        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
    }

    public void gameUpdate(GameModel gameModel) {
        // Determine if the panel should be open
        // Check that the current entities state will update the ui
        int newHashCode = gameModel.getCurrentActiveEntityStatisticsHashCode();
        if (newHashCode == mCurrentHashCode) { return; }
        mCurrentHashCode = newHashCode;


        String currentEntityID = gameModel.getCurrentActiveEntityID();
        mTargetPanel.gameUpdate(gameModel, currentEntityID);
        setupWithSelectedAbility(gameModel, currentEntityID);
        mLogger.info("Finished updating Greater UI");
    }

    public void setupWithSelectedAbility(GameModel gameModel, JSONObject request) {
        String id = request.getString("id");
        setupWithSelectedAbility(gameModel, id);
    }
    public void setupWithSelectedAbility(GameModel gameModel, String entityID) {

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);

        String selectedAbility = response.optString("selected_ability", null);
        if (selectedAbility == null) { return; }

        clear();

        Tuple<GridPane, BeveledLabel, BeveledLabel> label = getOrCreateRow(selectedAbility);

        String fancyAbilityText = StringUtils.convertSnakeCaseToCapitalized(selectedAbility);
        label.getSecond().setText("Ability:");
        label.getThird().setText(fancyAbilityText);

        label = getOrCreateRow("Damage Section");
        label.getSecond().setText("Damage");
        label.getThird().setText("");

        Map<String, Float> damageMap = CombatSystem.getDamageMapping(entityID, selectedAbility);
        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();
            int damage = value.intValue();

            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
            label = getOrCreateRow(key);
            label.getThird().setText(damage + " " + fancyText);
        }

        label = getOrCreateRow("Cost Section");
        label.getSecond().setText("Cost");
        label.getThird().setText("");

        Map<String, Float> costMap = CombatSystem.getCostMapping(entityID, selectedAbility);
        for (Map.Entry<String, Float> entry : costMap.entrySet()) {
            String key = entry.getKey();
            Float value = entry.getValue();
            int cost = value.intValue();

            String fancyText = StringUtils.convertSnakeCaseToCapitalized(key);
            label = getOrCreateRow(key);
            label.getThird().setText(cost + " " + fancyText);
        }

        JSONArray type = AbilityTable.getInstance().getType(selectedAbility);
        label = getOrCreateRow("type");
        label.getSecond().setText("Type:");
        label.getThird().setText(type.toList().toString());

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

    }
    public Tuple<GridPane, BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Tuple<GridPane, BeveledLabel, BeveledLabel> row = mRows.get(name);
        if (row != null) { return  row; }
        row = JavaFXUtils.createBeveledLabelRow((int) (mRowWidth * .95), mRowHeight);

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
