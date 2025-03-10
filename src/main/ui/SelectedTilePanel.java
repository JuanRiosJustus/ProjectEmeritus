package main.ui;

import javafx.geometry.Insets;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.CheckSum;
import main.game.main.GameController;
import main.logging.EmeritusLogger;
import main.ui.foundation.BevelStyle;
import main.ui.foundation.BeveledButton;
import main.ui.game.GameCanvas;
import main.ui.game.JavaFxUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class SelectedTilePanel extends BevelStyle {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(SelectedTilePanel.class);
    private final CheckSum mCheckSum = new CheckSum();
    private final VBox mContentPanel;
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private GameCanvas mGameCanvas = null;
    private BeveledButton mLabel = null;

    public SelectedTilePanel(int x, int y, int width, int height, Color color, GameController gc) {
        super(x, y, width, height, color);


        // 🔹 **Create StackPane to Hold Canvas (Ensures Beveled Effect)**
        int canvasWidth = (int) (width * 1);
        int canvasHeight = (int) (height * .8);
        Pane canvasWrapper = new StackPane();
        canvasWrapper.setPrefSize(canvasWidth, canvasHeight);
        canvasWrapper.setMinSize(canvasWidth, canvasHeight);
        canvasWrapper.setMaxSize(canvasWidth, canvasHeight);

        // 🔹 **Game Canvas (Inside Wrapper)**
        mGameCanvas = new GameCanvas(gc, canvasWidth, canvasHeight);
        canvasWrapper.getChildren().add(mGameCanvas);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.color(1, 1, 1, 0.6));
        innerShadow.setRadius(6);
        mGameCanvas.setEffect(innerShadow);

        setEffect(JavaFxUtils.createBasicDropShadow(width, height));

        // 🔹 **Set Camera Size Based on Canvas**
        String tileSelectionCamera = gc.getGameModel().getGameState().getTileSelectionCameraName();
        gc.getGameModel()
                .getGameState()
                .setCameraWidth(tileSelectionCamera, canvasWidth)
                .setCameraHeight(tileSelectionCamera, canvasHeight);

        // 🔹 **Label Below Canvas**
        int labelWidth = width;
        int labelHeight = (int) (height * 0.2);
        mLabel = new BeveledButton(labelWidth, labelHeight, "TEST", color);
        mLabel.setFocusTraversable(false);
        mLabel.setFont(getFontForHeight(labelHeight));
        mLabel.setPrefWidth(width);
        mLabel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);

        // 🔹 **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);
        mContentPanel.setSpacing(0);
        mContentPanel.getChildren().addAll(canvasWrapper, mLabel); // Now using the wrapper

        // 🔹 **Ensure Background is Set**
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().add(mContentPanel);
    }

    public void gameUpdate(GameController gc) {
        mGameCanvas.gameUpdate(gc, gc.getGameModel().getGameState().getTileSelectionCameraName());

        int selectedTilesHash = gc.getSelectedTilesHash();
        if (mCheckSum.setDefault(selectedTilesHash)) {
            mLogger.info("Checksum updated");
            JSONArray response = gc.getUnitAtSelectedTilesV2();
            if (response.length() == 1) {
                JSONObject unitData = response.getJSONObject(0);
                String id = unitData.getString("id");
                String nickname = unitData.getString("nickname");
                mLabel.setText(nickname);

                JavaFxUtils.setOnMousePressedEvent(mLabel.getUnderlyingButton(), e -> {
                    JSONObject request = new JSONObject();
                    request.put("id", id);

                    JSONArray response2 = gc.getCurrentTileIdOfUnit(request);
                    String currentTileID = response2.getString(0);

                    gc.setSelectedTiles(currentTileID);
                    gc.setTileToGlideTo(currentTileID);
                });
                mLogger.info("Updated selected tile display to view {}", nickname);
            } else {
                mLogger.info("Either no unit selected or more than one unit selected");
            }
        }
    }
}