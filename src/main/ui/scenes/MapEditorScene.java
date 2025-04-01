package main.ui.scenes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import main.constants.Pair;
import main.constants.Tuple;
import main.engine.EngineController;
import main.engine.EngineRunnable;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.AssetPool;
import main.logging.EmeritusLogger;
import main.ui.foundation.BevelStyle;
import main.ui.foundation.BeveledButton;
import main.constants.JavaFxUtils;
import main.utils.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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


    private Tuple<HBox, Label, TextField> mMapGenerationName = null;
    private Tuple<HBox, Label, TextField> mMapGenerationRowsField = null;
    private Tuple<HBox, Label, TextField> mMapGenerationColumnsField = null;
    private Tuple<HBox, Label, TextField> mSpriteHeightField = null;
    private Tuple<HBox, Label, TextField> mSpriteWidthField = null;
    private Tuple<HBox, Label, Slider> mCameraZoomField = null;
    private Tuple<HBox, Label, TextField> mMapGenerationMinimumTileElevationField = null;
    private Tuple<HBox, Label, TextField> mMapGenerationLiquidElevationField = null;
    private Tuple<HBox, Label, TextField> mMapGenerationMaximumTileElevationField = null;
    private Tuple<HBox, Label, Slider> mMapNoiseZoom = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationLiquidLevelField = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationTerrainField = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationStructureField = null;

    private Tuple<HBox, Label, TextField> mMapBrushSize = null;
    private Tuple<HBox, Label, TextField> mTileRowColumn = null;
    private Tuple<HBox, Label, TextField> mTileHeight = null;
    private Tuple<HBox, Label, TextField> mTileLayerCount = null;
    private Tuple<HBox, Label, TextField> mTileTopLayerState = null;
    private Tuple<HBox, Label, TextField> mTileTopLayerDepth = null;
    private Tuple<HBox, Label, TextField> mTileTopLayerAsset = null;
    private Tuple<HBox, Label, ComboBox<String>> mMapBrushMode = null;
    private Tuple<HBox, Button, ComboBox<Object>> mBrushLabel = null;

    @Override
    public Scene render() {
        mToolsPaneWidth = (int) (mWidth * .25);
        mToolsPaneHeight = mHeight;
        mToolsPaneX = mWidth - mToolsPaneWidth;
        mToolsPaneY = 0;

        mDisplayPaneWidth = mWidth - mToolsPaneWidth;
        mDisplayPaneHeight = (int) (mHeight * .25);
        mDisplayPaneX = 0;
        mDisplayPaneY = mHeight - mDisplayPaneHeight;

        mDisplayPane = getDisplayContainer(mDisplayPaneWidth, mDisplayPaneHeight);
//        mDisplayPane.setLayoutX(mDisplayPaneX);
//        mDisplayPane.setLayoutY(mDisplayPaneY);
        mDisplayPane.setVisible(true);
        mDisplayPane.setBackground(new Background(new BackgroundFill(Color.ORCHID, CornerRadii.EMPTY, Insets.EMPTY)));
        ScrollPane displayScrollPane = new ScrollPane(mDisplayPane);
        displayScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        displayScrollPane.setLayoutX(mDisplayPaneX);
        displayScrollPane.setLayoutY(mDisplayPaneY);

        VBox toolsPane = getToolsContainer(mToolsPaneWidth, mToolsPaneHeight);
        toolsPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        toolsPane.setLayoutX(mToolsPaneX);
        toolsPane.setLayoutY(mToolsPaneY);

        mGamePaneWidth = mWidth - mToolsPaneWidth;
        mGamePaneHeight = mHeight - mDisplayPaneHeight;
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
        mGamePaneContainer.setPrefSize(mGamePaneWidth, mGamePaneHeight);
        mGamePaneContainer.setPrefSize(mGamePaneWidth, mGamePaneHeight);
        final AtomicInteger currentX = new AtomicInteger();
        final AtomicInteger currentY = new AtomicInteger();
        mGamePaneContainer.setOnMouseMoved(e -> {
            if (currentX.get() == e.getX() && currentY.get() == e.getY()) { return; }
            currentX.set((int) e.getX());
            currentY.set((int) e.getY());

            JSONArray response = mGameController.getTileDetailsFromGameMapEditorAPI();
            if (response.isEmpty()) { return; }
            JSONObject selected = response.getJSONObject(0);

            mTileRowColumn.getThird().setText(
                    selected.getString("tile_row") + ", " +
                    selected.getString("tile_column")
            );
            int base_elevation = selected.getInt("tile_base_elevation");
            int modified_elevation = selected.getInt("tile_modified_elevation");
            int total_elevation = selected.getInt("tile_total_elevation");
            mTileHeight.getThird().setText(total_elevation +  "");
//            mTileHeight.getThird().setText(total_elevation + " = " + base_elevation + " ( " + modified_elevation + " ) ");
            mTileLayerCount.getThird().setText(selected.getString("tile_layer_count"));
            mTileTopLayerAsset.getThird().setText(selected.getString("top_layer_asset"));
            mTileTopLayerDepth.getThird().setText(selected.getString("top_layer_depth"));
            mTileTopLayerState.getThird().setText(selected.getString("top_layer_state"));
            mLogger.info("Updated hoveredTilePanes {},{}", currentX.get(), currentY.get());
        });

        mGamePaneContainer.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        createNewGame();
//        mGamePaneContainer.getChildren().add(gamesPane);


        mRootPane = JavaFxUtils.createWrapperPane(0, 0, mWidth, mHeight);
        mRootPane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        mRootPane.getChildren().addAll(toolsPane, displayScrollPane, mGamePaneContainer);
        Scene editorScene = new Scene(mRootPane, mWidth, mHeight);
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

    private VBox getToolsContainer(int width, int height) {

        Color color = Color.ANTIQUEWHITE;
        VBox container = new VBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);

        int rowWidth = width;
        int rowHeight = (int) (height * .025);
        BeveledButton titleBar = new BeveledButton(rowWidth, rowHeight, "Map Editor", color);
        container.getChildren().add(titleBar);

        Label mapGenerationLabel = new Label();
        mapGenerationLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight * 2));
        mapGenerationLabel.setPrefSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setMinSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setMaxSize(rowWidth, rowHeight * 2);
        mapGenerationLabel.setAlignment(Pos.CENTER);
        mapGenerationLabel.setText("Map Generation");

        Label tileDetailsLabel = new Label();
        tileDetailsLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight * 2));
        tileDetailsLabel.setPrefSize(rowWidth, rowHeight * 2);
        tileDetailsLabel.setMinSize(rowWidth, rowHeight * 2);
        tileDetailsLabel.setMaxSize(rowWidth, rowHeight * 2);
        tileDetailsLabel.setAlignment(Pos.CENTER);
        tileDetailsLabel.setText("Tile Details");

        mSpriteWidthField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mSpriteWidthField.getSecond().setText("Sprite Width");
        mSpriteWidthField.getThird().setText("64");

        mSpriteHeightField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mSpriteHeightField.getSecond().setText("Sprite Height");
        mSpriteHeightField.getThird().setText("64");

        mCameraZoomField = JavaFxUtils.getButtonAndSliderField(rowWidth, rowHeight, .25f);
        mCameraZoomField.getSecond().setText("Cam Zoom");
        mCameraZoomField.getThird().setMin(0.00001);
        mCameraZoomField.getThird().setValue(.75);
        mCameraZoomField.getThird().setMax(2);
        mCameraZoomField.getThird().setShowTickLabels(true);
        mCameraZoomField.getThird().setSnapToTicks(true);
        mCameraZoomField.getThird().setMinorTickCount(1);
        mCameraZoomField.getThird().setMajorTickUnit(.05);
        mCameraZoomField.getThird().setOnMouseReleased(e -> {
            try {
                mLogger.info("Started updating camera zoom value");

                // Create request to update the "Zoom"
                double zoom = mCameraZoomField.getThird().getValue();
                glideToCenterOfMapAndZoomCamera(zoom);
                mLogger.info("Successfully updated camera zoom value");
            } catch (Exception ex) {
                mLogger.info("Incorrect camera zoom value");
            }
        });

        mMapGenerationName = JavaFxUtils.getLabelToFieldRow(rowWidth, rowHeight, .25f);
        mMapGenerationName.getSecond().setText("Map Name");
        mMapGenerationName.getThird().setText(RandomUtils.createRandomName(3, 7));

        mMapGenerationRowsField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMapGenerationRowsField.getSecond().setText("Map Rows");
        mMapGenerationRowsField.getThird().setText(String.valueOf(20));

        mMapGenerationColumnsField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMapGenerationColumnsField.getSecond().setText("Map Columns");
        mMapGenerationColumnsField.getThird().setText(String.valueOf(25));

        mMapGenerationMinimumTileElevationField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .3f);
        mMapGenerationMinimumTileElevationField.getSecond().setText("Min Elevation");
        mMapGenerationMinimumTileElevationField.getThird().setText(String.valueOf(0));

        mMapGenerationLiquidElevationField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .3f);
        mMapGenerationLiquidElevationField.getSecond().setText("Liquid Elevation");
        mMapGenerationLiquidElevationField.getThird().setText(String.valueOf(3));

        mMapGenerationMaximumTileElevationField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .3f);
        mMapGenerationMaximumTileElevationField.getSecond().setText("Max Elevation");
        mMapGenerationMaximumTileElevationField.getThird().setText(String.valueOf(10));

        mMapGenerationTerrainField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
        setupComboImages("res/graphics/floor_tiles", mMapGenerationTerrainField);

        mMapGenerationStructureField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
        setupComboImages("res/graphics/structures", mMapGenerationStructureField);

