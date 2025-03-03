package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Pair;
import main.constants.SimpleCheckSum;
import main.game.main.GameController;
import main.logging.EmeritusLogger;
import main.utils.StringUtils;
import org.json.JSONArray;

import javax.swing.JButton;
import java.util.HashMap;
import java.util.Map;

public class AbilitySelectionPanel extends EscapablePanel {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(AbilitySelectionPanel.class);
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private final VBox mContentPanel;
    private final Map<String, Pair<BeveledButton, BeveledButton>> mRows = new HashMap<>();
    private String mSelectedAction = null;
    private String mSelectedEntity = null;
    private final int mButtonHeight;
    private final int mButtonWidth;

    public AbilitySelectionPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mButtonHeight = getContentHeight() / visibleRows;
        mButtonWidth = getContentWidth();

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
        gameController.setActionPanelIsOpen(isShowing);

        if (!isShowing) {
            mSelectedEntity = null;
            mSelectedAction = null;
            return;
        }

        String unit = gameController.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("ACTIONS", unit)) { return; }

        clear();
        JSONArray actions = gameController.getActionsOfUnit(unit);
        for (int index = 0; index < actions.length(); index++) {
            String action = actions.getString(index);

            Pair<BeveledButton, BeveledButton> pair = getOrCreateRow(action);
            BeveledButton detailsButton = pair.getFirst();
            detailsButton.setFont(getFontForHeight(mButtonHeight));
            detailsButton.setText("<");

            detailsButton.getUnderlyingButton().setOnMouseReleased(e -> {
                mSelectedAction = action;
                mSelectedEntity = unit;
            });

            BeveledButton abilityButton = pair.getSecond();
            abilityButton.setFont(getFontForHeight(mButtonHeight));
            abilityButton.setText(StringUtils.convertSnakeCaseToCapitalized(action));

            abilityButton.getUnderlyingButton().setOnMouseReleased(e -> {
                mEphemeralObject.clear();
                mEphemeralObject.put("id", unit);
                mEphemeralObject.put("action", action);
                mLogger.info("Selecting " + action);
                gameController.stageActionForUnit(mEphemeralObject);
            });
        }
    }
}