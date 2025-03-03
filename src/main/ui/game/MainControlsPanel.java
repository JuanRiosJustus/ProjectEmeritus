package main.ui.game;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.Pair;

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
        getTeamButton();
        getSettingsButton();
        getEndTurnButton();

        mContentPanelScroller = new ScrollPane();
        mContentPanelScroller.setFitToWidth(true);
        mContentPanelScroller.setFitToHeight(true);
        mContentPanelScroller.setPrefSize(width, height); // Limit ScrollPane to 4-button height
        mContentPanelScroller.setMinSize(width, height); // Limit ScrollPane to 4-button height
        mContentPanelScroller.setMaxSize(width, height); // Limit ScrollPane to 4-button height
        mContentPanelScroller.setContent(mContentPanel);
        mContentPanelScroller.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanelScroller.setPickOnBounds(false); // Allow clicks to pass through
        mContentPanelScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove vertical scrollbar
        mContentPanelScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Remove horizontal scrollbar

        // 🔹 Make ScrollPane fully transparent
        setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        setPickOnBounds(false); // Allow clicks to pass through
        getChildren().add(mContentPanelScroller);
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
        Color color = Color.DIMGRAY;//mColor;

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
    public void addButton(String label) {
        Button button = new Button(label);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setMinHeight(mButtonHeight);
        mContentPanel.getChildren().add(button);

        // Adjust VBox height to maintain scrolling
        mContentPanel.setPrefHeight(mContentPanel.getChildren().size() * mButtonHeight + (mContentPanel.getChildren().size() - 1) * 10);
    }
}