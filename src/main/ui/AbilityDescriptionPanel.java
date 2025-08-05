package main.ui;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.game.main.GameModel;
import main.logging.EmeritusLogger;
import main.ui.foundation.BevelStyle;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledKeyValue;
import main.ui.foundation.BeveledLabel;
import main.ui.game.GamePanel;
import main.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbilityDescriptionPanel extends GamePanel {
    private EmeritusLogger mLogger = EmeritusLogger.create(AbilityDescriptionPanel.class);
    private Color mColor;
    private VBox mContentPanel;
    private int mCurrentStatsHash = -1;
    private int mCurrentAbilityHash = -1;
    private String mCurrentID = null;
    protected HBox mAbilityTitleAndHidePanel = new HBox();
    protected BeveledButton mAbilityTitleButton = null;
    protected BeveledButton mAbilityHideButton = null;
    protected TextFlow mAbilityDescriptionArea = new TextFlow();
    protected ScrollPane mAbilityDescriptionScrollPane = new ScrollPane();
    protected VBox mKeyAbilityPoints = new VBox();
    protected Font mBannerFont = null;
    protected Font mDescriptionFont = null;
    protected Font mImportantStuffFont = null;
    protected int mKeyAbilityPointsHeight;
    protected int mKeyAbilityPointsWidth;
    protected int mKeyAbilityPointHeight;
    protected int mKeyAbilityPointWidth;

    public AbilityDescriptionPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height);

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mColor = color;

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        int bannerWidth = (int) (width * .98);
        int bannerHeight = (int) (height * .15);
        mAbilityTitleAndHidePanel.setMinSize(bannerWidth, bannerHeight);
        mAbilityTitleAndHidePanel.setMaxSize(bannerWidth, bannerHeight);
        mAbilityTitleAndHidePanel.setPrefSize(bannerWidth, bannerHeight);

        mBannerFont = getFontForHeight((int) (bannerHeight * .8));
        mDescriptionFont = getFontForHeight((int) (height * .1));

        int titleWidth = (int) (bannerWidth * .85);
        int titleHeight = (bannerHeight);
        mAbilityTitleButton = new BeveledButton(titleWidth, titleHeight);
        mAbilityTitleButton.setBorder((int) (titleWidth * .025f), (int) (titleHeight * .05f), color);
        mAbilityTitleButton.setFont(mBannerFont);
        mAbilityTitleButton.setBackgroundColor(color);

        int hideWidth = (bannerWidth - titleWidth);
        int hideHeight = bannerHeight;
        mAbilityHideButton = new BeveledButton(hideWidth, hideHeight);
        mAbilityHideButton.setFont(mBannerFont);
        mAbilityHideButton.setBorder((int) (titleWidth * .025f), (int) (titleHeight * .05f), color);
        mAbilityHideButton.setText("V");
        mAbilityHideButton.setBackgroundColor(color);

        mAbilityTitleAndHidePanel.getChildren().addAll(mAbilityTitleButton, mAbilityHideButton);


        mKeyAbilityPointsWidth = bannerWidth;
        mKeyAbilityPointsHeight = bannerHeight;

        mKeyAbilityPoints = new VBox();
        mKeyAbilityPoints.setPrefSize(mKeyAbilityPointsWidth, mKeyAbilityPointsHeight);
        mKeyAbilityPoints.setMinSize(mKeyAbilityPointsWidth, mKeyAbilityPointsHeight);
        mKeyAbilityPoints.setMaxSize(mKeyAbilityPointsWidth, mKeyAbilityPointsHeight);
        mKeyAbilityPoints.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        mKeyAbilityPoints.setPadding(new Insets(5, 5, 5, 5));
        mKeyAbilityPoints.setAlignment(Pos.CENTER);

        mKeyAbilityPointHeight = mKeyAbilityPointsHeight / 2;
        mKeyAbilityPointWidth = mKeyAbilityPointsWidth / 2;

//        mImportantStuff.getChildren().add(new Text("Teest"));
//        mImportantStuff.getChildren().add(new Text("yoooo"));





        int descriptionWidth = bannerWidth;
        int descriptionHeight = (int) ((height * .98) - (bannerHeight + mKeyAbilityPointsHeight));
        mDescriptionFont = getFontForHeight((int) (descriptionHeight / 5));
        mAbilityDescriptionArea = new TextFlow();
        mAbilityDescriptionArea.setPrefSize(descriptionWidth, -1);
        mAbilityDescriptionArea.setMinSize(descriptionWidth, -1);
        mAbilityDescriptionArea.setMaxSize(descriptionWidth, -1);
        mAbilityDescriptionArea.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
//        mAbilityDescriptionArea.setBorder(BeveledButton.createBorder(4, 4, color));