//        mMapGenerationWaterLiquidLevelField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
//        setupComboImages("res/graphics/liquids", mMapGenerationWaterLiquidLevelField, (int)
//                mMapGenerationWaterLiquidLevelField.getThird().getPrefWidth(), (int) mMapGenerationWaterLiquidLevelField.getThird().getPrefHeight());

//        mMapTerrainField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
//        setupComboImages("res/graphics/floor_tiles", mMapTerrainField,
//                (int) mMapTerrainField.getThird().getPrefWidth(), (int) mMapTerrainField.getThird().getPrefHeight());

        mMapNoiseZoom = JavaFxUtils.getButtonAndSliderField(rowWidth, rowHeight, .25f);
        mMapNoiseZoom.getThird().setMin(.005);
        mMapNoiseZoom.getThird().setValue(.8f);
        mMapNoiseZoom.getThird().setMax(.995);
        mMapNoiseZoom.getSecond().setText("Noise Zoom");
        mMapNoiseZoom.getThird().setShowTickLabels(true);
        mMapNoiseZoom.getThird().setSnapToTicks(true);
        mMapNoiseZoom.getThird().setMinorTickCount(1);
        mMapNoiseZoom.getThird().setMajorTickUnit(.05);

//        mMapTerrainFieldV1 = JavaFxUtils.getButtonToFieldRow(rowWidth, rowHeight, .25f);
//        setupAssetStructure(mMapTerrainFieldV1.getSecond(), "res/graphics/floor_tiles", mMapTerrainFieldV1.getThird());
//        mMapTerrainFieldV1.getSecond().setText("Terrain");

