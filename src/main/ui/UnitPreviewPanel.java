package main.ui;

import com.alibaba.fastjson2.JSONArray;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import main.constants.JavaFXUtils;
import main.game.main.GameModel;
import main.game.stores.FontPool;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledProgressBar;
import main.ui.foundation.GraphicButton;
import main.ui.game.GamePanel;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnitPreviewPanel extends GamePanel {
    private static final Map<String, String> RESOURCE_MAPPING = Map.of(
            "health", "HP", "mana", "MP", "stamina", "SP");

    private VBox mContentPanel = null;
    private VBox mResourceScrollPaneContent = null;
    private HBox mTagsScrollPaneContent = new HBox();
    private BeveledButton mNameLabel;
    private BeveledButton mLevelLabel;
    private BeveledButton mTypeLabel;
    private GraphicButton mImageDisplay;

    private int mTagsPanelButtonHeights;
    private int mTagsPanelButtonWidths;
    private Map<String, BeveledProgressBar> mResourcePanelProgressBars = null;
    private Map<String, Object> mTagsPanelMap = null;
    private int mResourceBarWidth;
    private int mResourceBarHeight;
    private Color mColor;

    private int mFirstRowHeight = 0;
    private int mFirstRowWidth = 0;
    private HBox mFirstRow = new HBox();
    private HBox mSecondRows = new HBox();
    private HBox mThirdRow = new HBox();
    public UnitPreviewPanel(int width, int height, Color color) {
        super(width, height);

        mColor = color;

        width = (int) (width * .99);
        height = (int) (height * .99);
        int genericRowWidth = (int) (width);
        int genericRowHeight = (int) (height * .2);
        int fortyPercentWidth = (int) (width * .4);
        int sixtyPercentHeight = (int) (height * .6);


        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);



        Color firstRowItemColors = Color.STEELBLUE;
        mFirstRowWidth = width;
        mFirstRowHeight = (int) (height * .2);

        mLevelLabel = new BeveledButton((int) (mFirstRowWidth * .2), mFirstRowHeight);
        mLevelLabel.setBackground(firstRowItemColors);

        mTypeLabel = new BeveledButton((int) (mFirstRowWidth * .25), mFirstRowHeight);
        mTypeLabel.setBackground(firstRowItemColors);

        mNameLabel = new BeveledButton((int) (mFirstRowWidth * .8), mFirstRowHeight);
        mNameLabel.setBackground(firstRowItemColors);
        mNameLabel.setTextAlignment(Pos.CENTER_LEFT);

        mFirstRow = new HBox();
        mFirstRow.getChildren().addAll(mLevelLabel, mNameLabel);
        mFirstRow.setPadding(new Insets(2, 2, 2, 2));
        mFirstRow.setFillHeight(true);
        mFirstRow.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create the panel to host the resource bars
        mResourcePanelProgressBars = new LinkedHashMap<>();
        mResourceScrollPaneContent = new VBox();
        int resourceScrollPaneWidth = width - fortyPercentWidth;
        int resourceScrollPaneHeight = (int) (height * .6f);
        ScrollPane resourceScrollPane = new ScrollPane(mResourceScrollPaneContent);
        resourceScrollPane.setFitToHeight(true);
        resourceScrollPane.setPrefSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setMinSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setMaxSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        resourceScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        resourceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        resourceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        mResourceBarWidth = (int) (resourceScrollPaneWidth * .98);
        mResourceBarHeight = (int) (resourceScrollPaneHeight * .32);

        mImageDisplay = new GraphicButton(fortyPercentWidth, sixtyPercentHeight, color);

        mSecondRows = new HBox(resourceScrollPane, mImageDisplay);

        // Tags panel
        mTagsPanelMap = new LinkedHashMap<>();
        mTagsPanelButtonHeights = (int) (height * .2);
        mTagsPanelButtonWidths = width / 6;

        mTagsScrollPaneContent = new HBox();
        mTagsScrollPaneContent.setPadding(new Insets(2, 2, 2, 2));

        int tagsScrollPaneWidth = genericRowWidth;
        int tagsScrollPaneHeight = genericRowHeight;
        ScrollPane tagScrollPane = new ScrollPane(mTagsScrollPaneContent);
        tagScrollPane.setFitToWidth(true);
        tagScrollPane.setFitToHeight(true);
        tagScrollPane.setPrefSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
        tagScrollPane.setMinSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
        tagScrollPane.setMaxSize(tagsScrollPaneWidth, tagsScrollPaneHeight);

        tagScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        tagScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        tagScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        tagScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        mThirdRow = new HBox(tagScrollPane);

        int tagWidth = (int) (tagsScrollPaneWidth * .3);
        int tagHeight = (int) (tagsScrollPaneHeight * 1);
//        row3.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        for (int i = 0; i < 5; i++) {

//            int buttonWidth = mTagsPanelButtonWidths;
//            int buttonHeight = (int) (mTagsPanelButtonHeights * .8);
//            BeveledButton bb = new BeveledButton(buttonWidth, buttonHeight);
//            bb.setFitText("Test");
//
//            Tooltip tooltip = new Tooltip("name");
//            tooltip.setFont(Font.font("Verdana", FontPosture.REGULAR, 20));
//            bb.setToolTip(tooltip);

//            mTagsScrollPaneContent.getChildren().add(new BeveledButton(tagWidth, tagHeight, "Example", color));
        }


        mContentPanel.getChildren().addAll(
                mFirstRow,
                mSecondRows,
                mThirdRow
        );
        getChildren().add(mContentPanel);
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }


    public void gameUpdate(GameModel gameModel, String entityID) {

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);

        clear();

        String unitID = response.getString("id");
        String nickname = response.getString("nickname");
