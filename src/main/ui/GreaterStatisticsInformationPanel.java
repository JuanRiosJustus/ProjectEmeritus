package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.CheckSum;
import main.constants.Pair;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
import main.ui.foundation.BeveledProgressBar;
import main.ui.game.GamePanel;
import main.ui.game.JavaFxUtils;
import main.utils.RandomUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GreaterStatisticsInformationPanel extends GamePanel {

    private static final String[] stats = new String[]{
            "health",
            "mana",
            "stamina",
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
    private static final EmeritusLogger mLogger = EmeritusLogger.create(LesserStatisticsInformationPanel.class);
    private final CheckSum mCheckSum = new CheckSum();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledLabel, BeveledLabel>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private VBox mResourcePanel = null;
    private Map<String, BeveledProgressBar> mResourcePanelProgressBars = null;
    private Color mColor = null;
    private int mResourceBarWidth = 0;
    private int mResourceBarHeight = 0;
    private Map<String, BeveledButton> mTagsPanelButtons = null;
    private HBox mTagsPanel = null;
    private int mTagsPanelButtonWidths = 0;
    private int mTagsPanelButtonHeights = 0;
    private BeveledButton nameLabel = null;
    private BeveledButton typeLabel = null;
    private BeveledButton levelLabel = null;
    private Map<String, Pair<BeveledLabel, BeveledLabel>> mStatisticsPanelMap = new LinkedHashMap<>();
    private VBox mStatisticsPanel = null;
    private Map<String, Pair<BeveledLabel, BeveledLabel>> mEquipmentPanelMap = new LinkedHashMap<>();
    private VBox mEquipmentPanel = null;
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
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
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
        BeveledButton imageContainer = new BeveledButton(fortyPercentWidth, fortyPercentWidth, "", color);
        mResourcePanelProgressBars = new LinkedHashMap<>();
        mResourcePanel = new VBox();

        int resourceScrollPaneWidth = sixtyPercentWidth;
        int resourceScrollPaneHeight = (int) imageContainer.getPrefHeight();
        ScrollPane resourceScrollPane = new ScrollPane(mResourcePanel);
//        resourceScrollPane.setFitToWidth(true);
        resourceScrollPane.setFitToHeight(true);
        resourceScrollPane.setPrefSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
//        resourceScrollPane.setMinSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
//        resourceScrollPane.setMaxSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        resourceScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        resourceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        resourceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        mResourceBarWidth = resourceScrollPaneWidth;
        mResourceBarHeight = resourceScrollPaneHeight / 4;
        getOrCreateResourceProgressBar("Health", mResourceBarWidth, mResourceBarHeight);
        getOrCreateResourceProgressBar("Mana", mResourceBarWidth, mResourceBarHeight);
        getOrCreateResourceProgressBar("Stamina", mResourceBarWidth, mResourceBarHeight);
        getOrCreateResourceProgressBar("Experience", mResourceBarWidth, mResourceBarHeight);
        getOrCreateResourceProgressBar("Shield", mResourceBarWidth, mResourceBarHeight);

        HBox row2 = new HBox(imageContainer, resourceScrollPane);




        // Tags panel
        mTagsPanelButtons = new LinkedHashMap<>();
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
        tagScrollPane.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        tagScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        tagScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        tagScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        getOrCreateTagButton("11", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("22", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("33", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("44", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("55", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("66", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        getOrCreateTagButton("77", mTagsPanelButtonWidths, mTagsPanelButtonHeights);
        HBox row3 = new HBox(tagScrollPane);



        // Create statistics panel
        // LABEL
        BeveledButton mStatisticsPanelLabel = new BeveledButton(genericRowWidth, genericRowHeight, "Statistics", mColor);
        HBox row4 = new HBox(mStatisticsPanelLabel);

        mStatisticsPanel = new VBox();
        HBox row5 = new HBox(mStatisticsPanel);
        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"1", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"2", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"3", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"4", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mStatisticsPanelMap, mStatisticsPanel,"5", genericRowWidth, genericRowHeight);

        mStatisticsPanelLabel.getUnderlyingButton()
                .setOnMousePressed(e -> {
                    mStatisticsPanel.setVisible(!mStatisticsPanel.isVisible());
                    mStatisticsPanel.autosize();
                    mStatisticsPanel.setDisable(true);
                });




        // Create Equipment panel
        BeveledButton mEquipment = new BeveledButton(genericRowWidth, genericRowHeight, "Equipment", mColor);
        HBox row6 = new HBox(mEquipment);

        mEquipmentPanel = new VBox();
        HBox row7 = new HBox(mEquipmentPanel);
        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "1", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "2", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "3", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "4", genericRowWidth, genericRowHeight);
        getOrCreateKeyValueRow(mEquipmentPanelMap, mEquipmentPanel, "5", genericRowWidth, genericRowHeight);


        mContentPanel.getChildren().addAll(
                row1,
                row2,
                row3,
                row4,
                row5,
                row6,
                row7
        );

//
        getChildren().add(mContentPanel);
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
        Pane centeringPane = JavaFxUtils.createHorizontallyCenteringPane(contentPane, width, height, rowWidth);
        container.getChildren().add(centeringPane);

        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
        trackingMap.put(name, pair);

        return pair;
    }

    public BeveledButton getOrCreateTagButton(String name) {
        return getOrCreateTagButton(name, mTagsPanelButtonWidths, mTagsPanelButtonWidths);
    }

    public BeveledButton getOrCreateTagButton(String name, int width, int height) {
        BeveledButton tagButton = mTagsPanelButtons.get(name);
        if (tagButton != null) {
            return tagButton;
        }

        tagButton = new BeveledButton(width, height, name, ColorPalette.getRandomColor());
        mTagsPanelButtons.put(name, tagButton);
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


//    public Pair<BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
//        Pair<BeveledLabel, BeveledLabel> newRow = mRows.get(name);
//        if (newRow != null) {
//            return newRow;
//        }
//
//        int rowWidth = 10; //(int) (mButtonWidth * .95);
//        int rowHeight = 6; //mButtonHeight;
//
//        // Create GridPane instead of HBox
//        GridPane gridPane = new GridPane();
//        gridPane.setPrefSize(rowWidth, rowHeight);
//        gridPane.setMinSize(rowWidth, rowHeight);
//        gridPane.setMaxSize(rowWidth, rowHeight);
//
//        Color color = Color.BLUE;
//
//        BeveledLabel leftLabel = new BeveledLabel(rowWidth / 2, rowHeight, name, color);
//        leftLabel.setAlignment(Pos.CENTER_LEFT);
//        leftLabel.setFont(getFontForHeight((int) (rowHeight * .8)));
//
//        BeveledLabel rightLabel = new BeveledLabel(rowWidth / 2, rowHeight, RandomUtils.createRandomName(3, 6), color);
//        rightLabel.setAlignment(Pos.CENTER_RIGHT);
//        rightLabel.setFont(getFontForHeight((int) (rowHeight * .8)));
//
//        // Add constraints to make sure columns resize properly
//        ColumnConstraints leftColumn = new ColumnConstraints();
//        leftColumn.setHgrow(Priority.ALWAYS); // Allows expansion
//        leftColumn.setPercentWidth(50); // Ensures left column takes 50% width
//        leftColumn.setHalignment(HPos.LEFT);
//
//        ColumnConstraints rightColumn = new ColumnConstraints();
//        rightColumn.setHgrow(Priority.ALWAYS);
//        rightColumn.setPercentWidth(50);
//        rightColumn.setHalignment(HPos.RIGHT);
//
//        gridPane.getColumnConstraints().addAll(leftColumn, rightColumn);
//
//        // Add labels to the grid
//        gridPane.add(leftLabel, 0, 0); // Left label in first column
//        gridPane.add(rightLabel, 1, 0); // Right label in second column
//
//        // Add the row to the content panel
//        mContentPanel.getChildren().add(gridPane);
//        mContentPanel.setAlignment(Pos.CENTER);
//
//        Pair<BeveledLabel, BeveledLabel> pair = new Pair<>(leftLabel, rightLabel);
//        mRows.put(name, pair);
//
//        return pair;
//    }
    public void gameUpdate(GameController gameController) {
        JSONObject response = gameController.getCurrentTurnsEntityAndStatisticsCheckSum();
        String id = response.optString("id");
        String checksum = response.optString("checksum");
        System.out.println("ID " + id);
        if (!mCheckSum.setDefault(id, checksum)) { return; }

//        JSONObject response = gameController.getSelectedUnitStatisticsHashState();
//        int hash = response.optInt("hash", 0);
//        if (!mSimpleCheckSum.isUpdated("hash", hash)|| hash == 0) {
//            return;
//        }
//
        response = gameController.getUnitAtSelectedTilesForStandardUnitInfoPanel();
        if (response.isEmpty()) {
            return;
        }
        System.out.println("ttppp");

        String nickname = response.optString("nickname");
        String unitName = response.optString("unit");
        int level = response.optInt("level");
        String type = response.optString("type");

        nameLabel.setText(nickname + " (" + unitName + ")");
        levelLabel.setText("Lv," + level);
        typeLabel.setText(type);
        mLogger.info("Finished updating Greater UI");
    }

    private void clear() {
        mContentPanel.getChildren().clear();
        mRows.clear();
    }

}
