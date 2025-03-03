package main.ui.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import main.constants.Pair;
import main.constants.SimpleCheckSum;
import main.game.main.GameController;
import main.game.main.GameControllerV1;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONObject;

import javax.swing.JLabel;
import java.util.HashMap;
import java.util.Map;

public class MovementInformationPanel extends EscapablePanel {
    private static final String[] stats = new String[]{ "move", "climb", "jump", "speed" };
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mButtonWidth;
    private final int mButtonHeight;
    public MovementInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mButtonHeight = getContentHeight() / visibleRows;
        mButtonWidth = getContentWidth();

        setMainContent(mContentPanel);
        getBanner().setText("Movement");
    }

    public Pair<BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Pair<BeveledLabel, BeveledLabel> newRow = mRows.get(name);
        if (newRow != null) {
            return newRow;
        }

        int rowWidth = (int) (mButtonWidth);
        int rowHeight = mButtonHeight;

        // Create GridPane instead of HBox
        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(rowWidth, rowHeight);
        gridPane.setMinSize(rowWidth, rowHeight);
        gridPane.setMaxSize(rowWidth, rowHeight);
//        gridPane.setPadding(new Insets(0, 10, 0, 10)); // Adds left and right padding
//        gridPane.setHgap(10); // Adds some space between columns

        // Define column constraints for equal spacing
        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setPercentWidth(50); // Left label takes 50% of the row width

        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setPercentWidth(50); // Right label takes 50% of the row width

        gridPane.getColumnConstraints().addAll(leftColumn, rightColumn);

        Color color = mColor;

        BeveledLabel leftLabel = new BeveledLabel(rowWidth / 2, rowHeight, name, color);
        leftLabel.setTextAlignment(Pos.CENTER_LEFT);

        BeveledLabel rightLabel = new BeveledLabel(rowWidth / 2, rowHeight, RandomUtils.createRandomName(3, 6), color);
        rightLabel.setTextAlignment(Pos.CENTER_RIGHT);

        // Add labels to the grid
        gridPane.add(leftLabel, 0, 0); // Left label in first column
        gridPane.add(rightLabel, 1, 0); // Right label in second column

        // Add the row to the content panel
        mContentPanel.getChildren().add(gridPane);

        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
        mRows.put(name, pair);

        return pair;
    }

    public void gameUpdate(GameController gameControllerV1) {
        boolean isShowing = isVisible();
        gameControllerV1.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameControllerV1.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("MOVES", currentTurnsUnitID)) { return; }

        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentTurnsUnitID);
        JSONObject response = gameControllerV1.getStatisticsForUnit(mEphemeralObject);
        clear();

        for (String stat : stats) {
            JSONObject statData = response.getJSONObject(stat);
            int base = statData.getInt("base");
            int modified = statData.getInt("modified");

            Pair<BeveledLabel, BeveledLabel> rowData = getOrCreateRow(stat);
            BeveledLabel left = rowData.getFirst();
            left.setText(" " + StringUtils.convertSnakeCaseToCapitalized(stat));

            BeveledLabel right = rowData.getSecond();
            String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
            right.setText(base + " ( " + modifiedSign + Math.abs(modified) + " ) ");
        }
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }

}
