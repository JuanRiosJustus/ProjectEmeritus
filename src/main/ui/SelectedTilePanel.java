package main.ui;

import javafx.geometry.Insets;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Checksum;
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
    private final Checksum mChecksum = new Checksum();
    private final VBox mContentPanel;
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private GameCanvas mGameCanvas = null;
    private BeveledButton mLabel = null;
    private int mPreviousChecksum = 0;
    private boolean mShouldOpenLargerStatsPanel = false;
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
        String tileSelectionCamera = gc.getGameModel().getGameState().getSecondaryCameraID();
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
        JavaFxUtils.setCachingHints(this);
    }

    public void gameUpdate(GameController gc) {
        mGameCanvas.gameUpdate(gc, gc.getGameModel().getGameState().getSecondaryCameraID());

        gc.getSelectedTilesChecksumAPI(mRequestObject);
        int currentChecksum = mRequestObject.optInt("checksum");
        if (mPreviousChecksum == currentChecksum) { return; }
        mPreviousChecksum = currentChecksum;

        mLogger.info("Checksum updated, getting units at selected tiles.");
        JSONArray response = gc.getUnitsAtSelectedTilesAPI();
        if (response.length() == 1) {
            JSONObject unitData = response.getJSONObject(0);
            String id = unitData.getString("id");
            String nickname = unitData.getString("nickname");

            mLabel.setText(nickname);

            // Glide to the selected unit
//            JSONArray response3 = gc.getCurrentTileIDOfUnit(new JSONObject(id));
//            String currentTileID = response3.getString(0);
//            gc.setSelectedTileIDs(currentTileID);
//            gc.setTileToGlideToID(currentTileID);

            JavaFxUtils.setOnMousePressedEvent(mLabel.getUnderlyingButton(), e -> {
                JSONObject request1 = new JSONObject();
                request1.put("id", id);

                JSONArray response2 = gc.getCurrentTileIDOfUnit(request1);
                String currentTileID2 = response2.getString(0);

                JSONArray request2 = new JSONArray();
                request2.put(currentTileID2);

                gc.setSelectedTileIdsAPI(request2);


                // Get camera to glide to the tile on
                JSONObject secondaryCameraInfo = gc.getSecondaryCameraInfoAPI();
                String camera = secondaryCameraInfo.getString("camera");
                // Create the request
                JSONObject tileToGlideToRequest = new JSONObject();
                tileToGlideToRequest.put("id", currentTileID2);
                tileToGlideToRequest.put("camera", camera);
                // Send request
                gc.setTileToGlideToAPI(tileToGlideToRequest);

//                JSONObject mainCameraInfo = gc.getMainCameraInfo();
//                camera = mainCameraInfo.getString("camera");
//                // Create the request
//                tileToGlideToRequest = new JSONObject();
//                tileToGlideToRequest.put("id", currentTileID2);
//                tileToGlideToRequest.put("camera", camera);
//                // Send request
//                gc.setTileToGlideToAPI(tileToGlideToRequest);

                mShouldOpenLargerStatsPanel = true;

            });
            mLogger.info("Updated selected tile display to view {}", nickname);
        } else {
            mLogger.info("Either no unit selected or more than one unit selected");
        }
    }

    public boolean consumeShouldOpenGreaterStatisticsPanel() {
        boolean shouldOpenGreaterStatisticsPanel = mShouldOpenLargerStatsPanel;
        mShouldOpenLargerStatsPanel = false;
        return shouldOpenGreaterStatisticsPanel;
    }
}