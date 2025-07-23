package main.ui.scenes.mapeditor;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.graphics.AnimationPool;
import main.ui.foundation.BeveledButton;
import main.utils.RandomUtils;

import java.io.File;
import java.util.Random;

public class MapEditorSceneGeneratorPanel extends VBox  {
    private Random mRandom = new Random();
    private int mWidth = 0;
    private int mHeight = 0;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationName = null;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationRows = null;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationColumns = null;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationMinElevation = null;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationWaterLevel = null;
    private Tuple<HBox, BeveledButton, TextField> mMapGenerationMaxElevation = null;
    private Slider mMapNoiseZoom = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationLiquidLevelField = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationTerrainField = null;
    private Tuple<HBox, Button, ComboBox<Object>> mMapGenerationStructureField = null;
    private Slider mCameraZoomField = null;

    public MapEditorSceneGeneratorPanel(int width, int height) {

        int rowWidth = width;
        int rowHeight = (int) (height * .03);

        mMapGenerationName = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapGenerationName.getSecond().setText("Name");
        mMapGenerationName.getThird().setText(RandomUtils.createRandomName(3, 7));

        mMapGenerationRows = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapGenerationRows.getSecond().setText("Rows");
        mMapGenerationRows.getThird().setText(String.valueOf(20));

        mMapGenerationColumns = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapGenerationColumns.getSecond().setText("Columns");
        mMapGenerationColumns.getThird().setText(String.valueOf(25));

        mMapGenerationMinElevation = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .4f);
        mMapGenerationMinElevation.getSecond().setText("Min Elevation");
        mMapGenerationMinElevation.getThird().setText(String.valueOf(0));

        mMapGenerationWaterLevel = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .4f);
        mMapGenerationWaterLevel.getSecond().setText("Liquid Elevation");
        mMapGenerationWaterLevel.getThird().setText(String.valueOf(3));

        mMapGenerationMaxElevation = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .4f);
        mMapGenerationMaxElevation.getSecond().setText("Max Elevation");
        mMapGenerationMaxElevation.getThird().setText(String.valueOf(10));




        int assetRowWidth = rowWidth;
        int assetRowHeight = rowHeight * 2;
        HBox mapGenerationTerrainRow = JavaFXUtils.createHBox((int) (assetRowWidth * .9), assetRowHeight);
        int assetButtonWidth = (int) (assetRowWidth * .4);
        int assetButtonHeight = assetRowHeight;
        BeveledButton assetButton = new BeveledButton(assetButtonWidth, assetButtonHeight);
        assetButton.setFitText("Terrain");
        mMapGenerationTerrainField = JavaFXUtils.getSyncedRatioImageAndComboBox(assetRowWidth - assetButtonWidth, rowHeight * 2);
        setupComboImages("res/graphics/floor_tiles", mMapGenerationTerrainField);
        mapGenerationTerrainRow.getChildren().addAll(assetButton, mMapGenerationTerrainField.getFirst());



        HBox mapGenerationStructureRow = JavaFXUtils.createHBox((int) (assetRowWidth * .9), assetRowHeight);
        assetButton = new BeveledButton(assetButtonWidth, assetButtonHeight);
        assetButton.setFitText("Structure");
        mMapGenerationStructureField = JavaFXUtils.getSyncedRatioImageAndComboBox(assetRowWidth - assetButtonWidth, rowHeight * 2);
        setupComboImages("res/graphics/structures", mMapGenerationStructureField);
        mapGenerationStructureRow.getChildren().addAll(assetButton, mMapGenerationStructureField.getFirst());



        HBox mapGenerationLiquidsRow = JavaFXUtils.createHBox((int) (assetRowWidth * .9), assetRowHeight);
        assetButton = new BeveledButton(assetButtonWidth, assetButtonHeight);
        assetButton.setFitText("Liquids");
        mMapGenerationLiquidLevelField = JavaFXUtils.getSyncedRatioImageAndComboBox(assetRowWidth - assetButtonWidth, rowHeight * 2);
        setupComboImages("res/graphics/liquids", mMapGenerationLiquidLevelField);
        mapGenerationLiquidsRow.getChildren().addAll(assetButton, mMapGenerationLiquidLevelField.getFirst());



        HBox mapGenerationNoiseRow = JavaFXUtils.createHBox((int) (assetRowWidth * .9), assetRowHeight);
        assetButton = new BeveledButton(assetButtonWidth, assetButtonHeight);
        assetButton.setFitText("Map Noise");

        mMapNoiseZoom = JavaFXUtils.createSlider(assetRowWidth - assetButtonWidth, rowHeight * 2); //  = JavaFXUtils.getButtonAndSliderField(assetRowWidth - assetButtonWidth, rowHeight * 2, .25f);\
        mMapNoiseZoom.setMin(.005);
        mMapNoiseZoom.setValue(.8f);
        mMapNoiseZoom.setMax(.995);
        mMapNoiseZoom.setShowTickLabels(true);
