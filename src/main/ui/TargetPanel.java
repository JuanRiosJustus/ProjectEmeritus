package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.game.stores.ColorPalette;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledProgressBar;
import main.ui.foundation.GraphicButton;
import main.ui.game.GamePanel;
import main.utils.RandomUtils;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TargetPanel extends GamePanel {

    private VBox mContentPanel = null;
    private VBox mResourcePanel = null;
    private HBox mTagsPanel = new HBox();
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

    public TargetPanel(int width, int height, Color color) {
        super(width, height);

        mColor = color;

        width = (int) (width * .999);
        height = (int) (height * .999);
        int genericRowWidth = width;
        int genericRowHeight = (int) (height * .2);
        int fortyPercentWidth = (int) (width * .4);
        int sixtyPercentHeight = (int) (height * .6);


        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        int row1fontHeight = (int) (genericRowHeight * .8);

        mLevelLabel = new BeveledButton((int) (genericRowWidth * .15), genericRowHeight, "Lv. 130", color);
        mLevelLabel.setFont(getFontForHeight(row1fontHeight));

        mTypeLabel = new BeveledButton((int) (genericRowWidth * .25), genericRowHeight, "Water", color);
        mTypeLabel.setFont(getFontForHeight(row1fontHeight));

        mNameLabel = new BeveledButton((int) (genericRowWidth * .6), genericRowHeight, "Heominhon", color);
        mNameLabel.setTextAlignment(Pos.CENTER_LEFT);
        mNameLabel.setFont(getFontForHeight(row1fontHeight));

        HBox row1 = new HBox(mLevelLabel, mTypeLabel, mNameLabel);
        row1.setFillHeight(true);


        //CREATE RESOURCE AND IMAGE ROW
        mImageDisplay = new GraphicButton(fortyPercentWidth, sixtyPercentHeight, color);
        mResourcePanelProgressBars = new LinkedHashMap<>();
        mResourcePanel = new VBox();

        int resourceScrollPaneWidth = width - fortyPercentWidth;
        int resourceScrollPaneHeight = sixtyPercentHeight;
        ScrollPane resourceScrollPane = new ScrollPane(mResourcePanel);
//        resourceScrollPane.setFitToWidth(true);
        resourceScrollPane.setFitToHeight(true);
        resourceScrollPane.setPrefSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setMinSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setMaxSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        resourceScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        resourceScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        resourceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        resourceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        mResourceBarWidth = (int) (resourceScrollPaneWidth);
        mResourceBarHeight = resourceScrollPaneHeight / 3;
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
        tagScrollPane.setFitToHeight(true);
        tagScrollPane.setPrefSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
        tagScrollPane.setMinSize(tagsScrollPaneWidth, tagsScrollPaneHeight);
        tagScrollPane.setMaxSize(tagsScrollPaneWidth, tagsScrollPaneHeight);

        tagScrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        tagScrollPane.setPickOnBounds(false); // Allow clicks to pass through
        tagScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        tagScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        HBox row3 = new HBox(tagScrollPane);

        int tagWidth = (int) (tagsScrollPaneWidth * .3);
        int tagHeight = (int) (tagsScrollPaneHeight * 1);
//        row3.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        for (int i = 0; i < 5; i++) {
            mTagsPanel.getChildren().add(new BeveledButton(tagWidth, tagHeight, "Example", color));
        }


        mContentPanel.getChildren().addAll(
                row1,
                row2,
                row3
        );
        getChildren().add(mContentPanel);
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void gameUpdate(JSONObject response) {

        String unitID = response.getString("id");
        String nickname = response.optString("nickname");
        String unitName = response.optString("unit");
        int level = response.optInt("level");
        String type = response.optString("type");

        mNameLabel.setText(nickname + " (" + StringUtils.convertSnakeCaseToCapitalized(unitName) + ")");
        mLevelLabel.setText("Lv." + level);
        mTypeLabel.setText(type);

        ImageView iv = createAndCacheEntityIcon(unitID);
        iv.setFitWidth(mImageDisplay.getWidth() * .8);
        iv.setFitHeight(mImageDisplay.getHeight() * .8);
        mImageDisplay.setImageView(iv);


        Set<String> resources = Set.of("health", "mana", "stamina");
        Map<String, String> mapping = Map.of("health", "HP", "mana", "MP", "stamina", "SP");
        JSONObject attributes = response.getJSONObject("attributes");
        for (String key : resources) {
            JSONObject attribute = attributes.optJSONObject(key);
            if (attribute == null) { continue; }
            int current = attribute.optInt("current");
            int base = attribute.optInt("base");
            int modified = attribute.optInt("modified");

            BeveledProgressBar progressBar = mResourcePanelProgressBars.get(key); //getOrCreateResourceProgressBar(key);
            if (progressBar == null) {
                progressBar = JavaFXUtils.createResourceProgressBar(mResourceBarWidth, mResourceBarHeight, mColor);
                mResourcePanel.getChildren().add(progressBar);
            }

            int total = base + modified;
            progressBar.setProgress(current, total, current + "/" + total + " " + mapping.get(key));
        }

    }
}
