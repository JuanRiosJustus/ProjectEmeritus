package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import main.constants.Pair;
import main.game.main.GameModel;
import main.game.stores.FontPool;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledKVP;
import main.ui.foundation.BeveledProgressBar;
import main.ui.game.GamePanel;
import main.constants.JavaFXUtils;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class GreaterStatisticsPanel extends GamePanel {

    private static final String[] stats = new String[]{
            "health",
            "mana",
            "stamina",
            "",
            "level",
            "experience",
            "",
            "physical_attack",
            "physical_defense",
            "magical_attack",
            "magical_defense",
            "",
            "move",
            "climb",
            "jump",
            "speed"
    };

    private static final Set<String> RESOURCES = Set.of("health", "mana", "stamina");
    private static final EmeritusLogger mLogger = EmeritusLogger.create(StatisticsPanel.class);
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledButton, BeveledButton>> mRows = new HashMap<>();
    private Color mColor = null;
    private int mSelectedTilesChecksum = 0;
    private int mStateOfUnitChecksum = 0;
    private BeveledButton levelLabel = null;
//    private final Map<String, Pair<BeveledButton, BeveledButton>> mStatisticsPanelMap = new LinkedHashMap<>();
    private final Map<String, BeveledKVP> mStatisticsPanelMap = new LinkedHashMap<>();
    private VBox mStatisticsPanel = null;
    private int mStatisticsPanelRowWidth = 0;
    private int mStatisticsPanelRowHeight = 0;
    private UnitPreviewPanel mUnitPreviewPanel = null;
    private int mCurrentEntityHash = -1;
    private String mCurrentEntityID = null;

    public GreaterStatisticsPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        int genericRowWidth = width;
        int genericRowHeight = (int) (height * .05);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);
        mContentPanel.setAlignment(Pos.CENTER);

        int targetPanelWidth = genericRowWidth;
        int targetPanelHeight = (int) (genericRowWidth * .6);
        mUnitPreviewPanel = new UnitPreviewPanel(targetPanelWidth, (int) (targetPanelHeight * 1), mColor);


        // Create statistics panel
        // LABEL
        int statisticsBannerWidth = genericRowWidth;
        int statisticsBannerHeight = genericRowHeight;
        BeveledButton mStatisticsPanelLabel = new BeveledButton(statisticsBannerWidth, statisticsBannerHeight);
        mStatisticsPanelLabel.setFitText("Statistics");
        mStatisticsPanelLabel.setFont(FontPool.getInstance().getFontForHeight(statisticsBannerHeight));
        mStatisticsPanelLabel.setExtrusionFactor(.1);
        mStatisticsPanelLabel.setBorder((int) (statisticsBannerWidth * .01), (int) (statisticsBannerHeight * .01), mColor);


        HBox row4 = new HBox(mStatisticsPanelLabel);

        mStatisticsPanelRowHeight = genericRowHeight;
        mStatisticsPanelRowWidth = genericRowWidth;
        mStatisticsPanel = new VBox();
        ScrollPane scrollPane = new ScrollPane(mStatisticsPanel);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.setFitToWidth(true);
//        scrollPane.setFitToHeight(true);
        scrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        final double SPEED = 0.01;
        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });

        HBox row5 = new HBox(scrollPane);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"1", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"2", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"3", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"4", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);
//        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"5", mStatisticsPanelRowWidth, mStatisticsPanelRowHeight);

        mStatisticsPanelLabel.getUnderlyingButton().setOnMousePressed(e -> {
            mStatisticsPanel.setVisible(!mStatisticsPanel.isVisible());
            mStatisticsPanel.autosize();
//            mStatisticsPanel.setDisable(true);
        });




        // Create Equipment panel
//        BeveledButton mEquipment = new BeveledButton(genericRowWidth, genericRowHeight, "Equipment", mColor);
//        HBox row6 = new HBox(mEquipment);

        mContentPanel.getChildren().addAll(
//                row1,
//                row2,
//                row3,
                mUnitPreviewPanel,
                row4,
                row5
        );


//
        getChildren().add(mContentPanel);

//        setEffect(JavaFXUtils.createBasicDropShadow(width, height));
        JavaFXUtils.setCachingHints(this);
    }


