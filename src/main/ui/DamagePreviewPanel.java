package main.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.constants.Tuple;
import main.game.main.GameModel;
import main.game.stores.ColorPalette;
import main.game.systems.combat.AbilityDamageReport;
import main.game.systems.combat.CombatSystem;
import main.ui.foundation.BevelStyle;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledLabel;
import main.ui.foundation.GraphicButton;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class DamagePreviewPanel extends BevelStyle {
    private final HBox mContentPanel;
    private final VBox mTextRowsContainers;
    private GraphicButton mDisplay;
    private int mSCHash = -1;
    private String mCurrentID = null;
    private int mACHash = -1;
    private int mTextRowWidth = -1;
    private int mTextRowHeight = -1;
    private Map<String, Tuple<GridPane, BeveledLabel, BeveledLabel>> mTextRows = new LinkedHashMap<>();
    private Map<String, ImageView> mViewCache = new LinkedHashMap<>();
    private String mSelectedAbility = null;
    private int mDisplayWidth = 0;
    private int mDisplayHeight = 0;

    public DamagePreviewPanel(int x, int y, int width, int height, Color color) {
        super(x, y, width, height, color);

//        getChildren().add(new Button("totooto"));

        // 🔹 **Ensure Both Elements Have the Same Width**
        mDisplayWidth = height;
        mDisplayHeight = height;
        mDisplay = new GraphicButton(mDisplayWidth, mDisplayHeight, color);
        mDisplay.setFocusTraversable(false);
        mDisplay.setPrefWidth(mDisplayWidth); // Ensure full width
        mDisplay.setPrefHeight(mDisplayHeight);
//        mDisplay.setBackgroundColor(color);
//        mDisplay.getUnderlyingButton().setBackground(new Background(new BackgroundFill(ColorPalette.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));


        mTextRowsContainers = new VBox();
        mTextRowWidth = width - mDisplayWidth;
        mTextRowHeight = mDisplayHeight / 4;


        int resourceScrollPaneWidth = width - mDisplayHeight;
        int resourceScrollPaneHeight = height;
        ScrollPane scrollPane = new ScrollPane(mTextRowsContainers);
//        resourceScrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        scrollPane.setMinSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        scrollPane.setMaxSize(resourceScrollPaneWidth, resourceScrollPaneHeight);
        scrollPane.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        scrollPane.setPickOnBounds(false); // Allow clicks to pass through
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar
        HBox row2 = new HBox(mDisplay, scrollPane);

        // 🔹 **Scrollable Content Panel**
        mContentPanel = new HBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillHeight(true);
        mContentPanel.setSpacing(0);
        mContentPanel.getChildren().addAll(row2); // Now using the wrapper

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.color(1, 1, 1, 0.6));
        innerShadow.setRadius(6);
        mContentPanel.setEffect(innerShadow);

        // 🔹 **Ensure Background is Set**
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));


        BeveledButton container = new BeveledButton(width, height, "", color);
        container.getUnderlyingButton().setGraphic(mContentPanel);
        getChildren().add(container);

//        getChildren().add(mContentPanel);
        JavaFXUtils.setCachingHints(this);
//        setEffect(JavaFXUtils.createBasicDropShadowFixed(width, height));
    }

    public void gameUpdateDamageFrom(GameModel gameModel, AbilityPanel abilityPanel) {
        // If an ability panel is not visible, this damage from the panel should not
        boolean isAbilityPanelOpen = abilityPanel.isVisible();
        if (!isAbilityPanelOpen) { hide(); return; }


        String entityID = gameModel.getActiveEntityID();
        int newSCHash = gameModel.getActiveEntityStatisticsComponentHash();
        int newACHash = gameModel.getActiveEntityAbilityComponentHash();

        if (newSCHash == mSCHash && entityID == mCurrentID && newACHash == mACHash) { return; }
        mSCHash = newSCHash;
        mCurrentID = entityID;
        mACHash = newACHash;

        ImageView imageView = getOrCreateImage(entityID);
        mDisplay.setImageView(imageView);

        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);
        String selectedAbility = response.getString("selected_ability");
        mSelectedAbility = selectedAbility;
        // Get the raw damage map for the ability based on the user
        if (selectedAbility == null) { hide(); return; }
        clear();

        Tuple<GridPane, BeveledLabel, BeveledLabel> row = getOrCreateRow("Unit");
        String unit = response.getString("unit");
        String fancyUnit = StringUtils.convertSnakeCaseToCapitalized(unit);
        String nickname = response.getString("nickname");
        String fancyNickname = StringUtils.convertSnakeCaseToCapitalized(nickname);
        row.getSecond().setVisible(false);
        row.getThird().setText(fancyNickname + " (" + fancyUnit + ")");

