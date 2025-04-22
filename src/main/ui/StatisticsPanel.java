package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.*;
import main.game.main.GameModel;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledLabel;
import main.ui.game.EscapablePanel;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StatisticsPanel extends EscapablePanel {
    private static final String[] stats = new String[]{
            "health",
            "mana",
            "stamina",
            "",
            "level",
            "experience",
            "",
            "physical_attack",
            "physical_defense",
            "magical_attack",
            "magical_defense",
            "",
            "move",
            "climb",
            "jump",
            "speed"
    };
    private static final Set<String> RESOURCES = Set.of("health", "mana", "stamina");
    private static final EmeritusLogger mLogger = EmeritusLogger.create(StatisticsPanel.class);
    private final VBox mContentPanel;
    private final Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private final int mRowWidth;
    private final int mRowHeight;
    private int mCurrentEntityHash = -1;
    private String mCurrentEntityID = null;
//    private Lis
    public StatisticsPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mRowHeight = getContentHeight() / visibleRows;
        mRowWidth = getContentWidth();

        setMainContent(mContentPanel);
        getBanner().setText("Statistics");

        JavaFXUtils.setCachingHints(this);
    }

    public void gameUpdate(GameModel gameModel) {

        boolean isShowing = isVisible();
        gameModel.updateIsStatisticsPanelOpen(isShowing);

        String selectedEntityID = gameModel.getSelectedEntityID();
        int selectedEntityHash = gameModel.getSpecificEntityStatisticsComponentHash(selectedEntityID);
        if (selectedEntityHash == mCurrentEntityHash && selectedEntityID == mCurrentEntityID) { return; }
        mCurrentEntityHash = selectedEntityHash;
        mCurrentEntityID = selectedEntityID;

        mEphemeralObject.clear();
        mEphemeralObject.put("id", selectedEntityID);
        JSONObject response = gameModel.getStatisticsForEntity(mEphemeralObject);
        clear();

        mLogger.info("Started updating Statistics Information Panel for entity {}", selectedEntityID);

        JSONObject attributes = response.getJSONObject("attributes");
        if (attributes != null) {
            for (String attribute : stats) {
                JSONObject statData = attributes.getJSONObject(attribute);
                if (statData != null) {
                    int base = statData.getIntValue("base");
                    int modified = statData.getIntValue("modified");
                    int current = statData.getIntValue("current");

                    Tuple<GridPane, BeveledLabel, BeveledLabel> rowData = getOrCreateRow(attribute);
                    BeveledLabel left = rowData.getSecond();
                    left.setText(StringUtils.convertSnakeCaseToCapitalized(attribute));

                    String txt = base + " ( " + (modified > 0 ? "+" : modified < 0 ? "-" : "") +  modified + " )";

                    if (RESOURCES.contains(attribute)) {
                        rowData.getThird().setText(current + " / " + txt);
                    } else {
                        rowData.getThird().setText(txt);
                    }
                } else {
                    Tuple<GridPane, BeveledLabel, BeveledLabel> rowData = getOrCreateRow(UUID.randomUUID().toString());
                    BeveledLabel left = rowData.getSecond();
                    left.setText("---------");
                    BeveledLabel right = rowData.getThird();
                    right.setVisible(false);
                }
            }
        }


        mLogger.info("Finished updating Statistics Information Panel for entity {}", selectedEntityID);
    }

    public Tuple<GridPane, BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Tuple<GridPane, BeveledLabel, BeveledLabel> row = mRows.get(name);
        if (row != null) { return row; }

        int rowWidth = (int) (mRowWidth * .95);
        int rowHeight = mRowHeight;
        row = JavaFXUtils.createBeveledLabelRow(rowWidth, rowHeight);

        // Add the row to the content panel
        mContentPanel.getChildren().add(row.getFirst());
        mContentPanel.setAlignment(Pos.CENTER);

        mRows.put(name, row);
        return row;
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }

    public String getCurrentEntityID() { return mCurrentEntityID; }

}