//        String unitName = response.getString("unit");
        int level = response.getIntValue("level");
        String type = response.getString("type");

        double multiplier = .45;
        mNameLabel.setText(nickname);
        mLevelLabel.setText("Lv " + level);
        mTypeLabel.setText(type);

        ImageView iv = createAndCacheEntityIcon(unitID);
        iv.setFitWidth(mImageDisplay.getWidth() * .8);
        iv.setFitHeight(mImageDisplay.getHeight() * .8);
        mImageDisplay.setImageView(iv);


        List<String> resources = List.of("health", "mana", "stamina");
        JSONObject attributes = response.getJSONObject("attributes");
        for (String key : resources) {
            JSONObject statistic = attributes.getJSONObject(key);
            addOrUpdateProgressBar(key, statistic);
        }


        JSONArray tags = new JSONArray();
        tags.add(new JSONObject().fluentPut("tag", "burn_infernal").fluentPut("name", "burnnner"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing").fluentPut("name", "icing"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_2").fluentPut("name", "Test_ting"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_4").fluentPut("name", "special_buff"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_5").fluentPut("name", "unknown"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_7").fluentPut("name", "koa_ken"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_7").fluentPut("name", "koa_ken"));
        tags.add(new JSONObject().fluentPut("tag", "burn_icing_8").fluentPut("name", "koa_ke"));

        addTags(tags);
    }

    private void addOrUpdateProgressBar(String key, JSONObject data) {
        int current = data.getIntValue("current");
        int base = data.getIntValue("base");
        int bonus = data.getIntValue("bonus");

        BeveledProgressBar progressBar = mResourcePanelProgressBars.get(key);
        if (progressBar == null) {
            progressBar = JavaFXUtils.createResourceProgressBar(mResourceBarWidth, mResourceBarHeight, mColor);
            Font font = FontPool.getInstance().getFontForHeight((int) (mResourceBarHeight * .8));

            progressBar.setFont(font);
            mResourceScrollPaneContent.getChildren().add(progressBar);
            mResourcePanelProgressBars.put(key, progressBar);
        }

        int total = base + bonus;
        progressBar.setProgress(current, total, current + "/" + total + " " + RESOURCE_MAPPING.get(key));
    }

    public void addTags(JSONArray tags) {
        for (int i = 0; i < tags.size(); i++) {
            JSONObject tag = tags.getJSONObject(i);
            addTag(tag);
        }
    }
    public void addTag(JSONObject tag) {
        String name = tag.getString("name");
        String fancyName = StringUtils.capitalizeFirstAndAfterUnderscores(name);

        int buttonWidth = mTagsPanelButtonWidths;
        int buttonHeight = (int) (mTagsPanelButtonHeights * .8);
        BeveledButton bb = new BeveledButton(buttonWidth, buttonHeight);
        bb.setFitText(fancyName);

        Tooltip tooltip = new Tooltip(name);
        tooltip.setFont(Font.font("Verdana", FontPosture.REGULAR, 20));
        bb.setTooltip(tooltip);

        mTagsScrollPaneContent.getChildren().add(bb);
    }

    private BeveledProgressBar getOrCreate(String key) {
        BeveledProgressBar progressBar = mResourcePanelProgressBars.get(key);
        if (progressBar != null) { return  progressBar; }
        progressBar = JavaFXUtils.createResourceProgressBar(mResourceBarWidth, mResourceBarHeight, mColor);
        Font font = FontPool.getInstance().getFontForHeight((int) (mResourceBarHeight * .8));
//        progressBar.setText(key, Color.WHITE, font);
        progressBar.setFont(font);
        mResourceScrollPaneContent.getChildren().add(progressBar);
        mResourcePanelProgressBars.put(key, progressBar);
        return progressBar;
    }

    private void clear() {
//        mResourcePanel.getChildren().clear();
//        mResourcePanelProgressBars.clear();
        mTagsScrollPaneContent.getChildren().clear();
    }
}
