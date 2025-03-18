package main.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.constants.Pair;
import main.ui.foundation.BeveledButton;
import main.ui.game.EscapablePanel;
import main.ui.game.JavaFxUtils;

import java.util.HashMap;
import java.util.Map;

public class SettingsPanel extends EscapablePanel {
    private final Map<String, Pair<BeveledButton, BeveledButton>> mRows = new HashMap<>();
    private final VBox mContentPanel;
    private final int mButtonHeight;
    private final int mButtonWidth;

    public SettingsPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color);


        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        mButtonHeight = getContentHeight() / visibleRows;
        mButtonWidth = getContentWidth();

        setMainContent(mContentPanel);
        getBanner().setText("Settings");

        getOrCreateRow(mRows, "Auto End Turn", mButtonWidth, mButtonHeight);
        getOrCreateRow(mRows, "Auto Follow Unit on Turn End", mButtonWidth, mButtonHeight);
        getOrCreateRow(mRows, "Some other Setting", mButtonWidth, mButtonHeight);

        JavaFxUtils.setCachingHints(this);
    }
}
