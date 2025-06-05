package main.ui.scenes.mapeditor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Tuple;
import main.engine.EngineRunnable;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.game.stores.FontPool;
import main.graphics.AssetPool;
import main.logging.EmeritusLogger;
import main.constants.JavaFXUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MapEditorScene extends EngineRunnable {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(MapEditorScene.class);
    private GameController mGameController = null;
    public MapEditorScene(int width, int height) { super(width, height); }

    private Random mRandom = new Random();

    private int mToolsPaneWidth = 0;
    private int mToolsPaneHeight =0;
    private int mToolsPaneX = 0;
    private int mToolsPaneY = 0;


    private TabPane mTabPane = null;
    private VBox mDisplayPane = null;
    private int mDisplayPaneWidth =0;
    private int mDisplayPaneHeight = 0;
    private int mDisplayPaneX = 0;
    private int mDisplayPaneY = 0;



    private int mGamePaneWidth = 0;
    private int mGamePaneHeight = 0;
    private int mGamePaneX = 0;
    private int mGamePaneY = 0;
    private Pane mGamePaneContainer = null;
    private Pane mRootPane = null;

    private static final String MAP_BRUSH_MODE_ADDITIVE = "Additive";
    private static final int SPRITE_SIZE = AssetPool.getInstance().getNativeSpriteSize();
    private StackPane mStackPane = new StackPane();
    private MapEditorSceneGeneratorPanel mGeneratorPanel = null;
    private MapEditorSceneEditorPanel mEditorPanel = null;
    private TileLayersPanel mLayersPanel = null;
    private Tuple<HBox, Label, ComboBox<String>> mMapTileSelectionBrushMode = null;




    @Override
    public Scene render() {
        mDisplayPaneWidth = mWidth;
        mDisplayPaneHeight = mHeight;
        mDisplayPaneX = 0;
        mDisplayPaneY = 0;
        mDisplayPane = getDisplayContainer(mDisplayPaneWidth, mDisplayPaneHeight);
        mDisplayPane.setVisible(true);
        mDisplayPane.setBackground(new Background(new BackgroundFill(Color.ORCHID, CornerRadii.EMPTY, Insets.EMPTY)));

        Pane mTileLayerPane = createWrapperPane(mWidth, mHeight);
        mLayersPanel = new TileLayersPanel(20, mHeight - 40, 200, 30);
        mTileLayerPane.getChildren().add(mLayersPanel);




        mToolsPaneWidth = (int) (mWidth * .25);
        mToolsPaneHeight = mHeight;
        mToolsPaneX = mWidth - mToolsPaneWidth;
        mToolsPaneY = 0;
        TabPane toolsPane = getToolsContainer(mToolsPaneWidth, mToolsPaneHeight);
        toolsPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        toolsPane.setLayoutX(mToolsPaneX);
        toolsPane.setLayoutY(mToolsPaneY);
        Pane mToolsPaneContainer = createWrapperPane(mWidth, mHeight);
        mToolsPaneContainer.getChildren().add(toolsPane);

        mGamePaneWidth = mWidth;
        mGamePaneHeight = mHeight;
//        mGamePaneHeight = mHeight - mDisplayPaneHeight;
        mGamePaneX = 0;
        mGamePaneY = 0;
//        mGameController = GameController.create(20, 20, mGamePaneWidth, mGamePaneHeight);
//        Pane gamesPane = mGameController.getGamePanel();
//        mGameController.setConfigurableStateGameplayHudIsVisible(false);
//        glideToCenterOfMapAndZoomCamera(mCameraZoomField.getThird().getValue());
        mGamePaneContainer = new FlowPane();
        mGamePaneContainer.setLayoutY(0);
        mGamePaneContainer.setLayoutX(0);
        mGamePaneContainer.setPrefSize(mGamePaneWidth, mGamePaneHeight);
        mGamePaneContainer.setMaxSize(mGamePaneWidth, mGamePaneHeight);
        mGamePaneContainer.setMinSize(mGamePaneWidth, mGamePaneHeight);
        final AtomicInteger currentX = new AtomicInteger();
        final AtomicInteger currentY = new AtomicInteger();
        mGamePaneContainer.setOnMouseMoved(e -> {
            if (currentX.get() == e.getX() && currentY.get() == e.getY()) { return; }
            currentX.set((int) e.getX());
            currentY.set((int) e.getY());

            JSONObject hoveredTile = mGameController.getHoveredTile();
            if (hoveredTile == null || hoveredTile.isEmpty()) { return; }

            mLayersPanel.update(hoveredTile, (int) (mWidth * .15), (int) (mHeight * .05));
        });

        mEditorPanel.setOnMouseMoved(e -> {
            if (!mEditorPanel.isVisible()) { return; }
            try {
                mLogger.info("Started updating cursor size");
                int cursorSize = mEditorPanel.getSelectionSize();
                JSONObject cursorSizeRequest = new JSONObject();
                cursorSizeRequest.put("cursor_size", cursorSize);
                mGameController.setMapEditorHoveredTilesCursorSizeAPI(cursorSizeRequest);
                mLogger.info("Finished updating cursor size");
            } catch (Exception ex) {
                mLogger.info("Unable to update cursor size");
            }
        });


        mGamePaneContainer.setOnMousePressed(e -> {
            if (!mEditorPanel.isVisible()) { return; }

            JSONObject request = mGameController.getHoveredTile();
            if (request == null || request.isEmpty()) { return; }
//
//
//            String operation = mEditorPanel.getOperation();;
//            String asset = mEditorPanel.getAsset();
//            String depth = 1 + ""; // mMapTileSelectionDepthsSize.getThird().getText().trim();
//            String state = "Solid"; //mTileTopLayerState.getThird().getText();
//
            if (mEditorPanel.isDeletionMode()) {
                String depth = 1 + "";
                mGameController.removeLayersOfHoveredTileIDs(depth);
            } else if (mEditorPanel.isAdditionMode() && mEditorPanel.getAsset() != null) {
                String asset = mEditorPanel.getAsset();
                String depth = 1 + ""; // mMapTileSelectionDepthsSize.getThird().getText().trim();
                String state = asset.contains("liquid") ? "liquid" : "solid"; //mTileTopLayerState.getThird().getText();
                mGameController.addLayersToHoveredTileIDs(asset, state, depth);
            }

            JSONObject response = mGameController.getHoveredTile();
            if (response == null || response.isEmpty()) { return; }

            mLayersPanel.update(response, (int) (mWidth * .15), (int) (mHeight * .05));
        });

        mGamePaneContainer.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        createNewGame();

        mRootPane = JavaFXUtils.createWrapperPane(0, 0, mWidth, mHeight);
        mRootPane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        mStackPane.getChildren().add(mGamePaneContainer);
        mStackPane.getChildren().add(mToolsPaneContainer);
        mStackPane.getChildren().add(mTileLayerPane);

        Scene editorScene = new Scene(mStackPane, mWidth, mHeight);
        return editorScene;
    }


    private VBox getDisplayContainer(int width, int height) {

        Color color = Color.ANTIQUEWHITE;
        VBox container = new VBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);


        mTabPane = new TabPane();
        container.getChildren().add(mTabPane);

        ScrollPane displayScrollPane = new ScrollPane(mDisplayPane);
        displayScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        displayScrollPane.setLayoutX(mDisplayPaneX);
        displayScrollPane.setLayoutY(mDisplayPaneY);


        return container;
    }

    private TabPane getToolsContainer(int width, int height) {

        Color color = Color.ANTIQUEWHITE;
        TabPane container = new TabPane();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);

        int rowWidth = width;
        int rowHeight = (int) (height * .03);
