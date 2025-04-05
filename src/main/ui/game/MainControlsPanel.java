package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Checksum;
import main.constants.JavaFxUtils;
import main.constants.Pair;
import main.game.main.GameAPI;
import main.game.main.GameController;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.BeveledCheckbox;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainControlsPanel extends GamePanel {
    private final ScrollPane mContentPanelScroller;
    private final VBox mContentPanel;
    private final int mButtonHeight;
    private final int mButtonWidth;
    private final Map<String, Pair<BeveledButton, BeveledCheckbox>> mRows = new LinkedHashMap<>();
    private static final int MAX_VISIBLE_BUTTONS = 4;
    private final Color mColor;

    public MainControlsPanel(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);

        // Ensure only 4 buttons are visible
        mButtonHeight = height / MAX_VISIBLE_BUTTONS;
        mButtonWidth = width;

        // Create VBox with spacing
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setPrefWidth(width);
        mColor = color;

        getAbilitiesButton();
        getMovementButton();
        getStatisticsButton();
        getEndTurnButton();
        getTeamButton();
        getSettingsButton();

        mContentPanelScroller = new ScrollPane();
        mContentPanelScroller.setFitToWidth(true);
        mContentPanelScroller.setFitToHeight(true);
        mContentPanelScroller.setPrefSize(width, height); // Limit ScrollPane to 4-button height
//        mContentPanelScroller.setMinSize(width, height); // Limit ScrollPane to 4-button height
//        mContentPanelScroller.setMaxSize(width, height); // Limit ScrollPane to 4-button height
        mContentPanelScroller.setContent(mContentPanel);
        mContentPanelScroller.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanelScroller.setPickOnBounds(false); // Allow clicks to pass through
        mContentPanelScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        mContentPanelScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        // 🔹 Make ScrollPane fully transparent
//        setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        setPickOnBounds(false); // Allow clicks to pass through
        getChildren().add(mContentPanelScroller);
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        setEffect(JavaFxUtils.createBasicDropShadow(width, height));
    }

    public Pair<BeveledButton, BeveledCheckbox> getOrCreateRow(String name) {
        return getOrCreateRow(name, false);
    }

    public Pair<BeveledButton, BeveledCheckbox> getOrCreateRow(String name, boolean useCheckbox) {
        Pair<BeveledButton, BeveledCheckbox> newRow = mRows.get(name);
        if (newRow != null) { return newRow; }

        HBox hBox = new HBox();
        hBox.setPrefSize(mButtonWidth, mButtonHeight);
        hBox.setMinSize(mButtonWidth, mButtonHeight);
        hBox.setMaxSize(mButtonWidth, mButtonHeight);
        hBox.setFillHeight(true);

        BeveledButton button = null;
        BeveledCheckbox checkBox = null;
        Color color = mColor;

        if (useCheckbox) {
            int newButtonWidth = (int) (mButtonWidth * .8);
            button = new BeveledButton(newButtonWidth, (int) mButtonHeight, name, color);

            int checkboxWidth = (int) (mButtonWidth - newButtonWidth);
            checkBox = new BeveledCheckbox(checkboxWidth, (int) mButtonHeight, color);
            checkBox.setPrefSize(checkboxWidth, mButtonHeight);
            checkBox.setMinSize(checkboxWidth, mButtonHeight);
            checkBox.setMaxSize(checkboxWidth, mButtonHeight);
        } else {
            button = new BeveledButton((int) mButtonWidth, (int) mButtonHeight, name, color);
        }

        hBox.getChildren().add(button);
        if (checkBox != null) { hBox.getChildren().add(checkBox); }
        mContentPanel.getChildren().add(hBox);

        Pair<BeveledButton, BeveledCheckbox> pair = new Pair<>(button, checkBox);
        mRows.put(name, pair);

        return pair;
    }

    public Pair<BeveledButton, BeveledCheckbox> getAbilitiesButton() {
        return getOrCreateRow("Abilities", true);
    }

    public Pair<BeveledButton, BeveledCheckbox> getMovementButton() {
        return getOrCreateRow("Movement", true);
    }

    public Pair<BeveledButton, BeveledCheckbox> getStatisticsButton() {
        return getOrCreateRow("Statistics");
    }

    public Pair<BeveledButton, BeveledCheckbox> getTeamButton() {
        return getOrCreateRow("Team");
    }

    public Pair<BeveledButton, BeveledCheckbox> getSettingsButton() {
        return getOrCreateRow("Settings");
    }

    public Pair<BeveledButton, BeveledCheckbox> getEndTurnButton() {
        return getOrCreateRow("End Turn");
    }

    // Optional: Method to add more buttons dynamically


    @Override
    public void gameUpdate(GameController gameController) {

//        JSONObject currentTurnState = gameController.getCurrentUnitTurnStatus();
        JSONObject currentTurnState = gameController.getSelectedUnitsTurnState();

        if (currentTurnState.isEmpty()) { return; }

        boolean hasActed = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED);
        getAbilitiesButton().getSecond().setChecked(hasActed);
//        OutlineCheckBox checkBox = mCheckBoxMap.get("")
//        getActionsButton().setEnabled(!hasActed);
//        mActionsButton.getCheckBox().setSelected(hasActed);

        boolean hasMoved = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED);
        getMovementButton().getSecond().setChecked(hasMoved);

        boolean isCurrentTurn = currentTurnState.getBoolean("is_current_turn");
        if (isCurrentTurn) {
            getAbilitiesButton().getSecond().getUnderlyingButton().setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            getMovementButton().getSecond().getUnderlyingButton().setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            getAbilitiesButton().getSecond().getUnderlyingButton().setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            getMovementButton().getSecond().getUnderlyingButton().setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }
}