//        mMapTerrainField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
//        setupComboImages("res/graphics/floor_tiles", mMapTerrainField, (int) mBrushLabel.getThird().getPrefWidth(), (int) mBrushLabel.getThird().getPrefHeight());

        mMapGenerationLiquidLevelField = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
        setupComboImages("res/graphics/liquids", mMapGenerationLiquidLevelField);



        mTileRowColumn = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileRowColumn.getSecond().setText("Tile");

        mTileLayerCount = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileLayerCount.getSecond().setText("Layer Count");

        mTileHeight = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileHeight.getSecond().setText("Tile Height");

//        mTileHeight = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
//        mTileHeight.getSecond().setText("Tile Height");

        mTileTopLayerAsset = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileTopLayerAsset.getSecond().setText("Top Layer Asset");

        mTileTopLayerDepth = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileTopLayerDepth.getSecond().setText("Top Layer Depth");

        mTileTopLayerState = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mTileTopLayerState.getSecond().setText("Top Layer State");

        Button generateMapButton = new Button("Generate Map");
        generateMapButton.setPrefSize(rowWidth, rowHeight);
        generateMapButton.setMinSize(rowWidth, rowHeight);
        generateMapButton.setMaxSize(rowWidth, rowHeight);
        generateMapButton.setOnAction(e -> {
            createNewGame();
        });


        mMapBrushSize = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMapBrushSize.getSecond().setText("Brush Size");
        mMapBrushSize.getThird().setText("1");
        mMapBrushSize.getThird().setOnAction(e -> {
            try {
                int brushSize = Integer.parseInt(mMapBrushSize.getThird().getText().trim());
                JSONObject request = new JSONObject();
                request.put("cursor_size", brushSize);
                mGameController.setHoveredTilesCursorSizeAPI(request);

            } catch (Exception ex) {
                mLogger.error("Unable to change brush size");
            }
        });


        mBrushLabel = JavaFxUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);
        setupComboImages("res/graphics/floor_tiles", mBrushLabel);