//        BeveledButton titleBar = new BeveledButton(rowWidth, rowHeight, "Map Editor", color);
//        container.getChildren().add(titleBar);

        Tab mapGeneratorTab = new Tab("Generator");
        VBox page1 = new VBox();
        page1.setPrefSize(width, height);
        page1.setMinSize(width, height);
        page1.setMaxSize(width, height);
        mapGeneratorTab.setContent(page1);


        Tab mapEditorTab = new Tab("Editor");
        VBox page2 = new VBox();
        page2.setPrefSize(width, height);
        page2.setMinSize(width, height);
        page2.setMaxSize(width, height);
        mapEditorTab.setContent(page2);



        Label mapGenerationLabel = new Label();
        mapGenerationLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight * 2));
        mapGenerationLabel.setPrefSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setMinSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setMaxSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setAlignment(Pos.CENTER);
        mapGenerationLabel.setText("Map Generation");


        mEditorPanel = new MapEditorSceneEditorPanel(rowWidth, height);

        mGeneratorPanel = new MapEditorSceneGeneratorPanel(rowWidth, height);
        mGeneratorPanel.getCameraZoomSlider().setOnMouseReleased(e -> {
            try {
                mLogger.info("Started updating camera zoom value");
                double zoom = mGeneratorPanel.getCameraZoom();
                glideToCenterOfMapAndZoomCamera(zoom);
                mLogger.info("Successfully updated camera zoom value");
            } catch (Exception ex) {
                mLogger.info("Incorrect camera zoom value");
            }
        });

        Button generateMapButton = new Button("Generate Map");
        generateMapButton.setPrefSize(rowWidth, rowHeight);
        generateMapButton.setMinSize(rowWidth, rowHeight);
        generateMapButton.setMaxSize(rowWidth, rowHeight);
        generateMapButton.setOnAction(e -> createNewGame());




        page1.getChildren().addAll(mGeneratorPanel, generateMapButton);

        page2.getChildren().addAll(mEditorPanel);

