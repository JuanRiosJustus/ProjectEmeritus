package main.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Pair;
import main.constants.CheckSum;
import main.game.main.GameController;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledLabel;
import main.ui.game.EscapablePanel;
import main.ui.game.JavaFxUtils;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LesserStatisticsInformationPanel extends EscapablePanel {
    private static final String[] stats = new String[]{
            "health",
            "mana",
            "stamina",
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
    private static final EmeritusLogger mLogger = EmeritusLogger.create(LesserStatisticsInformationPanel.class);
    private final CheckSum mCheckSum = new CheckSum();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mButtonWidth;
    private final int mButtonHeight;
    public LesserStatisticsInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mButtonHeight = getContentHeight() / visibleRows;
        mButtonWidth = getContentWidth();

        setMainContent(mContentPanel);
        getBanner().setText("Statistics");
    }


    public Pair<BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Pair<BeveledLabel, BeveledLabel> newRow = mRows.get(name);
        if (newRow != null) {
            return newRow;
        }

        int rowWidth = (int) (mButtonWidth * .95);
        int rowHeight = mButtonHeight;

        // Create GridPane instead of HBox
        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(rowWidth, rowHeight);
        gridPane.setMinSize(rowWidth, rowHeight);
        gridPane.setMaxSize(rowWidth, rowHeight);

        Color color = mColor;

        BeveledLabel leftLabel = new BeveledLabel(rowWidth / 2, rowHeight, name, color);
        leftLabel.setAlignment(Pos.CENTER_LEFT);
        leftLabel.setFont(getFontForHeight((int) (rowHeight * .8)));

        BeveledLabel rightLabel = new BeveledLabel(rowWidth / 2, rowHeight, RandomUtils.createRandomName(3, 6), color);
        rightLabel.setAlignment(Pos.CENTER_RIGHT);
        rightLabel.setFont(getFontForHeight((int) (rowHeight * .8)));

        // Add constraints to make sure columns resize properly
        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setHgrow(Priority.ALWAYS); // Allows expansion
        leftColumn.setPercentWidth(50); // Ensures left column takes 50% width
        leftColumn.setHalignment(HPos.LEFT);

        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setHgrow(Priority.ALWAYS);
        rightColumn.setPercentWidth(50);
        rightColumn.setHalignment(HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(leftColumn, rightColumn);

        // Add labels to the grid
        gridPane.add(leftLabel, 0, 0); // Left label in first column
        gridPane.add(rightLabel, 1, 0); // Right label in second column

        // Add the row to the content panel
        mContentPanel.getChildren().add(gridPane);
        mContentPanel.setAlignment(Pos.CENTER);

        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
        mRows.put(name, pair);

        return pair;
    }
    public void gameUpdate(GameController gameController) {

        boolean isShowing = isVisible();
        gameController.setStatisticsPanelIsOpen(isShowing);


        JSONObject response = gameController.getCurrentTurnsEntityAndStatisticsCheckSum();
        String id = response.optString("id");
        String checksum = response.optString("checksum");
        if (!mCheckSum.setDefault(id, checksum)) { return; }

        mLogger.info("Checksum has been updated, querying statistics for unit {}", id);
        mRequestObject.clear();
        mRequestObject.put("id", id);
        response = gameController.getStatisticsForUnit(mRequestObject);



        clear();

        for (String stat : stats) {
            JSONObject statData = response.optJSONObject(stat);
            if (statData != null) {
                int base = statData.getInt("base");
                int modified = statData.getInt("modified");

                Pair<BeveledLabel, BeveledLabel> rowData = getOrCreateRow(stat);
                BeveledLabel left = rowData.getFirst();
                left.setText(StringUtils.convertSnakeCaseToCapitalized(stat));

                BeveledLabel right = rowData.getSecond();
                String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
                right.setText(base + " ( " + modifiedSign + Math.abs(modified) + " )");
            } else {
                Pair<BeveledLabel, BeveledLabel> rowData = getOrCreateRow(UUID.randomUUID().toString());
                BeveledLabel left = rowData.getFirst();
                left.setText("---------");
                BeveledLabel right = rowData.getSecond();
                right.setVisible(false);
//                right.setText("");
            }


        }
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }

}
