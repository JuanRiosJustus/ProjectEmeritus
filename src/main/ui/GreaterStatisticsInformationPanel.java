package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Pair;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
import main.ui.foundation.BeveledProgressBar;
import main.ui.foundation.GraphicButton;
import main.ui.game.GamePanel;
import main.constants.JavaFXUtils;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GreaterStatisticsInformationPanel extends GamePanel {

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
    private static final EmeritusLogger mLogger = EmeritusLogger.create(LesserStatisticsInformationPanel.class);
    private JSONObject mEphemeralResponseContainer = new JSONObject();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private VBox mResourcePanel = null;
    private Map<String, BeveledProgressBar> mResourcePanelProgressBars = null;
    private Color mColor = null;
    private int mSelectedTilesChecksum = 0;
    private int mStateOfUnitChecksum = 0;
    private int mResourceBarWidth = 0;
    private int mResourceBarHeight = 0;
    private Map<String, BeveledButton> mTagsPanelMap = null;
    private HBox mTagsPanel = null;
    private int mTagsPanelButtonWidths = 0;
    private int mTagsPanelButtonHeights = 0;
    private BeveledButton nameLabel = null;
    private BeveledButton typeLabel = null;
    private BeveledButton levelLabel = null;
    private Map<String, Pair<BeveledLabel, BeveledLabel>> mStatisticsPanelMap = new LinkedHashMap<>();
    private VBox mStatisticsPanel = null;
    private int mStatisticsPanelRowWidth = 0;
    private int mStatisticsPanelRowHeight = 0;
    private Map<String, Pair<BeveledLabel, BeveledLabel>> mEquipmentPanelMap = new LinkedHashMap<>();
    private VBox mEquipmentPanel = null;
    private GraphicButton mImageDisplay = null;

    public GreaterStatisticsInformationPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        int genericRowWidth = width;
        int genericRowHeight = (int) (height * .05);
        int fortyPercentWidth = (int) (width * .40);
        int sixtyPercentWidth = width - fortyPercentWidth;

//        int twoThirdsOfWidth =

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);


//
//        BeveledLabel beveledLabel = new BeveledLabel(genericRowWidth, genericRowHeight, "TEST LABEL", color);
        int row1fontHeight = (int) (genericRowHeight * .9);
        nameLabel = new BeveledButton((int) (genericRowWidth * .6), genericRowHeight, "Heominhon", color);
        nameLabel.setTextAlignment(Pos.CENTER_LEFT);
        nameLabel.setFont(getFontForHeight(row1fontHeight));

        levelLabel = new BeveledButton((int) (genericRowWidth * .2), genericRowHeight, "Lv. 130", color);
        levelLabel.setFont(getFontForHeight(row1fontHeight));

        typeLabel = new BeveledButton((int) (genericRowWidth * .2), genericRowHeight, "Water", color);
        typeLabel.setFont(getFontForHeight(row1fontHeight));

        HBox row1 = new HBox(levelLabel, typeLabel, nameLabel);
        row1.setFillHeight(true);



        //CREATE RESOURCE AND IMAGE ROW
        mImageDisplay = new GraphicButton(fortyPercentWidth, fortyPercentWidth, color);
        mResourcePanelProgressBars = new LinkedHashMap<>();
        mResourcePanel = new VBox();

        int resourceScrollPaneWidth = sixtyPercentWidth;
        int resourceScrollPaneHeight = (int) mImageDisplay.getPrefHeight();
        ScrollPane resourceScrollPane = new ScrollPane(mResourcePanel);
//        resourceScrollPane.setFitToWidth(true);
        resourceScrollPane.setFitToHeight(true);
        resourceScrollPane.setPrefSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
//        resourceScrollPane.setMinSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
//        resourceScrollPane.setMaxSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        resourceScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        resourceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        resourceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        mResourceBarWidth = resourceScrollPaneWidth;
        mResourceBarHeight = resourceScrollPaneHeight / 3;
