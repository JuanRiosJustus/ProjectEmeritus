package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Tuple;
import main.game.main.GameModel;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledLabel;
import main.ui.game.EscapablePanel;
import main.constants.JavaFXUtils;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MovementPanel extends EscapablePanel {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(MovementPanel.class);
    private static final String[] stats = new String[]{ "move", "climb", "jump", "speed" };
    private final VBox mContentPanel;
    private final Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private final int mRowWidth;
    private final int mRowHeight;
    private int mCurrentEntityHash = -1;
    private String mCurrentEntityID = null;
    public MovementPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);
        mContentPanel.setAlignment(Pos.CENTER);

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

    public void gameUpdate(GameModel gameModel) {
        boolean isShowing = isVisible();
        gameModel.updateIsMovementPanelOpen(isShowing);

        // Check that the current entities state will update the ui
        String currentEntityID = gameModel.getActiveEntityID();
        int newHashCode = gameModel.getSpecificEntityStatisticsComponentHash(currentEntityID);
        if (newHashCode == mCurrentEntityHash && currentEntityID == mCurrentEntityID) { return; }
        mCurrentEntityHash = newHashCode;
        mCurrentEntityID = currentEntityID;

        if (currentEntityID == null) { return; }
        mLogger.info("Started update to Movement Information panel.");

        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentEntityID);
        JSONObject response = gameModel.getStatisticsForEntity(mEphemeralObject);


        JSONObject attributes = response.getJSONObject("attributes");
        clear();

        for (String stat : stats) {
            JSONObject statData = attributes.getJSONObject(stat);
            int base = statData.getIntValue("base");
            int modified = statData.getIntValue("modified");

            Tuple<GridPane, BeveledLabel, BeveledLabel> rowData = getOrCreateRow(stat);
            BeveledLabel left = rowData.getSecond();
            left.setText(StringUtils.convertSnakeCaseToCapitalized(stat));

            BeveledLabel right = rowData.getThird();
            String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
            right.setText(base + " ( " + modifiedSign + Math.abs(modified) + " )");
        }
        mLogger.info("Finished update to Movement Information panel.");
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }

    public String getCurrentEntityID() { return mCurrentEntityID; }
}
