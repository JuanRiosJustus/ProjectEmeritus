package main.ui.scenes.mapeditor;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.game.stores.ColorPalette;
import main.graphics.AssetPool;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;

import java.util.List;
import java.util.Random;

public class MapEditorSceneEditorPanel extends VBox  {
    private EmeritusLogger mLogger = EmeritusLogger.create(MapEditorSceneEditorPanel.class);
    private Random mRandom = new Random();
    private int mWidth = 0;
    private int mHeight = 0;
    private Tuple<HBox, BeveledButton, TextField> mMapSelectionSize = null;
    private Tuple<HBox, BeveledButton, TextField> mMapTileLayerDeletionAmount = null;
    private Tuple<HBox, BeveledButton, TextField> mMapTileSubLayerDeletionAmount = null;
    private Tuple<HBox, BeveledButton, TextField> mMapTileLayerAdditionAmount = null;
    private Tuple<HBox, BeveledButton, TextField> mMapTileSubLayerAdditionAmount = null;
    private static final String DELETE_OPERATION = "Delete";
    private static final String ADD_OPERATION = "Add";
    private static final String ADD_FLOOR_TILES = "Floors";
    private static final String ADD_WALL_TILES = "Walls";
    private static final String ADD_STRUCTURES = "Structures";
    private static final String ADD_LIQUIDS = "Liquids";
    private Tuple<HBox, BeveledButton, ComboBox<String>> mMapOperationMode = null;
    private Tuple<HBox, Button, ComboBox<Object>> mBrushLabel = null;
    private Tuple<HBox, Button, ComboBox<Object>> mAssetType = null;
    private Tuple<HBox, BeveledButton, ComboBox<String>> mSelectionType = null;
    private VBox mOperationContainer = new VBox();