//        getOrCreateResourceProgressBar("Health", mResourceBarWidth, mResourceBarHeight);
//        getOrCreateResourceProgressBar("Mana", mResourceBarWidth, mResourceBarHeight);
//        getOrCreateResourceProgressBar("Stamina", mResourceBarWidth, mResourceBarHeight);
//        getOrCreateResourceProgressBar("Experience", mResourceBarWidth, mResourceBarHeight);
//        getOrCreateResourceProgressBar("Shield", mResourceBarWidth, mResourceBarHeight);

        HBox row2 = new HBox(mImageDisplay, resourceScrollPane);




        // Tags panel
        mTagsPanelMap = new LinkedHashMap<>();
        mTagsPanelButtonHeights = genericRowHeight;
        mTagsPanelButtonWidths = genericRowWidth / 7;

        mTagsPanel = new HBox();

        int tagsScrollPaneWidth = genericRowWidth;
        int tagsScrollPaneHeight = genericRowHeight;
        ScrollPane tagScrollPane = new ScrollPane(mTagsPanel);
        tagScrollPane.setFitToWidth(true);
//        tagScrollPane.setFitToHeight(true);
        tagScrollPane.setPrefSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
//        tagScrollPane.setMinSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
//        tagScrollPane.setMaxSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
        tagScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        tagScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        tagScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        tagScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

//        getOrCreateTagButton("11", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("22", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("33", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("44", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("55", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("66", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
//        getOrCreateTagButton("77", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        HBox row3 = new HBox(tagScrollPane);



        // Create statistics panel
        // LABEL
        BeveledButton mStatisticsPanelLabel = new BeveledButton(genericRowWidth, genericRowHeight, "Statistics", mColor);
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

        mStatisticsPanelLabel.getUnderlyingButton()
                .setOnMousePressed(e -> {
                    mStatisticsPanel.setVisible(!mStatisticsPanel.isVisible());
                    mStatisticsPanel.autosize();
                    mStatisticsPanel.setDisable(true);
                });




        // Create Equipment panel
//        BeveledButton mEquipment = new BeveledButton(genericRowWidth, genericRowHeight, "Equipment", mColor);
//        HBox row6 = new HBox(mEquipment);
//
//        mEquipmentPanel = new VBox();
//        HBox row7 = new HBox(mEquipmentPanel);
//        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "1", genericRowWidth, genericRowHeight);
//        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "2", genericRowWidth, genericRowHeight);
//        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "3", genericRowWidth, genericRowHeight);
//        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "4", genericRowWidth, genericRowHeight);
//        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "5", genericRowWidth, genericRowHeight);
//

        mContentPanel.getChildren().addAll(
                row1,
                row2,
                row3,
                row4,
                row5
        );