//        mMapNoiseZoom.setSnapToTicks(true);
        mMapNoiseZoom.setMinorTickCount(1);
        mMapNoiseZoom.setMajorTickUnit(.5);
        mapGenerationNoiseRow.getChildren().addAll(assetButton, mMapNoiseZoom);



        mCameraZoomField = JavaFXUtils.createSlider(assetRowWidth - assetButtonWidth, rowHeight * 2); //JavaFXUtils.getButtonAndSliderField(rowWidth, rowHeight, .25f);
        mCameraZoomField.setMin(0.00001);
        mCameraZoomField.setValue(.75);
        mCameraZoomField.setMax(2);
        mCameraZoomField.setShowTickLabels(true);
        mCameraZoomField.setSnapToTicks(true);
        mCameraZoomField.setMinorTickCount(1);
        mCameraZoomField.setMajorTickUnit(.05);

        HBox mapGenerationCameraZoomRow = JavaFXUtils.createHBox((int) (assetRowWidth * .9), assetRowHeight);
        assetButton = new BeveledButton(assetButtonWidth, assetButtonHeight);
        assetButton.setFitText("Camera Zoom");

        mapGenerationCameraZoomRow.getChildren().addAll(assetButton, mCameraZoomField);






        getChildren().addAll(
                mMapGenerationName.getFirst(),
                mMapGenerationRows.getFirst(),
                mMapGenerationColumns.getFirst(),
                mMapGenerationMinElevation.getFirst(),
                mMapGenerationWaterLevel.getFirst(),
                mMapGenerationMaxElevation.getFirst(),

                mapGenerationTerrainRow,
                mapGenerationStructureRow,
                mapGenerationLiquidsRow,
                mapGenerationNoiseRow,
                mapGenerationCameraZoomRow
        );
    }

    public double getCameraZoom() { return mCameraZoomField.getValue(); }
    public Slider getCameraZoomSlider() { return mCameraZoomField; }

    private static class ComboBoxResourceItem {
        private final String name;
        private final String fileName;
        private final Image image;

        public ComboBoxResourceItem(Image image, String fileName, String name) {
            this.name = name;
            this.image = image;
            this.fileName = fileName;
        }
    }


    private void setupComboImages(String directory, Tuple<HBox, Button, ComboBox<Object>> row) {
        File files = new File(directory);
        File[] filesInDirectory = files.listFiles();
        if (filesInDirectory == null) {
            return;
        }

        for (File file : filesInDirectory) {
            // This spritesheet can be many frames, but its of the same "Sprite"/Thing
            Image rawImage = new Image("file:" + file.getAbsolutePath());
            Image firstFrame = new WritableImage(rawImage.getPixelReader(),
                    0,
                    0,
                    AnimationPool.getInstance().getNativeSpriteSize(),
                    AnimationPool.getInstance().getNativeSpriteSize()
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



    public double getNoiseZoom() { return mMapNoiseZoom.getValue(); }
    public String getRows() { return mMapGenerationRows.getThird().getText(); }
    public String getColumns() { return mMapGenerationColumns.getThird().getText(); }
    public String getMinElevation() { return mMapGenerationMinElevation.getThird().getText(); }
    public String getWaterElevation() { return mMapGenerationWaterLevel.getThird().getText(); }
    public String getMaxElevation() { return mMapGenerationMaxElevation.getThird().getText(); }
    public String getTerrainAsset() {
        ComboBoxResourceItem row = (ComboBoxResourceItem) mMapGenerationTerrainField.getThird().getValue();
        return row.fileName;
    }
    public String getStructureAsset() {
        ComboBoxResourceItem row = (ComboBoxResourceItem) mMapGenerationStructureField.getThird().getValue();
        return row.fileName;
    }
    public String getTerrainAssetField() {
        ComboBoxResourceItem item = (ComboBoxResourceItem) mMapGenerationTerrainField.getThird().getValue();
        return item.fileName;
    }

    public String getStructureAssetField() {
        ComboBoxResourceItem item = (ComboBoxResourceItem) mMapGenerationStructureField.getThird().getValue();
        return item.fileName;
    }

    public String getLiquidAssetField() {
        ComboBoxResourceItem item = (ComboBoxResourceItem) mMapGenerationLiquidLevelField.getThird().getValue();
        return item.fileName;
    }
}
