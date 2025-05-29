package main.ui;

import com.alibaba.fastjson2.JSONObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.constants.JavaFXUtils;
import main.game.main.GameModel;
import main.ui.scenes.mapeditor.TileLayersPanel;

import java.util.List;

public class TilerLayersStage extends Stage {

    private final int mWidth;
    private final int mHeight;

    private final ScrollPane mScrollPane;
    private final TileLayersPanel mLayersPanel;

    private JSONObject mLastHovered = null;
    public TilerLayersStage(int width, int height) {
        mWidth = width;
        mHeight = height;

        setTitle("Hovered Tile Layers");

        // Place the stage to the right side of the screen
        List<Screen> screens = Screen.getScreens();
        setX(screens.getFirst().getBounds().getWidth() - width - 20);
        setX(20);
        setY(20);
        setWidth(width);
        setHeight(height);
        setResizable(false);
//        setAlwaysOnTop(true);

        mLayersPanel = new TileLayersPanel(0, 0, width, (int)(height * 0.05));
        mScrollPane = new ScrollPane(mLayersPanel);
        mScrollPane.setFitToWidth(true);
        mScrollPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene = new Scene(mScrollPane);
        setScene(scene);
    }



    public void gameUpdate(GameModel gameModel) {
        JSONObject hovered = gameModel.getHoveredTile();
        if (hovered == null || hovered.isEmpty()) return;

        // Optional: avoid redundant updates
        if (hovered.equals(mLastHovered)) return;

        mLastHovered = hovered;

        int numRows = 1; // for row/column header
        if (hovered.containsKey("layers")) {
            numRows += hovered.getJSONArray("layers").size();
        }

        if (hovered.getJSONObject("structure") != null) {
            if (hovered.containsKey("structure") && !hovered.getJSONObject("structure").isEmpty()) {
                numRows += 1;
            }
        }

        numRows = Math.max(numRows, 1); // prevent division by 0
        int rowHeight = mHeight / numRows;
        int rowWidth = mWidth; // leave margin
//        int rowWidth = (int) (mWidth * 0.95); // leave margin

        mLayersPanel.update(hovered, rowWidth, (int) (rowHeight * .9));
    }
}