//
        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
    }


    public Pair<BeveledLabel, BeveledLabel> getOrCreateKeyValueRow(
            Map<String, Pair<BeveledLabel, BeveledLabel>> trackingMap, VBox container, String name, int width, int height) {
        Pair<BeveledLabel, BeveledLabel> newRow = trackingMap.get(name);
        if (newRow != null) {
            return newRow;
        }

        int rowWidth = (int) (width * .95);
        int rowHeight = height;

        Color color = mColor;

        // ✅ Create Beveled Labels
        BeveledLabel leftLabel = new BeveledLabel((int) (rowWidth * .666), rowHeight, RandomUtils.createRandomName(3, 6), color);
        leftLabel.setAlignment(Pos.CENTER_LEFT);

        BeveledLabel rightLabel = new BeveledLabel((int) (rowWidth * .333), rowHeight, name, color);
        rightLabel.setAlignment(Pos.CENTER_RIGHT);

        HBox contentPane = new HBox(leftLabel, rightLabel);
        Pane centeringPane = JavaFXUtils.createHorizontallyCenteringPane(contentPane, width, height, rowWidth);
        container.getChildren().add(centeringPane);

        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
        trackingMap.put(name, pair);

        return pair;
    }

    public BeveledButton getOrCreateTagButton(String name) {
        return getOrCreateTagButton(name, mTagsPanelButtonWidths, mTagsPanelButtonWidths);
    }

    public BeveledButton getOrCreateTagButton(String name, int width, int height) {
        BeveledButton tagButton = mTagsPanelMap.get(name);
        if (tagButton != null) {
            return tagButton;
        }

        tagButton = new BeveledButton(width, height, name, ColorPalette.getRandomColor());
        mTagsPanelMap.put(name, tagButton);
        mTagsPanel.getChildren().add(tagButton);

        return tagButton;
    }

    public BeveledProgressBar getOrCreateResourceProgressBar(String name) {
        return getOrCreateResourceProgressBar(name, mResourceBarWidth, mResourceBarHeight);
    }

    public BeveledProgressBar getOrCreateResourceProgressBar(String name, int width, int height) {
        BeveledProgressBar progressBar = mResourcePanelProgressBars.get(name);
        if (progressBar != null) {
            return progressBar;
        }

        int progressBarWidth = width;
        int progressBarHeight = height;
        progressBar = new BeveledProgressBar(progressBarWidth, progressBarHeight, mColor, ColorPalette.getRandomColor());
        mResourcePanelProgressBars.put(name, progressBar);
        mResourcePanel.getChildren().add(progressBar);

        return progressBar;
    }

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

        clear();
        String nickname = response.optString("nickname");
        String unitName = response.optString("unit");
        int level = response.optInt("level");
        String type = response.optString("type");

        nameLabel.setText(nickname + " (" + StringUtils.convertSnakeCaseToCapitalized(unitName) + ")");
        levelLabel.setText("Lv." + level);
        typeLabel.setText(type);

        ImageView iv = createAndCacheEntityIcon(unitID);
        iv.setFitWidth(mImageDisplay.getWidth() * .8);
        iv.setFitHeight(mImageDisplay.getHeight() * .8);
        mImageDisplay.setImageView(iv);

        JSONArray tags = response.getJSONArray("tags");
        for (int index = 0; index < tags.length(); index++) {
            JSONObject tag = tags.getJSONObject(index);
            String name = tag.getString("name");
            int age = tag.getInt("age");
            int count = tag.getInt("count");
            BeveledButton bb = getOrCreateTagButton(
                    name.toUpperCase(Locale.ROOT).substring(0, 2), mTagsPanelButtonWidths, mTagsPanelButtonHeights
            );
//            Tooltip tt = new Tooltip();
            bb.getUnderlyingButton().setTooltip(new Tooltip(age + " turns old"));
        }

        Set<String> resources = Set.of("health", "mana", "stamina");
        Map<String, String> mapping = Map.of("health", "HP", "mana", "MP", "stamina", "SP");
        JSONObject attributes = response.getJSONObject("attributes");
        for (String key : resources) {
            JSONObject attribute = attributes.optJSONObject(key);
            if (attribute == null) { continue; }
            int current = attribute.optInt("current");
            int base = attribute.optInt("base");
            int modified = attribute.optInt("modified");
            BeveledProgressBar progressBar = getOrCreateResourceProgressBar(key);

            int total = base + modified;
            progressBar.setProgress(current, total, current + "/" + total + " " + mapping.get(key));
        }

        for (String key : stats) {
            JSONObject attribute = attributes.optJSONObject(key);
            if (attribute == null) {
                Pair<BeveledLabel, BeveledLabel> row = getOrCreateKeyValueRow(
                        mStatisticsPanelMap,
                        mStatisticsPanel,
                        UUID.randomUUID().toString(),
                        mStatisticsPanelRowWidth,
                        mStatisticsPanelRowHeight
                );
                row.getFirst().setText("");
                row.getSecond().setText("");
            } else {
                int current = attribute.optInt("current");
                int base = attribute.optInt("base");
                int modified = attribute.optInt("modified");
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
                    row.getSecond().setText(current + " / " + txt);
                } else {
                    row.getSecond().setText(txt);
                }
            }
        }

        mLogger.info("Finished updating Greater UI");
    }

    private void clear() {
        mStatisticsPanelMap.clear();
        mStatisticsPanel.getChildren().clear();
        mTagsPanel.getChildren().clear();
        mTagsPanelMap.clear();
    }

}