//        AbilityDamageReport adr = new AbilityDamageReport(entityID, selectedAbility, null);
//        Map<String, Float> damageMap = adr.getRawDamageMap();
//        for (Map.Entry<String, Float> entry : damageMap.entrySet()) {
//            String attribute = entry.getKey();
//            int damage = entry.getValue().intValue();
//            row = getOrCreateRow(attribute);
//            row.getSecond().setVisible(false);
//            row.getThird().setText(damage + " " + attribute + " damage");
//        }
//
//        Map<String, Float> tagsToUserMap = adr.getTagsToUserMap();
//        for (Map.Entry<String, Float> entry : tagsToUserMap.entrySet()) {
//            String tag = entry.getKey();
//            String fancyTag = StringUtils.convertSnakeCaseToCapitalized(tag);
//            float chance = entry.getValue();
//            row = getOrCreateRow(tag + "_tag");
//            row.getSecond().setVisible(false);
//            row.getThird().setText(fancyTag + " (" + StringUtils.floatToPercentage(chance) + ")");
//        }

        show();
    }

    public void gameUpdateDamageToPanel(GameModel gameModel, DamagePreviewPanel damageFromPanel) {
        // If damage from panel is not visible, this damage to panel should not
        boolean isDamageFromPanelOpen = damageFromPanel.isVisible();
        if (!isDamageFromPanelOpen) { hide(); return; }

        // If no ability is selected, dont show either
        String ability = damageFromPanel.mSelectedAbility;
        if (ability == null) { hide(); return; }

        String activeEntityID = gameModel.getActiveEntityID();
        String targetEntityID = gameModel.getTargetedUnitFromSpecificEntityID(activeEntityID);
        if (targetEntityID == null) { hide(); return; }

        int newSCHash = gameModel.getSpecificEntityStatisticsComponentHash(targetEntityID);
        int newACHash = gameModel.getSpecificEntityAbilityComponentHash(targetEntityID);

        if (newSCHash == mSCHash && targetEntityID == mCurrentID && newACHash == mACHash) { return; }
        mSCHash = newSCHash;
        mCurrentID = targetEntityID;
        mACHash = newACHash;

        if (targetEntityID.isEmpty()) { hide(); return; }
        ImageView imageView = getOrCreateImage(targetEntityID);
        mDisplay.setImageView(imageView);


        JSONObject request = new JSONObject();
        request.put("id", targetEntityID);
        JSONObject response = gameModel.getStatisticsForEntity(request);
        clear();

        Tuple<GridPane, BeveledLabel, BeveledLabel> row = getOrCreateRow("Unit");
        String unit = response.getString("unit");
        String fancyUnit = StringUtils.convertSnakeCaseToCapitalized(unit);
        String nickname = response.getString("nickname");
        String fancyNickname = StringUtils.convertSnakeCaseToCapitalized(nickname);
        row.getSecond().setVisible(false);
        row.getThird().setText(fancyNickname + " (" + fancyUnit + ")");

        AbilityDamageReport adr = new AbilityDamageReport(activeEntityID, ability, targetEntityID);
        Map<String, Float> finalDamageMap = adr.getFinalDamageMap();
        Map<String, Float> upperDamageMap = adr.getUpperDamageMap();
        Map<String, Float> lowerDamageMap = adr.getLowerDamageMap();
        Map<String, String> acronyms = Map.of("health", "HP", "mana", "MP", "Stamina", "SP");

        for (Map.Entry<String, Float> entry : finalDamageMap.entrySet()) {
            String key = entry.getKey();


            int rawLowerDamage = lowerDamageMap.get(key).intValue();
            int rawUpperDamage = upperDamageMap.get(key).intValue();
            int rawAverageDamage = (rawLowerDamage + rawUpperDamage) / 2;

            int lowerDamage = Math.min(Math.abs(rawLowerDamage), Math.abs(rawUpperDamage));
            int upperDamage = Math.max(Math.abs(rawLowerDamage), Math.abs(rawUpperDamage));
            int averageDamage = (lowerDamage + upperDamage) / 2;

            Color goodOrBad = Color.ORANGE;
            String sign = "";
            if (rawAverageDamage > 0) {
                goodOrBad = Color.LIGHTCORAL;
                sign = "-";
            } else if (rawAverageDamage < 0) {
                goodOrBad = Color.LIGHTGREEN;
                sign = "+";
            }


            row = getOrCreateRow(key);
            row.getSecond().setVisible(false);
            row.getThird().setTextColor(goodOrBad);
            row.getThird().setText(sign + " ( " +
                    lowerDamage + " , " +
                    upperDamage + " ) " + StringUtils.convertSnakeCaseToCapitalized(key) );
        }

        Map<String, Float> tagsToTargetMap = adr.getTagsToTargetMap();
        for (Map.Entry<String, Float> entry : tagsToTargetMap.entrySet()) {
            String tag = entry.getKey();
            String fancyTag = StringUtils.convertSnakeCaseToCapitalized(tag);
            float chance = entry.getValue();
            row = getOrCreateRow(tag + "_tag");
            row.getSecond().setVisible(false);
            row.getThird().setText(fancyTag + " (" + StringUtils.floatToPercentage(chance) + ")");
        }
        show();
    }

    private ImageView getOrCreateImage(String entityID) {
        ImageView view = mViewCache.get(entityID);
        if (view != null) { return view; }

        view = createAndCacheEntityIcon(entityID);

        mViewCache.put(entityID, view);

        view.setFitWidth(mDisplayWidth);
        view.setFitHeight(mDisplayHeight);
        view.setPreserveRatio(true);

        return view;
    }

    private Tuple<GridPane, BeveledLabel, BeveledLabel> getOrCreateRow(String name) {
        Tuple<GridPane, BeveledLabel, BeveledLabel> row = mTextRows.get(name);
        if (row != null) { return row; }
        row = JavaFXUtils.createBeveledLabelRow(mTextRowWidth, mTextRowHeight);
        mTextRowsContainers.getChildren().add(row.getFirst());
        mTextRows.put(name, row);
        return row;
    }

    private void hide() { if (isVisible()) { setVisible(false); } }
    private void show() { if (!isVisible()) { setVisible(true); } }
    private void clear() {
        mTextRows.clear();
        mTextRowsContainers.getChildren().clear();
    }
}