    public MapEditorSceneEditorPanel(int width, int height) {

        int rowWidth = width;
        int rowHeight = (int) (height * .03);

        int brushModeContainerWidth = rowWidth;
        int brushModeContainerHeight = (int) (height * .5);
        mOperationContainer.setPrefSize(brushModeContainerWidth, brushModeContainerHeight);
        mOperationContainer.setMinSize(brushModeContainerWidth, brushModeContainerHeight);
        mOperationContainer.setMaxSize(brushModeContainerWidth, brushModeContainerHeight);
        mOperationContainer.setBackground(new Background(new BackgroundFill(ColorPalette.getRandomColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        mOperationContainer.setPickOnBounds(false);

        mMapOperationMode = JavaFXUtils.getBeveledButtonAndComboBox(rowWidth, rowHeight, .25f);
        mMapOperationMode.getSecond().setFitText("Operation");
        mMapOperationMode.getThird().getItems().add(ADD_OPERATION);
        mMapOperationMode.getThird().getItems().add(DELETE_OPERATION);
        mMapOperationMode.getThird().getSelectionModel().select(0);

        mMapOperationMode.getThird().setOnAction(e -> {
            try {
                mLogger.info("Started swapping");

                String selection = mMapOperationMode.getThird().getValue();
                switch (selection) {
                    case ADD_OPERATION -> setupAddOperation(rowWidth, rowHeight);
                    case DELETE_OPERATION -> setupDeleteOperation(rowWidth, rowHeight);
                }

                mLogger.info("Finished logging");
            } catch (Exception ex) {
                mLogger.info("Swapping logging");
            }
        });

        mMapSelectionSize = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapSelectionSize.getSecond().setText("Selection Size");
        mMapSelectionSize.getThird().setText("1");


        mMapTileLayerDeletionAmount = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapTileLayerDeletionAmount.getSecond().setText("Depth Size");
        mMapTileLayerDeletionAmount.getThird().setText("1");


//        mBrushLabel = JavaFXUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);


        getChildren().addAll(
//                mBrushLabel.getFirst(),
                mMapOperationMode.getFirst(),
                mOperationContainer
        );
    }


    private void setupAddOperation(int rowWidth, int rowHeight) {
        // DELETE N LAYERS
        mSelectionType = JavaFXUtils.getBeveledButtonAndComboBox(rowWidth, rowHeight, .25f);
        mSelectionType.getSecond().setFitText("Object");
        mSelectionType.getThird().getItems().add(ADD_FLOOR_TILES);
        mSelectionType.getThird().getItems().add(ADD_WALL_TILES);
        mSelectionType.getThird().getItems().add(ADD_STRUCTURES);
        mSelectionType.getThird().getItems().add(ADD_LIQUIDS);
        mSelectionType.getThird().getSelectionModel().select(0);
        mSelectionType.getThird().setOnAction(e -> {
            try {
                mLogger.info("Started loading assets to add");
                String selection = mSelectionType.getThird().getValue();
                handleSelection(selection);
                mLogger.info("Finished loading assets to add");
            } catch (Exception ex) {
                mLogger.info("Could not load assets to add");
            }
        });


        mMapSelectionSize = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapSelectionSize.getSecond().setFitText("Selection Size");
        mMapSelectionSize.getThird().setText("1");

        mAssetType = JavaFXUtils.getSyncedRatioImageAndComboBox(rowWidth, rowHeight * 2);

        mOperationContainer.getChildren().clear();
        mOperationContainer.getChildren().add(mSelectionType.getFirst());
        mOperationContainer.getChildren().add(mAssetType.getFirst());
        mOperationContainer.getChildren().add(mMapSelectionSize.getFirst());
    }

    private void handleSelection(String selection) {
        if (mAssetType != null) { mAssetType.getThird().getItems().clear(); }
        switch (selection) {
            case ADD_FLOOR_TILES -> setupComboBoxImages("floor_tiles", mAssetType);
            case ADD_WALL_TILES -> setupComboBoxImages("wall_tiles", mAssetType);
            case ADD_STRUCTURES -> setupComboBoxImages("structures", mAssetType);
            case ADD_LIQUIDS -> setupComboBoxImages("liquids", mAssetType);
        }
    }

    private void setupDeleteOperation(int rowWidth, int rowHeight) {
        // DELETE N LAYERS
        mMapSelectionSize = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapSelectionSize.getSecond().setFitText("Selection Size");
        mMapSelectionSize.getThird().setText("1");

        mMapTileLayerDeletionAmount = JavaFXUtils.getBeveledButtonToFieldRow(rowWidth, rowHeight, .25f);
        mMapTileLayerDeletionAmount.getSecond().setFitText("Deletion Amount");
        mMapTileLayerDeletionAmount.getThird().setText("1");

        mOperationContainer.getChildren().clear();
        mOperationContainer.getChildren().add(mMapSelectionSize.getFirst());
        mOperationContainer.getChildren().add(mMapTileLayerDeletionAmount.getFirst());
    }
    private void setupComboBoxImages(String bucketName, Tuple<HBox, Button, ComboBox<Object>> row) {

        List<String> bucket = AssetPool.getInstance().getBucket(bucketName);
        for (String asset: bucket) {
            // This spritesheet can be many frames, but its of the same "Sprite"/Thing
            String id = AssetPool.getInstance().getOrCreateAsset(
                    AssetPool.getInstance().getNativeSpriteSize(),
                    AssetPool.getInstance().getNativeSpriteSize(),
                    asset,
                    AssetPool.STATIC_ANIMATION,
                    -1,
                    asset + " rrrr"
            );
            Image image = AssetPool.getInstance().getImage(id);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(row.getSecond().getPrefWidth());
            imageView.setFitHeight(row.getSecond().getPrefHeight());

            ComboBoxResourceItem item = new ComboBoxResourceItem(image, asset);
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
                            label.setText(item.asset);
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
                    setText(item.asset);

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

    public String getSelectionType() { return mSelectionType != null ?  mSelectionType.getThird().getValue() : ""; }
    public String getAsset() {
        if (mAssetType == null) { return null; }
        ComboBoxResourceItem item = (ComboBoxResourceItem) mAssetType.getThird().getValue();
        if (item == null) {
            return null;
        }
        return item.asset;
    }

    public String getOperation() { return mMapOperationMode.getThird().getValue(); }
    public boolean isDeletionMode() { return mMapOperationMode.getThird().getValue().equalsIgnoreCase(DELETE_OPERATION); }
    public boolean isAdditionMode() { return mMapOperationMode.getThird().getValue().equalsIgnoreCase(ADD_OPERATION); }
    public int getSelectionSize() { return Integer.parseInt(mMapSelectionSize.getThird().getText()); }

    private static class ComboBoxResourceItem {
        public final String asset;
        public final Image image;

        public ComboBoxResourceItem(Image image, String asset) {
            this.image = image;
            this.asset = asset;
        }
    }

}