//    public Pair<BeveledButton, BeveledButton> getOrCreateKeyValueRow(
//            Map<String, Pair<BeveledButton, BeveledButton>> trackingMap, VBox container, String name, int width, int height) {
//        Pair<BeveledButton, BeveledButton> newRow = trackingMap.get(name);
//        if (newRow != null) {
//            return newRow;
//        }
//
//        int rowWidth = (int) (width * .99);
//        int rowHeight = height;
//        Color color = mColor;
//
//        // ✅ Create Beveled Labels
//        BeveledButton leftLabel = new BeveledButton((int) (rowWidth * .666), rowHeight, color);
//        leftLabel.setTextAlignment(Pos.CENTER_LEFT);
//        leftLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight));
//        leftLabel.disableBevelEffect();
//        leftLabel.disableMouseEnteredAndExitedEffect();
//
//        BeveledButton rightLabel = new BeveledButton((int) (rowWidth * .333), rowHeight, color);
//        rightLabel.setTextAlignment(Pos.CENTER_RIGHT);
//        rightLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight));
//        rightLabel.disableBevelEffect();
//        rightLabel.disableMouseEnteredAndExitedEffect();
//
//        HBox contentPane = new HBox(leftLabel, rightLabel);
//        Pane centeringPane = JavaFXUtils.createHorizontallyCenteringPane(contentPane, width, height, rowWidth);
//        contentPane.setSpacing(2);
//        container.getChildren().add(centeringPane);
//
////        contentPane.setOnMouseEntered(e -> {
////            leftLabel.setBackground(color.brighter());
////            rightLabel.setBackground(color.brighter());
////
////        });
////        contentPane.setOnMouseExited(e -> {
////            leftLabel.setBackground(color);
////            rightLabel.setBackground(color);
////        });
//
//        Pair<BeveledButton, BeveledButton> pair = new Pair<>(leftLabel, rightLabel);
//        trackingMap.put(name, pair);
//
//        return pair;
//    }

    public BeveledKVP getOrCreateKeyValueRow( VBox container, String name, int width, int height) {
        BeveledKVP newRow = mStatisticsPanelMap.get(name);
        if (newRow != null) {
            return newRow;
        }

        int rowWidth = (int) (width * .99);
        int rowHeight = height;
        Color color = mColor;


        BeveledKVP contentPane = new BeveledKVP(rowWidth, rowHeight, color);
        Pane centeringPane = JavaFXUtils.createHorizontallyCenteringPane(contentPane, width, height, rowWidth);
        contentPane.setSpacing(2);
        container.getChildren().add(centeringPane);

        mStatisticsPanelMap.put(name, contentPane);
//        trackingMap.put(name, pair);

        return contentPane;
    }

    public void gameUpdate(GameModel gameModel) {
        // Check that the current entities state will update the ui
        gameModel.updateIsGreaterStatisticsPanelOpen(isVisible());

        int entityHash = gameModel.getActiveEntityStatisticsComponentHash();
//        String entityID = gameModel.getActiveUnitID();
        String entityID = gameModel.getSelectedEntityID();

        if (entityHash == mCurrentEntityHash && mCurrentEntityID == entityID) { return; }
        mCurrentEntityHash = entityHash;
        mCurrentEntityID = entityID;

        if (entityID == null) { return; }

        mLogger.info("Started updating statistics information panel");
        mUnitPreviewPanel.gameUpdate(gameModel, entityID);
        setupStatisticsInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating statistics information panel");
    }

    private void setupStatisticsInformationPanel(GameModel gameModel, String entityID) {
        clear();

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);

        JSONObject attributes = response.getJSONObject("attributes");
        for (String key : stats) {
            JSONObject attribute = attributes.getJSONObject(key);
            if (attribute == null) {
                BeveledKVP row = getOrCreateKeyValueRow(
                        mStatisticsPanel,
                        UUID.randomUUID().toString(),
                        mStatisticsPanelRowWidth,
                        mStatisticsPanelRowHeight
                );
            } else {
                int current = attribute.getIntValue("current");
                int base = attribute.getIntValue("base");
                int modified = attribute.getIntValue("modified");
                BeveledKVP row = getOrCreateKeyValueRow(
                        mStatisticsPanel,
                        key,
                        mStatisticsPanelRowWidth,
                        mStatisticsPanelRowHeight
                );


                row.getLeft().setText(StringUtils.convertSnakeCaseToCapitalized(key));
                String txt = base + " ( " + (modified > 0 ? "+" : modified < 0 ? "-" : "") +  modified + " )";
                if (RESOURCES.contains(key)) {
                    txt = current + " / " + txt;
                }
                row.getRight().setText(txt);


                Tooltip tooltip = new Tooltip("name");
//                tooltip.setFont(Font.font("Verdana", FontPosture.REGULAR, 20));
//                tooltip.setOpacity(1);
                tooltip.setStyle("-fx-background-color: yellow; -fx-text-fill: black; -fx-font-size: 20;");
                row.getRight().setTooltip(tooltip);
                tooltip.setOnShowing(e -> {
                    System.out.println("rorok");
                });
            }
        }
    }

    private void clear() {
        mStatisticsPanelMap.clear();
        mStatisticsPanel.getChildren().clear();
//        mTagsPanel.getChildren().clear();
//        mTagsPanelMap.clear();
    }

    public void gameUpdate(GameModel gameModel, MovementPanel movementPanel) {
        // Check that the current entities state will update the ui
        gameModel.updateIsGreaterStatisticsPanelOpen(isVisible());

        String entityID = movementPanel.getCurrentEntityID();
        int entityHash = gameModel.getEntityStatisticsComponentHashCode(entityID);

        if (entityHash == mCurrentEntityHash && mCurrentEntityID == entityID) { return; }
        mCurrentEntityHash = entityHash;
        mCurrentEntityID = entityID;

        if (entityID == null) { return; }

        mLogger.info("Started updating statistics information panel");
        mUnitPreviewPanel.gameUpdate(gameModel, entityID);
        setupStatisticsInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating statistics information panel");
    }

    public void gameUpdate(GameModel gameModel, StatisticsPanel statisticsPanel) {
        // Check that the current entities state will update the ui
        gameModel.updateIsGreaterStatisticsPanelOpen(isVisible());

        String entityID = statisticsPanel.getCurrentEntityID();
        int entityHash = gameModel.getEntityStatisticsComponentHashCode(entityID);

        if (entityHash == mCurrentEntityHash && mCurrentEntityID == entityID) { return; }
        mCurrentEntityHash = entityHash;
        mCurrentEntityID = entityID;

        if (entityID == null) { return; }

        mLogger.info("Started updating statistics information panel");
        mUnitPreviewPanel.gameUpdate(gameModel, entityID);
        setupStatisticsInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating statistics information panel");
    }
}