//        page2.getChildren().addAll(
//                mBrushLabel.getFirst(),
//                mMapTileSelectionCursorSize.getFirst(),
//                mMapTileSelectionDepthsSize.getFirst(),
//                mMapTileSelectionBrushMode.getFirst()
//        );


        mTabPane.getTabs().add(mapGeneratorTab);
        mTabPane.getTabs().add(mapEditorTab);

        return mTabPane;
    }


    private void createNewGame() {
        try {
            mLogger.info("Starting to create new GameController!");
            int rows = Integer.parseInt(mGeneratorPanel.getRows());
            int columns = Integer.parseInt(mGeneratorPanel.getColumns());
            int spriteWidth = SPRITE_SIZE;
            int spriteHeight = SPRITE_SIZE;
            int minElevation = Integer.parseInt(mGeneratorPanel.getMinElevation());
            int liquidElevation = Integer.parseInt(mGeneratorPanel.getWaterElevation());
            int maxElevation = Integer.parseInt(mGeneratorPanel.getMaxElevation());
            double noiseZoom = mGeneratorPanel.getNoiseZoom();
            double cameraZoom = mGeneratorPanel.getCameraZoom();

            String liquid = mGeneratorPanel.getLiquidAssetField();
            String terrain = mGeneratorPanel.getTerrainAssetField();
            String structure = mGeneratorPanel.getStructureAssetField();

            int viewportWidth = mGamePaneWidth;
            int viewportHeight = mGamePaneHeight;

            GameConfigs gc = GameConfigs.getDefaults()
                    .setOnStartupSpriteWidth(spriteWidth)
                    .setOnStartupSpriteHeight(spriteHeight)
                    .setViewportWidth(viewportWidth)
                    .setViewportHeight(viewportHeight)
                    .setMapGenerationRows(rows)
                    .setMapGenerationColumns(columns)
                    .setMapGenerationTerrainHeightNoise(.75f)
                    .setMapGenerationTerrainStartingElevation(minElevation)
                    .setMapGenerationLiquidElevation(liquidElevation)
                    .setMapGenerationTerrainEndingElevation(maxElevation)
                    .setMapGenerationTerrainHeightNoise((float) noiseZoom)
                    .setMapGenerationTerrainAsset("./" + terrain)
                    .setMapGenerationLiquidAsset("./" + liquid)
                    .setMapGenerationStructureAssets(List.of("./" + structure));

            AssetPool.getInstance().clearPool();
            if (mGameController != null && mGameController.isRunning()) { mGameController.stop(); }

            mGameController = GameController.create(gc);
            mGamePaneContainer.getChildren().clear();

            Pane gamesPane = mGameController.getGamePanel();
            mGamePaneContainer.getChildren().add(gamesPane);

            mGameController.initialize();
            mGameController.setConfigurableStateGameplayHudIsVisible(false);
            glideToCenterOfMapAndZoomCamera(cameraZoom);

//            mGamePaneContainer.getChildren().add()
//            System.gc();
//            mGamePaneContainer.setBackground(new Background(new BackgroundFill(ColorPalette.getRandomColor(), CornerRadii.EMPTY, Insets.EMPTY)));

            mLogger.info("Successfully created new GameController!");
        } catch (Exception ex) {
            mLogger.info("Unable to create new GameController!");
        }
    }

    private void glideToCenterOfMapAndZoomCamera(double zoom) {
        JSONObject request = new JSONObject();
        request.put("zoom", zoom);
        mGameController.setCameraZoomAPI(request);

        // Create request to center the camera
        JSONObject tileToGlideToRequest = new JSONObject();

        JSONObject response = mGameController.getMainCameraInfoAPI();
        String cameraName = response.getString("camera");
        tileToGlideToRequest.put("camera", cameraName);

        response = mGameController.getCenterTileEntityAPI();
        String centerTileEntityID = response.getString("id");
        tileToGlideToRequest.put("id", centerTileEntityID);

        mGameController.setTileToGlideToAPI(tileToGlideToRequest);
    }

    @Override
    public void initialize() { mGameController.initialize(); }

    @Override
    public void stop() { mGameController.stop(); }
}
