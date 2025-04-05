package main.ui;

import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.constants.JavaFxUtils;
import main.constants.Tuple;
import main.game.components.SecondTimer;
import main.game.main.GameController;
import main.game.stores.pools.FontPool;
import main.logging.EmeritusLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

public class DevPanel extends Stage {

    private final EmeritusLogger mLogger = EmeritusLogger.create(DevPanel.class);

    private SecondTimer st = new SecondTimer();
    private Tuple<HBox, Label, CheckBox> mAutoEndTurns = null;
    private Tuple<HBox, Label, ComboBox<String>> mCameraModes = null;
    public DevPanel(GameController controller, int width, int height) {
        setTitle("Dev Panel");

//        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        List<Screen> screens = Screen.getScreens();
        setX(screens.get(0).getBounds().getWidth() - width);
        setY((0));
//        setX
        setAlwaysOnTop(true);
        setOpacity(.9f);

//        setFont(Font.font("Verdana", FontWeight.BOLD, 70));


        int rowWidths = width;
        int rowHeights = (int) (height * .15);

        mAutoEndTurns = JavaFxUtils.getLabelToSwitchButton("Auto End Turns", rowWidths, rowHeights);
        mAutoEndTurns.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));

        mCameraModes = JavaFxUtils.getLabelAndComboBox(rowWidths, rowHeights, .4f);
        mCameraModes.getSecond().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
        mCameraModes.getSecond().setText("Camera Mode:");
        mCameraModes.getThird().getEditor().setFont(FontPool.getInstance().getBoldFontForHeight(rowHeights));
        controller.getCameraModes().toList().forEach(e -> mCameraModes.getThird().getItems().add((String)e));
        mCameraModes.getThird().setOnAction(e -> {
            String cameraMode = mCameraModes.getThird().getSelectionModel().getSelectedItem();
            JSONObject request = new JSONObject();
            request.put("mode", cameraMode);
            controller.setCameraMode(request);
            mLogger.info("Setting camera mode");
        });
        mCameraModes.getThird().getSelectionModel().select(0);



        VBox vBox = new VBox();
        vBox.getChildren().addAll(
                mAutoEndTurns.getFirst(),
                mCameraModes.getFirst()
        );

//        vBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        vBox.setCache(true);
        vBox.setCacheHint(CacheHint.SPEED);

        setScene(new Scene(vBox, width, height));

    }
    public void gameUpdate(GameController gc) {

        if (st.elapsed() <= 5) { return; }
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