//        mAbilityDescriptionArea.setBorder(EmeritusButton.createBorder((int) (descriptionWidth * .005f), (int) (descriptionHeight * .005), color));
        mAbilityDescriptionArea.setPadding(new Insets(5, 5, 5, 5));

        mAbilityDescriptionScrollPane = new ScrollPane();
        mAbilityDescriptionScrollPane.setPrefSize(descriptionWidth, descriptionHeight);
        mAbilityDescriptionScrollPane.setMinSize(descriptionWidth, descriptionHeight);
        mAbilityDescriptionScrollPane.setMaxSize(descriptionWidth, descriptionHeight);
        mAbilityDescriptionScrollPane.setContent(mAbilityDescriptionArea);
        mAbilityDescriptionScrollPane.setFitToHeight(false);
        mAbilityDescriptionScrollPane.setFitToWidth(false);
        mAbilityDescriptionScrollPane.setPannable(false);
        mAbilityDescriptionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        mAbilityDescriptionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar
        mAbilityDescriptionScrollPane.setBorder(BeveledButton.createBorder(4, 4, color));
        mAbilityDescriptionArea.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        mContentPanel.setPadding(new Insets(5, 5, 5, 5));
        mContentPanel.getChildren().addAll(
                mAbilityTitleAndHidePanel,
                mKeyAbilityPoints,
                mAbilityDescriptionScrollPane
        );

        mAbilityHideButton.setOnMousePressedV2(e -> {
            String currentText = mAbilityHideButton.getText();
            if (currentText.equalsIgnoreCase("V")) {
                mAbilityDescriptionScrollPane.setVisible(false);
                mAbilityHideButton.setText("<");

                setSize(mAbilityDescriptionScrollPane, 0, 0);
                setSize(this, width, -1);
            } else if (currentText.equalsIgnoreCase("<")) {
                mAbilityDescriptionScrollPane.setVisible(true);
                mAbilityHideButton.setText("V");

                setSize(this, width, height);
                setSize(mAbilityDescriptionScrollPane, descriptionWidth, descriptionHeight);
            }
        });

        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
    }

    public void gameUpdate(GameModel gameModel) {
        // Determine if the panel should be open
        // Check that the current entities state will update the ui
        String entityID = gameModel.getActiveUnitID();
        // If the stats have changed, update ui
        int statsHash = gameModel.getEntityStatisticsComponentHashCode(entityID);
        // If ability has changed, update ui
        int abilityHash = gameModel.getEntityAbilityComponentHashCode(entityID);


        if (statsHash == mCurrentStatsHash && abilityHash == mCurrentAbilityHash && entityID == mCurrentID) { return; }
        mCurrentStatsHash = statsHash;
        mCurrentAbilityHash = abilityHash;
        mCurrentID = entityID;


        mLogger.info("Started updating Greater Ability Information Panel");
        setupAbilityInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating Greater Ability Information Panel");
    }


    public void gameUpdate(GameModel gameModel, AbilitySelectionPanel abilitySelectionPanel) {
        gameModel.updateIsGreaterAbilityPanelOpen(isVisible());
        // Determine if the panel should be open
        // Check that the current entities state will update the ui
        String entityID = abilitySelectionPanel.getCurrentID();
        int statsHash = gameModel.getEntityStatisticsComponentHashCode(entityID);
        int abilityHash = gameModel.getEntityAbilityComponentHashCode(entityID);


        if (statsHash == mCurrentStatsHash && abilityHash == mCurrentAbilityHash && entityID == mCurrentID) { return; }
        mCurrentStatsHash = statsHash;
        mCurrentAbilityHash = abilityHash;
        mCurrentID = entityID;


        mLogger.info("Started updating Simple Ability Information Panel");
        setupAbilityInformationPanel(gameModel, entityID);
        mLogger.info("Finished updating Simple Ability Information Panel");
    }

    public void setupAbilityInformationPanel(GameModel gameModel, String entityID) {

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);

        String selectedAbility = response.getString("selected_ability");
        if (selectedAbility == null) { return; }

        String fancyAbilityText = StringUtils.convertSnakeCaseToCapitalized(selectedAbility);
        mAbilityTitleButton.setText(fancyAbilityText);

        JSONObject abilityData = gameModel.getAbilityData(selectedAbility);

        // Create some rows at the top before description
        mAbilityDescriptionArea.getChildren().clear();
        String[] details = new String[]{ "area", "range", "accuracy", "type" };
        Font font = mDescriptionFont;


        mKeyAbilityPoints.getChildren().clear();
        HBox hBox = new HBox();
        for (int i = 0; i < details.length; i += 1) {
            String key = details[i];
            String value = abilityData.getString(key);

            if (key.equalsIgnoreCase("accuracy")) {
                value = StringUtils.floatToPercentage(Float.parseFloat(value));
            }
            key = StringUtils.convertSnakeCaseToCapitalized(key);

            BeveledKeyValue kvp = new BeveledKeyValue(
                    mKeyAbilityPointWidth,
                    mKeyAbilityPointHeight,
                    key,
                    value,
                    mColor
            );

            boolean isNewRow = i % 2 == 0;
            if (isNewRow) { hBox = new HBox(); }
            hBox.getChildren().add(kvp);
            if (isNewRow) { mKeyAbilityPoints.getChildren().add(hBox); }
        }

        String description = abilityData.getString("description");
        JSONArray splits = gameModel.splitOnBracketedWords(description);
        for (int i = 0; i < splits.size(); i++) {
            String split = splits.getString(i);
            double width = JavaFXUtils.computeWidth(font, split);
            double height = JavaFXUtils.computeHeight(font, split.replace(System.lineSeparator(), ""));
            Text text = BevelStyle.createText(split, (int) width, (int) height, Color.WHITE, 0.1f);
            text.setFont(font);
            if (split.startsWith("[") && split.endsWith("]")) {
                text.setFill(Color.PURPLE);
            }

            mAbilityDescriptionArea.getChildren().add(text);
        }
    }
}
