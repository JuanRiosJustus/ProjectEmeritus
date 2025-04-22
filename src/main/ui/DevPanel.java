package main.ui;

import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.constants.JavaFXUtils;
import main.constants.Pair;
import main.constants.Tuple;
import main.constants.SecondTimer;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stores.FontPool;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

public class DevPanel extends Stage {

    private final EmeritusLogger mLogger = EmeritusLogger.create(DevPanel.class);

    private SecondTimer st = new SecondTimer();
    private Tuple<HBox, Label, CheckBox> mAutoEndTurnsRow = null;
    private Tuple<HBox, Label, ComboBox<String>> mCameraModesRow = null;
    private Pair<HBox, Button> mForcefullyEndTurnRow = null;

    public DevPanel(GameModel gameModel, int width, int height) {
        setTitle("Dev Panel");

//        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        List<Screen> screens = Screen.getScreens();
        setX(screens.get(0).getBounds().getWidth() - width);
        setY((0));
//        setX
//        setAlwaysOnTop(true);
        setOpacity(.9f);

//        setFont(Font.font("Verdana", FontWeight.BOLD, 70));


        int rowWidths = width;
        int rowHeights = (int) (height * .15);

        mAutoEndTurnsRow = JavaFXUtils.getLabelToSwitchButton("Auto End Turns", rowWidths, rowHeights);
        mAutoEndTurnsRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));

        mCameraModesRow = JavaFXUtils.getLabelAndComboBox(rowWidths, rowHeights, .4f);
        mCameraModesRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
        mCameraModesRow.getSecond().setText("Camera Mode:");
        mCameraModesRow.getThird().getEditor().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
        gameModel.getCameraModes().stream().toList().forEach(e -> mCameraModesRow.getThird().getItems().add((String)e));
        mCameraModesRow.getThird().setOnAction(e -> {
            String cameraMode = mCameraModesRow.getThird().getSelectionModel().getSelectedItem();
            JSONObject request = new JSONObject();
            request.put("mode", cameraMode);
            gameModel.setCameraMode(request);
            mLogger.info("Setting camera mode");
        });
        mCameraModesRow.getThird().getSelectionModel().select(0);



        mForcefullyEndTurnRow = JavaFXUtils.getButtonRow(rowWidths, rowHeights);
        mForcefullyEndTurnRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
        mForcefullyEndTurnRow.getSecond().setText("Force Turn End");
        mForcefullyEndTurnRow.getSecond().setOnAction(e -> { gameModel.forcefullyEndTurn(); });



        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                mAutoEndTurnsRow.getFirst(),
                mCameraModesRow.getFirst(),
                mForcefullyEndTurnRow.getFirst()
        );

//        vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        vBox.setCache(true);
        vBox.setCacheHint(CacheHint.SPEED);

        setScene(new Scene(vBox, width, height));

    }

//    public DevPanel(GameController controller, int width, int height) {
//        setTitle("Dev Panel");
//
////        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
//        List<Screen> screens = Screen.getScreens();
//        setX(screens.get(0).getBounds().getWidth() - width);
//        setY((0));
////        setX
////        setAlwaysOnTop(true);
//        setOpacity(.9f);
//
////        setFont(Font.font("Verdana", FontWeight.BOLD, 70));
//
//
//        int rowWidths = width;
//        int rowHeights = (int) (height * .15);
//
//        mAutoEndTurnsRow = JavaFXUtils.getLabelToSwitchButton("Auto End Turns", rowWidths, rowHeights);
//        mAutoEndTurnsRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
//
//        mCameraModesRow = JavaFXUtils.getLabelAndComboBox(rowWidths, rowHeights, .4f);
//        mCameraModesRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
//        mCameraModesRow.getSecond().setText("Camera Mode:");
//        mCameraModesRow.getThird().getEditor().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
//        controller.getCameraModes().toList().forEach(e -> mCameraModesRow.getThird().getItems().add((String)e));
//        mCameraModesRow.getThird().setOnAction(e -> {
//            String cameraMode = mCameraModesRow.getThird().getSelectionModel().getSelectedItem();
//            JSONObject request = new JSONObject();
//            request.put("mode", cameraMode);
//            controller.setCameraMode(request);
//            mLogger.info("Setting camera mode");
//        });
//        mCameraModesRow.getThird().getSelectionModel().select(0);
//
//
//
//        mForcefullyEndTurnRow = JavaFXUtils.getButtonRow(rowWidths, rowHeights);
//        mForcefullyEndTurnRow.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
//        mForcefullyEndTurnRow.getSecond().setText("Force Turn End");
//        mForcefullyEndTurnRow.getSecond().setOnAction(e -> { controller.forcefullyEndTurn(); });
//
//
//
//        VBox vBox = new VBox();
//        vBox.getChildren().addAll(
//                mAutoEndTurnsRow.getFirst(),
//                mCameraModesRow.getFirst(),
//                mForcefullyEndTurnRow.getFirst()
//        );
//
////        vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        vBox.setCache(true);
//        vBox.setCacheHint(CacheHint.SPEED);
//
//        setScene(new Scene(vBox, width, height));
//
//    }



    public void gameUpdate(GameController gc) {

//        if (st.elapsed() <= 5) { return; }
//        JSONArray allUnitIDs = gc.getAllUnitIDs();
//        for (int i = 0; i < allUnitIDs.length(); i++) {
//            String unitID = allUnitIDs.getString(i);
//            mCameraModes.getThird().getItems().add(unitID);
//        }
//        mCameraModes.getThird().setOnAction(e -> {
//            String unitID = mCameraModes.getThird().getSelectionModel().getSelectedItem();
//            JSONObject request = new JSONObject();
//            request.put("id", unitID);
//            gc.setCameraMode(request);
//            System.out.println("ANCHORING");
//        });
    }

    public void gameUpdate(GameModel gameModel) {

//        if (st.elapsed() <= 5) { return; }
//        JSONArray allUnitIDs = gc.getAllUnitIDs();
//        for (int i = 0; i < allUnitIDs.length(); i++) {
//            String unitID = allUnitIDs.getString(i);
//            mCameraModes.getThird().getItems().add(unitID);
//        }
//        mCameraModes.getThird().setOnAction(e -> {
//            String unitID = mCameraModes.getThird().getSelectionModel().getSelectedItem();
//            JSONObject request = new JSONObject();
//            request.put("id", unitID);
//            gc.setCameraMode(request);
//            System.out.println("ANCHORING");
//        });
    }
}
