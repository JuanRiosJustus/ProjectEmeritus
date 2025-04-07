package main.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.game.main.GameController;
import main.ui.foundation.BeveledButton;
import main.ui.game.GamePanel;
import org.json.JSONArray;
import org.json.JSONObject;

public class GreaterAbilityInformationPanel extends GamePanel {

    private Color mColor;
    private VBox mContentPanel;
    private TargetPanel mTargetPanel;

    public GreaterAbilityInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);



        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        int genericRowWidth = width;
        int genericRowHeight = (int) (height * .05);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        int targetPanelWidth = genericRowWidth;
        int targetPanelHeight = (int) (genericRowWidth * .5);
        mTargetPanel = new TargetPanel(targetPanelWidth, (int) (targetPanelHeight * 1), mColor);


        // Create statistics panel
        // LABEL
        BeveledButton mStatisticsPanelLabel = new BeveledButton(genericRowWidth, genericRowHeight, "Statistics", mColor);
        HBox row4 = new HBox(mStatisticsPanelLabel);
//
//        mStatisticsPanelRowHeight = genericRowHeight;
//        mStatisticsPanelRowWidth = genericRowWidth;
//        mStatisticsPanel = new VBox();
//        ScrollPane scrollPane = new ScrollPane(mStatisticsPanel);
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
////        scrollPane.setFitToWidth(true);
////        scrollPane.setFitToHeight(true);
//        scrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
//        final double SPEED = 0.01;
//        scrollPane.getContent().setOnScroll(scrollEvent -> {
//            double deltaY = scrollEvent.getDeltaY() * SPEED;
//            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
//        });

//        HBox row5 = new HBox(scrollPane);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"1", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"2", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"3", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"4", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"5", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);

//        mStatisticsPanelLabel.getUnderlyingButton()
//                .setOnMousePressed(e -> {
//                    mStatisticsPanel.setVisible(!mStatisticsPanel.isVisible());
//                    mStatisticsPanel.autosize();
//                    mStatisticsPanel.setDisable(true);
//                });




        // Create Equipment panel
//        BeveledButton mEquipment = new BeveledButton(genericRowWidth, genericRowHeight, "Equipment", mColor);
//        HBox row6 = new HBox(mEquipment);

        mContentPanel.getChildren().addAll(
//                row1,
//                row2,
//                row3,
                mTargetPanel,
                row4
//                row5
        );


//
        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        gameController.getEntityOnSelectedTilesChecksumAPI(mEphemeralResponseContainer);
        int checksum = mEphemeralResponseContainer.optInt("checksum");
        if (checksum == mStateOfUnitChecksum) { return; }
        mStateOfUnitChecksum = checksum;


        JSONArray array = gameController.getEntityIDsAtSelectedTiles();
        if (array.isEmpty()) { return; }
        String unitID = array.getJSONObject(0).getString("id");

        mRequestObject.clear();
        mRequestObject.put("id", unitID);
        JSONObject response = gameController.getStatisticsForEntity(mRequestObject);
        if (response.isEmpty()) {
            return;
        }

        mTargetPanel.gameUpdate(response);
        clear();
    }
}
