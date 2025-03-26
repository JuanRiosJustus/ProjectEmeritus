package main.ui.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.constants.Pair;
import main.constants.Tuple;
import main.engine.EngineController;
import main.engine.EngineRunnable;
import main.game.main.GameConfigs;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.asset.AssetPool;
import main.logging.EmeritusLogger;
import main.ui.foundation.BevelStyle;
import main.ui.foundation.BeveledButton;
import main.constants.JavaFxUtils;
import main.utils.RandomUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapEditorScene extends EngineRunnable {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(MapEditorScene.class);
    private GameController mGameController = null;
    private List<ImageView> mAssetViews = new ArrayList<>();
    private List<Pair<File, ToggleButton>> mSelectableFloorTiles = new ArrayList<>();
    public MapEditorScene(int width, int height) { super(width, height); }

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


    private Tuple<HBox, Label, TextField> mMapName = null;
    private Tuple<HBox, Label, TextField> mMapRowsField = null;
    private Tuple<HBox, Label, TextField> mMapColumnsField = null;
    private Tuple<HBox, Label, TextField> mSpriteHeightField = null;
    private Tuple<HBox, Label, TextField> mSpriteWidthField = null;
    private Tuple<HBox, Label, Slider> mCameraZoomField = null;
    private Tuple<HBox, Label, TextField> mMinTileHeightField = null;
    private Tuple<HBox, Label, TextField> mMaxTileHeightField = null;
    private Tuple<HBox, Label, Slider> mMapNoiseZoom = null;
    private Tuple<HBox, Button, TextField> mMapTerrainField = null;
    private Tuple<HBox, Button, TextField> mMapStructuresField = null;

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

        mMapName = JavaFxUtils.getLabelToFieldRow(rowWidth, rowHeight, .25f);
        mMapName.getSecond().setText("Map Name");
        mMapName.getThird().setText(RandomUtils.createRandomName(3, 7));

        mMapRowsField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMapRowsField.getSecond().setText("Map Rows");
        mMapRowsField.getThird().setText(String.valueOf(20));

        mMapColumnsField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMapColumnsField.getSecond().setText("Map Columns");
        mMapColumnsField.getThird().setText(String.valueOf(25));

        mMinTileHeightField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMinTileHeightField.getSecond().setText("Min Height");
        mMinTileHeightField.getThird().setText(String.valueOf(-5));

        mMaxTileHeightField = JavaFxUtils.getLabelAndIntegerField(rowWidth, rowHeight, .25f);
        mMaxTileHeightField.getSecond().setText("Max Height");
        mMaxTileHeightField.getThird().setText(String.valueOf(5));

        mMapNoiseZoom = JavaFxUtils.getButtonAndSliderField(rowWidth, rowHeight, .25f);
        mMapNoiseZoom.getThird().setMin(.05);
        mMapNoiseZoom.getThird().setValue(.5f);
        mMapNoiseZoom.getThird().setMax(.95);
        mMapNoiseZoom.getSecond().setText("Noise Zoom");
        mMapNoiseZoom.getThird().setShowTickLabels(true);
        mMapNoiseZoom.getThird().setSnapToTicks(true);
        mMapNoiseZoom.getThird().setMinorTickCount(1);
        mMapNoiseZoom.getThird().setMajorTickUnit(.05);

        mMapTerrainField = JavaFxUtils.getButtonToFieldRow(rowWidth, rowHeight, .25f);
        setupAssetStructure(mMapTerrainField.getSecond(), "res/graphics/floor_tiles", mMapTerrainField.getThird());
        mMapTerrainField.getSecond().setText("Terrain");

        mMapStructuresField = JavaFxUtils.getButtonToFieldRow(rowWidth, rowHeight, .25f);
        setupAssetStructure(mMapStructuresField.getSecond(), "res/graphics/structures", mMapStructuresField.getThird());
        mMapStructuresField.getSecond().setText("Structures");


        Button generateMapButton = new Button("Generate Map");
        generateMapButton.setPrefSize(rowWidth, rowHeight);
        generateMapButton.setMinSize(rowWidth, rowHeight);
        generateMapButton.setMaxSize(rowWidth, rowHeight);
        generateMapButton.setOnAction(e -> {
            createNewGame();
        });


        Map<String, String> floors = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> structures = AssetPool.getInstance().getBucketV2("structures");

        Tuple<HBox, Label, ComboBox<String>> foundationAsset = getLabelToComboBoxRow("Foundation",  rowWidth, rowHeight, .25f);
        foundationAsset.getThird().getItems().addAll(floors.keySet());



        container.getChildren().addAll(
                mMapName.getFirst(),
                mMapRowsField.getFirst(),
                mMapColumnsField.getFirst(),
                mSpriteWidthField.getFirst(),
                mSpriteHeightField.getFirst(),
                mMinTileHeightField.getFirst(),
                mMaxTileHeightField.getFirst(),
                mMapTerrainField.getFirst(),
                mMapStructuresField.getFirst(),
                mMapNoiseZoom.getFirst(),
                generateMapButton,
                mCameraZoomField.getFirst(),
                new Label("=======================")

        );

        return container;
    }

    private void createNewGame() {
        try {
            mLogger.info("Starting to create new GameController!");
            int rows = Integer.parseInt(mMapRowsField.getThird().getText());
            int columns = Integer.parseInt(mMapColumnsField.getThird().getText());
            int spriteWidth = Integer.parseInt(mSpriteWidthField.getThird().getText());
            int spriteHeight = Integer.parseInt(mSpriteHeightField.getThird().getText());
            int viewportWidth = mGamePaneWidth;
            int viewportHeight = mGamePaneHeight;
            String terrain = mMapTerrainField.getThird().getText();
            String structure = mMapStructuresField.getThird().getText();
//
            Map<String, String> floors = AssetPool.getInstance().getBucketV2("floor_tiles");
            Map<String, String> structures = AssetPool.getInstance().getBucketV2("structures");

            GameConfigs gc = GameConfigs.getDefaults()
                    .setMapGenerationRows(rows)
                    .setMapGenerationColumns(columns)
                    .setOnStartupSpriteWidth(spriteWidth)
                    .setOnStartupSpriteHeight(spriteHeight)
                    .setOnStartupCameraWidth(viewportWidth)
                    .setOnStartupCameraHeight(viewportHeight)
//                .setMapGenerationNoiseZoom(.2f)
//                    .setMapGenerationTerrainAsset(new ArrayList<>(floors.keySet()).get(new Random().nextInt(floors.size())))
//                    .setMapGenerationStructureAssets(structures.keySet().stream().toList().stream().findFirst().stream().toList());
                    .setMapGenerationTerrainAsset("./" + terrain)
                    .setMapGenerationStructureAssets(List.of("./" + structure));

            mGameController = GameController.create(gc);
//            mGameController = GameController.create(20, 20, mGamePaneWidth, mGamePaneHeight);
            mGamePaneContainer.getChildren().clear();

            Pane gamesPane = mGameController.getGamePanel();
            mGamePaneContainer.getChildren().add(gamesPane);

            mGameController.start();
            mGameController.setConfigurableStateGameplayHudIsVisible(false);
            glideToCenterOfMapAndZoomCamera(mCameraZoomField.getThird().getValue());

            System.gc();
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
        } catch (Exception ex) {
            System.out.println("tt,tl,");
        }
    }

    @Override
    public void start() { mGameController.start(); }

    @Override
    public void stop() { mGameController.stop(); }
}
