package main.ui.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Tuple;
import main.engine.EngineRunnable;
import main.game.main.GameController;
import main.game.stores.pools.asset.AssetPool;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledTextField;
import main.ui.game.JavaFxUtils;

import java.util.Map;

public class MapEditorScene extends EngineRunnable {
    private GameController mGameController = null;
    public MapEditorScene(int width, int height) { super(width, height); }
    @Override
    public Scene render() {
        int toolsPaneWidth = (int) (mWidth * .25);
        int toolsPaneHeight = mHeight;
        int toolsPaneX = mWidth - toolsPaneWidth;
        int toolsPaneY = 0;

//        Pane toolsPane = JavaFxUtils.createWrapperPane(toolsPaneWidth, toolsPaneHeight);
        VBox toolsPane = getToolsContainer(toolsPaneWidth, toolsPaneHeight);

        toolsPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        toolsPane.setLayoutX(toolsPaneX);
        toolsPane.setLayoutY(toolsPaneY);

        int gamesPaneWidth = mWidth - toolsPaneWidth;
        int gamesPaneHeight = mHeight;
        int gamesPaneX = 0;
        int gamesPaneY = 0;
        mGameController = GameController.create(20, 20, gamesPaneWidth, gamesPaneHeight);
        Pane gamesPane = mGameController.getGamePanel();
        mGameController.setConfigurableStateGameplayHudIsVisible(false);

        Pane sp = JavaFxUtils.createWrapperPane(0, 0, mWidth, mHeight);
        sp.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        sp.getChildren().addAll(toolsPane, gamesPane);
        Scene editorScene = new Scene(sp, mWidth, mHeight);
        return editorScene;
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


        Tuple<HBox, Label, TextField> mapName = getLabelToFieldRow("Map Name", rowWidth, rowHeight, .25f);
        Tuple<HBox, Label, BeveledTextField> mapRows = getLabelToBeveledFieldRow("Rows", rowWidth, rowHeight, .25f);
        Tuple<HBox, Label, TextField> mapColumns = getLabelToFieldRow("Columns", rowWidth, rowHeight, .25f);
        Tuple<HBox, Label, TextField> mapNoise = getLabelToFieldRow("Noise", rowWidth, rowHeight, .25f);


        Map<String, String> floors = AssetPool.getInstance().getBucketV2("floor_tiles");
        Map<String, String> structures = AssetPool.getInstance().getBucketV2("structures");

        Tuple<HBox, Label, ComboBox<String>> foundationAsset = getLabelToComboBoxRow("Foundation",  rowWidth, rowHeight, .25f);
        foundationAsset.getThird().getItems().addAll(floors.keySet());



        container.getChildren().addAll(
                mapName.getFirst(),
                mapRows.getFirst(),
                mapColumns.getFirst(),
                mapNoise.getFirst(),
                new Label("======================="),
                foundationAsset.getFirst()

        );

        Tuple<HBox, Label, TextField> row = getLabelToFieldRow("Map Name", rowWidth, rowHeight, .25f);
        for (int i = 0; i < 15; i++) {
//            Tuple<HBox, BeveledLabel, BeveledTextField> row = getRow(rowWidth, rowHeight, .3f, color);
//            row.getSecond().setText(" "+ i);
//            container.getChildren().add(row.getFirst());
//            container.getChildren().add(new TextField(i + " textfield"));
        }


        return container;
    }

    private Tuple<HBox, Label, BeveledTextField> getLabelToBeveledFieldRow(String text, int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        BeveledTextField field = new BeveledTextField(fieldWidth, height, Color.SLATEGRAY);
//        field.setPrefSize(fieldWidth, height);
//        field.setMinSize(fieldWidth, height);
//        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, BeveledTextField> row = new Tuple<>(container, label, field);

        return row;
    }

    private Tuple<HBox, Label, TextField> getLabelToFieldRow(String text, int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField field = new TextField();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, TextField> row = new Tuple<>(container, label, field);

        return row;
    }

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

//    private Tuple<HBox, BeveledLabel, BeveledTextField> getRow(int width, int height, float ratio, Color color) {
//        int labelWidth = (int) (width * ratio);
//        int fieldWidth = width - labelWidth;
//        BeveledLabel label = new BeveledLabel(labelWidth, height, "", Color.GRAY);
//        BeveledTextField field = new BeveledTextField(fieldWidth / 2, height, color);
//
//        HBox container = new HBox();
//        container.setPrefSize(width, height);
//        container.setMinSize(width, height);
//        container.setMaxSize(width, height);
//        container.getChildren().addAll(label, field);
//
//        Tuple<HBox, BeveledLabel, BeveledTextField> row = new Tuple<>(container, label, field);
//
//        return row;
//    }

//    private
    @Override
    public void start() { mGameController.start(); }

    @Override
    public void stop() { mGameController.stop(); }
}
