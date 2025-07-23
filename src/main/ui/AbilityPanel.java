package main.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.HashSlingingSlasher;
import main.game.main.GameModel;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.game.EscapablePanel;
import main.constants.JavaFXUtils;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class AbilityPanel extends EscapablePanel {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilityPanel.class);
    private final HashSlingingSlasher mHashSlingingSlasher = new HashSlingingSlasher();
    private int mCurrentHash = -1;
    private String mCurrentID = "";
    private final VBox mContentPanel;
    private final Map<String, BeveledButton> mRows = new HashMap<>();
    private final int mButtonHeight;
    private final int mButtonWidth;

    public AbilityPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);
        mContentPanel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));


        mButtonHeight = getContentHeight() / visibleRows;
        mButtonWidth = getContentWidth();

        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        setMainContent(mContentPanel);
        getBanner().setText("Abilities");

        // ✅ **Add Buttons for Testing**
    }

    public BeveledButton getOrCreateRow(String name) {
        BeveledButton newRow = mRows.get(name);
        if (newRow != null) { return newRow; }

        HBox hBox = new HBox();
        hBox.setPrefSize(mButtonWidth, mButtonHeight);
        hBox.setMinSize(mButtonWidth, mButtonHeight);
        hBox.setMaxSize(mButtonWidth, mButtonHeight);
        hBox.setFillHeight(true);

        BeveledButton beveledButton = new BeveledButton(mButtonWidth, mButtonHeight, name, mColor);

        hBox.getChildren().addAll(beveledButton);
        mContentPanel.getChildren().add(hBox); // ✅ Add to the scrollable area
        mRows.put(name, beveledButton);

        return beveledButton;
    }


    public void clear() {
        mRows.clear();
        mContentPanel.getChildren().clear();
    }

    public void gameUpdate(GameModel gameModel) {
        gameModel.updateIsAbilityPanelOpen(isVisible());

        // Check that the current entities state will update the ui
        String currentEntityID = gameModel.getActiveEntityID();
        int newHash = gameModel.getSpecificEntityStatisticsComponentHash(currentEntityID);
        if (newHash == mCurrentHash && Objects.equals(currentEntityID, mCurrentID)) { return; }
        mCurrentHash = newHash;
        mCurrentID = currentEntityID;
        String hoveredTileID = gameModel.getHoveredTileID();

        if (currentEntityID == null) { return; }


        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentEntityID);
        JSONObject response = gameModel.getStatisticsForEntity(mEphemeralObject);

        clear();
        mLogger.info("Updating ability selection for {}", currentEntityID);

        String passiveAbility = response.getString("passive_ability");
        String basicAbility = response.getString("basic_ability");
        JSONArray otherAbility = response.getJSONArray("other_ability");

//        gameModel.setCurrentActiveEntityAbilityToDefault();

        JSONArray actions = new JSONArray();
        actions.add(basicAbility);
        actions.add(passiveAbility);
        for (int i = 0; i < otherAbility.size(); i++) { actions.add(otherAbility.getString(i)); }

        for (int index = 0; index < actions.size(); index++) {
            String ability = actions.getString(index);

            BeveledButton abilityButton = getOrCreateRow(ability + index);
            abilityButton.setFont(getFontForHeight(mButtonHeight));

            String additionalContext = "";
            if (index == 0) {
                abilityButton.setBackgroundColor(Color.LIGHTGRAY);
                additionalContext = " (Basic)";
            } else if (index == 1) {
                abilityButton.setBackgroundColor(Color.DARKGRAY);
                additionalContext = " (Passive)";
            } else {
                abilityButton.setBackgroundColor(Color.DIMGRAY);
            }
            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(ability) + additionalContext);

            abilityButton.getUnderlyingButton().setOnMouseReleased(e -> {
                JSONObject request = new JSONObject();

                request.put("unit_id", currentEntityID);
                request.put("ability", ability);
                request.put("tile_id", hoveredTileID);
                gameModel.useAbility(request);
            });
        }

        mLogger.info("Finished ability selection for {}", currentEntityID);
    }

    public String getCurrentID() { return mCurrentID; }
    public int getCurrentHash() { return mCurrentHash; }
}