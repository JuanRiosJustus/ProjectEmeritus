package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Checksum;
import main.constants.Pair;
import main.constants.Tuple;
import main.game.main.GameController;
import main.ui.foundation.BeveledLabel;
import main.ui.game.EscapablePanel;
import main.constants.JavaFXUtils;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MovementInformationPanel extends EscapablePanel {
    private static final String[] stats = new String[]{ "move", "climb", "jump", "speed" };
    private final Checksum mMovementPanelVisibleStateChecksum = new Checksum();
    private final VBox mContentPanel;
    private final Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mRowWidth;
    private final int mRowHeight;
    private int mUnitStateChecksum = 0;
    public MovementInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mRowHeight = getContentHeight() / visibleRows;
        mRowWidth = getContentWidth();

        setMainContent(mContentPanel);
        getBanner().setText("Movement");

        JavaFXUtils.setCachingHints(this);
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

    public void gameUpdate(GameController gameController) {
        boolean isShowing = isVisible();

        gameController.setMovementPanelIsOpen(isShowing);

        // Check that the current entities state will update the ui
        gameController.getCurrentActiveEntityWithStatisticsChecksumAPI(mRequestObject);
        String id = mRequestObject.optString("id");
        int checksum = mRequestObject.optInt("checksum");
        if (mUnitStateChecksum == checksum) { return; }
        mUnitStateChecksum = checksum;


        mRequestObject.clear();
        mRequestObject.put("id", id);
        JSONObject response = gameController.getStatisticsForUnit(mRequestObject);
        clear();

        for (String stat : stats) {
            JSONObject statData = response.getJSONObject(stat);
            int base = statData.getInt("base");
            int modified = statData.getInt("modified");

            Tuple<GridPane, BeveledLabel, BeveledLabel> rowData = getOrCreateRow(stat);
            BeveledLabel left = rowData.getSecond();
            left.setText(StringUtils.convertSnakeCaseToCapitalized(stat));

            BeveledLabel right = rowData.getThird();
            String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
            right.setText(base + " ( " + modifiedSign + Math.abs(modified) + " )");
        }
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }
}
