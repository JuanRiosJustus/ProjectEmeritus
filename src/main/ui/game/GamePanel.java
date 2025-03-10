package main.ui.game;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.constants.Pair;
import main.game.main.GameController;
import main.game.stores.pools.FontPool;
import main.ui.foundation.BeveledLabel;
import main.utils.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GamePanel extends StackPane {

    protected JSONObject mRequestObject = new JSONObject();

    public GamePanel(int x, int y, int width, int height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setLayoutX(x);
        setLayoutY(y);
    }

    public GamePanel(int width, int height) {
        this(0, 0, width, height);
    }

    protected static Font getFontForHeight(int height) {
        return FontPool.getInstance().getFontForHeight(height);
    }
    public void gameUpdate(GameController gameController) { }

//    public Pair<BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
//        Pair<BeveledLabel, BeveledLabel> newRow = mRows.get(name);
//        if (newRow != null) {
//            return newRow;
//        }
//
//        int rowWidth = (int) (mButtonWidth * .9);
//        int rowHeight = mButtonHeight;
//
//        // Create GridPane instead of HBox
//        GridPane gridPane = new GridPane();
//        gridPane.setPrefSize(rowWidth, rowHeight);
//        gridPane.setMinSize(rowWidth, rowHeight);
//        gridPane.setMaxSize(rowWidth, rowHeight);
//
//        Color color = mColor;
//
//        BeveledLabel leftLabel = new BeveledLabel(rowWidth / 2, rowHeight, name, color);
//        leftLabel.setAlignment(Pos.CENTER_LEFT);
//
//        BeveledLabel rightLabel = new BeveledLabel(rowWidth / 2, rowHeight, RandomUtils.createRandomName(3, 6), color);
//        rightLabel.setAlignment(Pos.CENTER_RIGHT);
//
//        // Add constraints to make sure columns resize properly
//        ColumnConstraints leftColumn = new ColumnConstraints();
//        leftColumn.setHgrow(Priority.ALWAYS); // Allows expansion
//        leftColumn.setPercentWidth(50); // Ensures left column takes 50% width
//        leftColumn.setHalignment(HPos.LEFT);
//
//        ColumnConstraints rightColumn = new ColumnConstraints();
//        rightColumn.setHgrow(Priority.ALWAYS);
//        rightColumn.setPercentWidth(50);
//        rightColumn.setHalignment(HPos.RIGHT);
//
//        gridPane.getColumnConstraints().addAll(leftColumn, rightColumn);
//
//        // Add labels to the grid
//        gridPane.add(leftLabel, 0, 0); // Left label in first column
//        gridPane.add(rightLabel, 1, 0); // Right label in second column
//
//        // Add the row to the content panel
//        mContentPanel.getChildren().add(gridPane);
//        mContentPanel.setAlignment(Pos.CENTER);
//
//        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
//        mRows.put(name, pair);
//
//        return pair;
//    }
}
