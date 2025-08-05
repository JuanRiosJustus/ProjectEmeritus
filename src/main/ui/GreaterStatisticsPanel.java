package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Pair;
import main.game.main.GameModel;
import main.game.stores.FontPool;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
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
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private Map<String, BeveledProgressBar> mResourcePanelProgressBars = null;
    private Color mColor = null;
    private int mSelectedTilesChecksum = 0;
    private int mStateOfUnitChecksum = 0;
    private int mResourceBarWidth = 0;
    private int mResourceBarHeight = 0;
    private BeveledButton levelLabel = null;
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mStatisticsPanelMap = new LinkedHashMap<>();
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
            mStatisticsPanel.setDisable(true);
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


    public Pair<BeveledLabel, BeveledLabel> getOrCreateKeyValueRow(
            Map<String, Pair<BeveledLabel, BeveledLabel>> trackingMap, VBox container, String name, int width, int height) {
        Pair<BeveledLabel, BeveledLabel> newRow = trackingMap.get(name);
        if (newRow != null) {
            return newRow;
        }

        int rowWidth = (int) (width * .98);
        int rowHeight = height;

        Color color = mColor;

        // ✅ Create Beveled Labels
        BeveledLabel leftLabel = new BeveledLabel((int) (rowWidth * .666), rowHeight, "???L??", color);
        leftLabel.setAlignment(Pos.CENTER_LEFT);
        leftLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight));
        leftLabel.setExtrusionFactor(.08);

        BeveledLabel rightLabel = new BeveledLabel((int) (rowWidth * .333), rowHeight, "???R???", color);
        rightLabel.setAlignment(Pos.CENTER_RIGHT);
        rightLabel.setFont(FontPool.getInstance().getFontForHeight(rowHeight));
        rightLabel.setExtrusionFactor(.08);
//        rightLabel.setBorder((int) (rowWidth * .01), (int) (rowHeight * .01), mColor);

        HBox contentPane = new HBox(leftLabel, rightLabel);
        Pane centeringPane = JavaFXUtils.createHorizontallyCenteringPane(contentPane, width, height, rowWidth);
        container.getChildren().add(centeringPane);

        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
        trackingMap.put(name, pair);

        return pair;
    }

    public void gameUpdate(GameModel gameModel) {
        // Check that the current entities state will update the ui
        gameModel.updateIsGreaterStatisticsPanelOpen(isVisible());

        int entityHash = gameModel.getActiveEntityStatisticsComponentHash();;
        String entityID = gameModel.getActiveUnitID();

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
                Pair<BeveledLabel, BeveledLabel> row = getOrCreateKeyValueRow(
                        mStatisticsPanelMap,
                        mStatisticsPanel,
                        UUID.randomUUID().toString(),
                        mStatisticsPanelRowWidth,
                        mStatisticsPanelRowHeight
                );
//                row.getFirst().setText("");
//                row.getSecond().setText("");
            } else {
                int current = attribute.getIntValue("current");
                int base = attribute.getIntValue("base");
                int modified = attribute.getIntValue("modified");
                Pair<BeveledLabel, BeveledLabel> row = getOrCreateKeyValueRow(
                        mStatisticsPanelMap,
                        mStatisticsPanel,
                        key,
                        mStatisticsPanelRowWidth,
                        mStatisticsPanelRowHeight
                );


                row.getFirst().setText(StringUtils.convertSnakeCaseToCapitalized(key));
                String txt = base + " ( " + (modified > 0 ? "+" : modified < 0 ? "-" : "") +  modified + " )";
                if (RESOURCES.contains(key)) {
                    txt = current + " / " + txt;
                }
                row.getSecond().setText(txt);
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