//        mBrushLabel.getThird().
//        ObservableList<String> options = FXCollections.observableArrayList();
//        options.addAll(notOnLine, onLine);
//        mBrushLabel.getThird().getItems().addAll(options);
//        mBrushLabel.getThird().setCellFactory(c -> new StatusListCell());
//
//
//        getImageAndStringField

        mMapBrushMode = JavaFxUtils.getLabelAndComboBox(rowWidth, rowHeight, .25f);
        mMapBrushMode.getSecond().setText("Brush Mode");
        mMapBrushMode.getThird().getItems().add("Add Single Layer");
        mMapBrushMode.getThird().getItems().add("Add N Layers");
        mMapBrushMode.getThird().getItems().add("Delete Single Layer");
        mMapBrushMode.getThird().getItems().add("Delete N Layers");
//        mMapBrushMode.getThird().getItems().add("Delete All Layers");
//        mMapBrushMode.getThird().getItems().add("Delete Until Different Layer");
//        mMapBrushMode.getThird().getItems().add("Delete Until Foundation");



        container.getChildren().addAll(
                mMapGenerationName.getFirst(),
                mapGenerationLabel,
                mMapGenerationRowsField.getFirst(),
                mMapGenerationColumnsField.getFirst(),
                mSpriteWidthField.getFirst(),
                mSpriteHeightField.getFirst(),
                mMapGenerationMinimumTileElevationField.getFirst(),
                mMapGenerationLiquidElevationField.getFirst(),
                mMapGenerationMaximumTileElevationField.getFirst(),
                mMapGenerationTerrainField.getFirst(),
                mMapGenerationStructureField.getFirst(),

//                mMapTerrainFieldV1.getFirst(),
//                mMapStructuresFieldV1.getFirst(),
                mMapGenerationLiquidLevelField.getFirst(),
                mMapNoiseZoom.getFirst(),
                generateMapButton,
                mCameraZoomField.getFirst(),
                tileDetailsLabel,
                mTileRowColumn.getFirst(),
                mTileHeight.getFirst(),
                mTileLayerCount.getFirst(),
                mTileTopLayerAsset.getFirst(),
                mTileTopLayerDepth.getFirst(),
                mTileTopLayerState.getFirst(),
                new Label("======================================================================"),

                mBrushLabel.getFirst(),
                mMapBrushSize.getFirst(),
                mMapBrushMode.getFirst()

        );

        return container;
    }

    private void setupComboImages(String directory, Tuple<HBox, Button, ComboBox<Object>> row) {
        File files = new File(directory);
        File[] filesInDirectory = files.listFiles();
        if (filesInDirectory == null) {
            return;
        }

//        int imageWidth = AssetPool.getInstance()

        for (File file : filesInDirectory) {
            // This spritesheet can be many frames, but its of the same "Sprite"/Thing
            Image rawImage = new Image("file:" + file.getAbsolutePath());
            Image firstFrame = new WritableImage(rawImage.getPixelReader(),
                    0,
                    0,
                    AssetPool.getInstance().getNativeSpriteSize(),
                    AssetPool.getInstance().getNativeSpriteSize()
            );

            ImageView imageView = new ImageView(firstFrame);
            imageView.setFitWidth(row.getSecond().getPrefWidth());
            imageView.setFitHeight(row.getSecond().getPrefHeight());

            ComboBoxResourceItem item = new ComboBoxResourceItem(firstFrame, file.getPath(), file.getName());
            row.getThird().getItems().add(item);
        }
        row.getThird().setCellFactory(new Callback<>() {
            public ListCell<Object> call(ListView<Object> p) {
                return new ListCell<>() {
                    private final HBox hBox = new HBox(5); // 5 pixels spacing
                    private final ImageView imageView = new ImageView();
                    private final Label label = new Label();

                    {
                        hBox.getChildren().addAll(imageView, label);
                    }

                    @Override
                    protected void updateItem(Object selection, boolean isEmpty) {
                        super.updateItem(selection, isEmpty);

                        ComboBoxResourceItem item = (ComboBoxResourceItem) selection;
                        if (selection == null || isEmpty) {
                            setGraphic(null);
                        } else {
                            imageView.setImage(item.image);
//                            imageView.setFitHeight(row.getSecond().getPrefHeight());
//                            imageView.setFitWidth(row.getSecond().getPrefWidth());
//                            imageView.setPreserveRatio(true);
//                            label.setFont(FontPool.getInstance().getFontForHeight(height));
                            label.setText(item.name);
                            setGraphic(hBox);

                            if (!row.getFirst().isFocused()) { return; }
                            ImageView iv = new ImageView(item.image);
                            iv.setFitHeight(row.getSecond().getPrefHeight());
                            iv.setFitWidth(row.getSecond().getPrefWidth());
                            row.getSecond().setGraphic(iv);
                        }
                    }
                };
            }
        });

        ListCell<Object> buttonCell = new ListCell<Object>() {
//            private final HBox hBox = new HBox(5); // 5 pixels spacing
//            private final ImageView imageView = new ImageView();
//            private final Label label = new Label();
//
//            {
//                hBox.getChildren().addAll(imageView, label);
//            }
            @Override protected void updateItem(Object selection, boolean isEmpty) {

                ComboBoxResourceItem item = (ComboBoxResourceItem) selection;
                if (selection == null || isEmpty) {
                    setGraphic(null);
                } else {
//                    imageView.setImage(item.image);
//                    imageView.setFitHeight(row.getSecond().getPrefHeight());
//                    imageView.setFitWidth(row.getSecond().getPrefWidth());
//                    label.setText(item.name);
//                    setGraphic(hBox);
                    setText(item.name);

                    ImageView iv = new ImageView(item.image);
                    iv.setFitHeight(row.getSecond().getPrefHeight());
                    iv.setFitWidth(row.getSecond().getPrefWidth());
                    row.getSecond().setGraphic(iv);
                }
            }
        };
        row.getThird().setButtonCell(buttonCell);
//        row.getThird().getSelectionModel().selectFirst();
//        row.getThird().


        int selectedIndex = mRandom.nextInt(row.getThird().getItems().size());
        row.getThird().getSelectionModel().select(selectedIndex);
        ComboBoxResourceItem item = (ComboBoxResourceItem) row.getThird().getItems().get(selectedIndex);
        ImageView iv = new ImageView(item.image);
        iv.setFitHeight(row.getSecond().getPrefHeight());
        iv.setFitWidth(row.getSecond().getPrefWidth());
        row.getSecond().setGraphic(iv);
        row.getSecond().setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static class ComboBoxResourceItem {
        private final String name;
        private final String fileName;
        private final Image image;

        public ComboBoxResourceItem(Image image, String fileName, String name) {
            this.name = name;
            this.image = image;
            this.fileName = fileName;
        }
    }

    private void createNewGame() {
        try {
            mLogger.info("Starting to create new GameController!");
            int rows = Integer.parseInt(mMapGenerationRowsField.getThird().getText());
            int columns = Integer.parseInt(mMapGenerationColumnsField.getThird().getText());
            int spriteWidth = Integer.parseInt(mSpriteWidthField.getThird().getText());
            int spriteHeight = Integer.parseInt(mSpriteHeightField.getThird().getText());
            int minElevation = Integer.parseInt(mMapGenerationMinimumTileElevationField.getThird().getText());
            int liquidElevation = Integer.parseInt(mMapGenerationLiquidElevationField.getThird().getText());
            int maxElevation = Integer.parseInt(mMapGenerationMaximumTileElevationField.getThird().getText());
            double noiseZoom = mMapNoiseZoom.getThird().getValue();

            ComboBoxResourceItem row = (ComboBoxResourceItem) mMapGenerationLiquidLevelField.getThird().getValue();
            String liquid = row.fileName;

            row = (ComboBoxResourceItem) mMapGenerationTerrainField.getThird().getValue();
            String terrain = row.fileName;

            row = (ComboBoxResourceItem) mMapGenerationStructureField.getThird().getValue();
            String structure = row.fileName;

            int viewportWidth = mGamePaneWidth;
            int viewportHeight = mGamePaneHeight;

            Map<String, String> floors = AssetPool.getInstance().getBucketV2("floor_tiles");
            Map<String, String> structures = AssetPool.getInstance().getBucketV2("structures");

            GameConfigs gc = GameConfigs.getDefaults()
                    .setOnStartupSpriteWidth(spriteWidth)
                    .setOnStartupSpriteHeight(spriteHeight)
                    .setOnStartupCameraWidth(viewportWidth)
                    .setOnStartupCameraHeight(viewportHeight)
                    .setMapGenerationRows(rows)
                    .setMapGenerationColumns(columns)
                    .setMapGenerationTerrainMinimumElevation(minElevation)
                    .setMapGenerationLiquidElevation(liquidElevation)
                    .setMapGenerationTerrainMaximumElevation(maxElevation)
                    .setMapGenerationNoiseZoom((float) noiseZoom)
//                    .setMapGenerationNoiseZoom(.2f)
//                    .setMapGenerationTerrainAsset(new ArrayList<>(floors.keySet()).get(new Random().nextInt(floors.size())))
//                    .setMapGenerationStructureAssets(structures.keySet().stream().toList().stream().findFirst().stream().toList());
                    .setMapGenerationTerrainAsset("./" + terrain)
                    .setMapGenerationLiquidAsset("./" + liquid)
                    .setMapGenerationStructureAssets(List.of("./" + structure));

            AssetPool.getInstance().clearPool();
            if (mGameController != null && mGameController.isRunning()) { mGameController.stop(); }

            mGameController = GameController.create(gc);
//            mGameController = GameController.create(20, 20, mGamePaneWidth, mGamePaneHeight);
            mGamePaneContainer.getChildren().clear();

            Pane gamesPane = mGameController.getGamePanel();
            mGamePaneContainer.getChildren().add(gamesPane);

            mGameController.start();
            mGameController.setConfigurableStateGameplayHudIsVisible(false);
            glideToCenterOfMapAndZoomCamera(mCameraZoomField.getThird().getValue());

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


//    private Tuple<HBox, Button, TextField> getFolderToFieldRow(String text, int width, int height, float ratio) {
//        int labelWidth = (int) (width * ratio);
//        int fieldWidth = width - labelWidth;
//        Button label = new Button();
//        label.setText(text);
//        label.setAlignment(Pos.CENTER);
//        label.setPrefSize(labelWidth, height);
//        label.setMinSize(labelWidth, height);
//        label.setMaxSize(labelWidth, height);
//
//        TextField field = new TextField();
//        field.setPrefSize(fieldWidth, height);
//        field.setMinSize(fieldWidth, height);
//        field.setMaxSize(fieldWidth, height);
//        field.setEditable(false);
//
//        FileChooser fileChooser = new FileChooser();
//        label.setOnMousePressed(e -> {
//            fileChooser.showOpenDialog(EngineController.getInstance().getStage());
////            field.setText(fileChooser.get);
//        });
//
//        HBox container = new HBox();
//        container.setPrefSize(width, height);
//        container.setMinSize(width, height);
//        container.setMaxSize(width, height);
//        container.getChildren().addAll(label, field);
//
//        Tuple<HBox, Button, TextField> row = new Tuple<>(container, label, field);
//
//        return row;
//    }

    private Tuple<HBox, Label, ComboBox<String>> getLabelToComboBoxRow(String text, int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        ComboBox<String> field = new ComboBox<String>();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, ComboBox<String>> row = new Tuple<>(container, label, field);

        return row;
    }


    private void setupAssetStructure(Button accessor, String directory, TextField tf) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (directory != null && !directory.isEmpty()) { directoryChooser.setInitialDirectory(new File(directory)); }

        accessor.setOnMousePressed(e -> {
            File selected = directoryChooser.showDialog(EngineController.getInstance().getStage());

            if (selected == null) { return; }
            setupAssetDirectory(selected, tf);
        });

        if (directory == null || directory.isEmpty()) { return; }

        setupAssetDirectory(new File(directory), tf);
    }



    private void setupAssetDirectory(File selected, TextField tf) {
        File[] filesInDirectory = selected.listFiles();
        if (filesInDirectory == null) {
            return;
        }

        Tab newTab = new Tab(selected.getName(), new Label("Show all " + selected.getName()));
        mTabPane.getTabs().add(newTab);

        VBox tabRows = new VBox();
        newTab.setContent(tabRows);

        HBox row = new HBox();
        tabRows.getChildren().add(row);

        int spriteWidths = Integer.parseInt(mSpriteWidthField.getThird().getText());
        int spriteHeights = Integer.parseInt(mSpriteHeightField.getThird().getText());

        List<Pair<File, ToggleButton>> mSelectableFloorTiles = new ArrayList<>();

        for (File file : filesInDirectory) {
            // This spritesheet can be many frames, but its of the same "Sprite"/Thing
            Image rawImage = new Image("file:" + file.getAbsolutePath());
            Image firstFrame = new WritableImage(
                    rawImage.getPixelReader(), 0, 0, spriteWidths, spriteHeights
            );
            ImageView imageView = new ImageView(firstFrame);
            imageView.setFitWidth(spriteWidths * 1);
            imageView.setFitHeight(spriteHeights * 1);

            Color color = Color.GREY;
            ToggleButton button = new ToggleButton();
            button.setBorder(BevelStyle.getBordering(spriteWidths, spriteHeights, color));
            button.setGraphic(imageView);



            JavaFxUtils.addMouseEnteredEvent(button, hb -> {
                if (button.isSelected()) { return; }
                button.setStyle(ColorPalette.getJavaFxColorStyle(color.brighter()));
            });




            JavaFxUtils.addMouseExitedEvent(button, hb -> {
                if (button.isSelected()) { return; }
                button.setStyle(ColorPalette.getJavaFxColorStyle(color));
            });
            button.setStyle(ColorPalette.getJavaFxColorStyle(color));



            button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    button.setStyle(ColorPalette.getJavaFxColorStyle(ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1));
                    for (var rrrrrrrr : mSelectableFloorTiles) {
                        if (rrrrrrrr.getSecond() == button) { continue; }
                        rrrrrrrr.getSecond().setSelected(false);
                    }

                    tf.setText(file.getPath());
                } else {
                    button.setStyle(ColorPalette.getJavaFxColorStyle(color));
                }
            });




            // 🔹 **Pressed Effect (Shrink & Move Text)**
            EventHandler<Event> mButtonPressedHandler = (EventHandler<Event>) event -> {
                button.setScaleX(0.95);
                button.setScaleY(0.95);
                button.setTranslateY(2);
            };
            JavaFxUtils.addMousePressedEvent(button, mButtonPressedHandler);

            EventHandler<Event> mButtonReleasedHandler = (EventHandler<Event>) event -> {
                button.setScaleX(1.0);
                button.setScaleY(1.0);
                button.setTranslateY(0);
            };
            // 🔹 **Release Effect (Restore Text & Button Scale)**
            JavaFxUtils.addMouseReleasedEvent(button, mButtonReleasedHandler);





            int currentSize = row.getChildren().size() * spriteWidths;
            if (currentSize + spriteWidths < mDisplayPaneWidth) {
                row.getChildren().add(button);
            } else {
                row = new HBox();
                row.getChildren().add(button);
                tabRows.getChildren().add(row);
            }


            mSelectableFloorTiles.add(new Pair<>(file, button));
        }

        Random random = new Random();
        try {

            int selection = random.nextInt(mSelectableFloorTiles.size());
            tf.setText(mSelectableFloorTiles.get(selection).first.getPath());
//            mAssetMappings.put(selected.getName(), mSelectableFloorTiles);
        } catch (Exception ex) {
            System.out.println("tt,tl,");
        }
    }

    @Override
    public void start() { mGameController.start(); }

    @Override
    public void stop() { mGameController.stop(); }
}
