package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.*;
import main.game.main.GameController;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledLabel;
import main.ui.game.EscapablePanel;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LesserStatisticsInformationPanel extends EscapablePanel {
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
    private static final EmeritusLogger mLogger = EmeritusLogger.create(LesserStatisticsInformationPanel.class);
    private final Checksum mChecksum = new Checksum();
    private final VBox mContentPanel;
    private final Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mRowWidth;
    private final int mRowHeight;
    private int mUnitStateChecksum = 0;
    public LesserStatisticsInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
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

    public void gameUpdate(GameController gameController) {

        boolean isShowing = isVisible();
        gameController.setStatisticsPanelIsOpen(isShowing);

        gameController.getCurrentActiveEntityWithStatisticsChecksumAPI(mRequestObject);
        String id = mRequestObject.optString("id");
        int checksum = mRequestObject.optInt("checksum");
        if (mUnitStateChecksum == checksum) { return; }
        mUnitStateChecksum = checksum;

        mLogger.info("Checksum has been updated, querying statistics for unit {}", id);
        mRequestObject.clear();
        mRequestObject.put("id", id);
        JSONObject response = gameController.getStatisticsForEntity(mRequestObject);
        clear();


        Tuple<GridPane, BeveledLabel, BeveledLabel> top = getOrCreateRow("nickname");
        top.getThird().setText("Test");
        JSONObject attributes = response.getJSONObject("attributes");
        for (String attribute : stats) {
            JSONObject statData = attributes.optJSONObject(attribute);
            if (statData != null) {
                int base = statData.getInt("base");
                int modified = statData.getInt("modified");
                int current = statData.getInt("current");

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

}
