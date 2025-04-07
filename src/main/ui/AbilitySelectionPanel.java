package main.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Checksum;
import main.constants.Pair;
import main.game.main.GameController;
import main.game.stores.ColorPalette;
import main.logging.EmeritusLogger;
import main.ui.foundation.BeveledButton;
import main.ui.game.EscapablePanel;
import main.constants.JavaFXUtils;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AbilitySelectionPanel extends EscapablePanel {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilitySelectionPanel.class);
    private final JSONObject mEphemeralRequest = new JSONObject();
    private final Checksum mChecksum = new Checksum();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledButton, BeveledButton>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mButtonHeight;
    private final int mButtonWidth;

    public AbilitySelectionPanel(int x, int y, int width, int height, Color color, int visibleRows) {
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
//        getOrCreateRow("Pulse Ray");
//        getOrCreateRow("Light of the Other");
//        getOrCreateRow("Flamethrower");
//        getOrCreateRow("Vice Claw");
//        getOrCreateRow("Ultra Instinct");
    }

    public Pair<BeveledButton, BeveledButton> getOrCreateRow(String name) {
        Pair<BeveledButton, BeveledButton> newRow = mRows.get(name);
        if (newRow != null) { return newRow; }

        HBox hBox = new HBox();
        hBox.setPrefSize(mButtonWidth, mButtonHeight);
        hBox.setMinSize(mButtonWidth, mButtonHeight);
        hBox.setMaxSize(mButtonWidth, mButtonHeight);
        hBox.setFillHeight(true);

        Color color = mColor;

        int newButtonWidth = (int) (mButtonWidth * .2);
        BeveledButton leftButton = new BeveledButton(newButtonWidth, mButtonHeight, "", color);

        int rightButtonWidth = (int) (mButtonWidth - newButtonWidth);
        BeveledButton rightButton = new BeveledButton(rightButtonWidth, mButtonHeight, name, color);

        hBox.getChildren().addAll(leftButton, rightButton);

        mContentPanel.getChildren().add(hBox); // ✅ Add to the scrollable area

        Pair<BeveledButton, BeveledButton> pair = new Pair<>(leftButton, rightButton);
        mRows.put(name, pair);

        return pair;
    }

    public String getSelectedAction() {
        return mSelectedAction;
    }

    public String getSelectedEntity() {
        return mSelectedEntity;
    }

    public void clear() {
        mRows.clear();
        mContentPanel.getChildren().clear();
    }


    public void gameUpdate(GameController gameController) {
        boolean isShowing = isVisible();
        gameController.setAbilityPanelIsOpen(isShowing);

        if (!isShowing) {
            mSelectedEntity = null;
            mSelectedAction = null;
            return;
        }

        JSONObject response = gameController.getEntityOfCurrentTurnsID();
        String entityID = response.optString("id");
        if (!mChecksum.getThenSet(entityID)) { return; }


        clear();
        JSONObject request = new JSONObject();
        request.put("id", entityID);
        JSONObject statistics = gameController.getStatisticsForEntity(request);


        String passiveAbility = statistics.getString("passive_ability");
        String basicAbility = statistics.getString("basic_ability");
        JSONArray otherAbility = statistics.getJSONArray("other_ability");

        JSONArray actions = new JSONArray();
        actions.put(basicAbility);
        actions.put(passiveAbility);
        for (int i = 0; i < otherAbility.length(); i++) { actions.put(otherAbility.getString(i)); }

        for (int index = 0; index < actions.length(); index++) {
            String action = actions.getString(index);

            Pair<BeveledButton, BeveledButton> pair = getOrCreateRow(action + index);
            BeveledButton detailsButton = pair.getFirst();
            detailsButton.setFont(getFontForHeight(mButtonHeight));
            detailsButton.setText("<");

            detailsButton.setOnMouseReleased(e -> {
                mSelectedAction = action;
                mSelectedEntity = entityID;
            });

            BeveledButton abilityButton = pair.getSecond();
            abilityButton.setFont(getFontForHeight(mButtonHeight));
//            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(action));
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
            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(action) + additionalContext);

            abilityButton.getUnderlyingButton().setOnMouseReleased(e -> {
//                String hoveredTileID = gameController.getG

                mRequestObject.clear();
                mRequestObject.put("id", entityID);
                mRequestObject.put("action", action);
                mLogger.info("Selecting " + action);
                gameController.stageActionForUnit(mRequestObject);

//                gameController.publishEvent(GameController.createEvent(AbilitySystem.USE_ABILITY_EVENT, AbilitySystem.createUsingAbilityEvent(
//                        entityID, action, ""
//                )));
            });
        }
    }
}