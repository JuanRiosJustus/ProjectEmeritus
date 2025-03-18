package main.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.game.stores.pools.ColorPalette;
import main.ui.foundation.BeveledButton;
import main.ui.foundation.GraphicButton;
import main.ui.game.GamePanel;
import main.ui.game.JavaFxUtils;

public class TimeLinePanelItem extends GamePanel {
    public final VBox mContainer;
    public final BeveledButton label;
    public final GraphicButton display;
    public final int displayWidth;
    public final int displayHeight;
    private Color baseColor;

    public TimeLinePanelItem(int width, int height, Color color) {
        super(width, height);
        this.baseColor = color;

        displayWidth = width;
        displayHeight = (int) (height * 0.75); // 75% height for display, remaining for label

        // 🔹 **Ensure Both Elements Have the Same Width**
        display = new GraphicButton(width, displayHeight, color);
        display.setFocusTraversable(false);
        display.setPrefWidth(width); // Ensure full width

        int labelWidth = width;
        int labelHeight = height - displayHeight;
        label = new BeveledButton(labelWidth, labelHeight, "", color);
        label.setFocusTraversable(false);
        label.setFont(getFontForHeight(labelHeight));
        label.setPrefWidth(width); // Ensure full width
        label.setStyle(JavaFxUtils.TRANSPARENT_STYLING);


        // 🔹 **Scrollable Content Panel**
        mContainer = new VBox();
        mContainer.setPrefSize(width, height);
        mContainer.setMinSize(width, height);
        mContainer.setMaxSize(width, height);
        mContainer.setStyle(JavaFxUtils.TRANSPARENT_STYLING);
        mContainer.setFillWidth(true);
        mContainer.setSpacing(0);
        mContainer.getChildren().addAll(display, label); // Now using the wrapper

        setBackgroundColor(color);

        getChildren().add(mContainer);
        setFocusTraversable(false);
        setStyle(JavaFxUtils.TRANSPARENT_STYLING);

        setEffect(JavaFxUtils.createBasicDropShadow(width, height));
    }

    public void setBackgroundColor(Color color) {
        baseColor = color;
        Color adjustedBaseColor = color.deriveColor(0, 1, 0.85, 1);

        setBackground(new Background(new BackgroundFill(adjustedBaseColor, CornerRadii.EMPTY, Insets.EMPTY)));

        JavaFxUtils.setBackgroundWithHoverEffect(display.getUnderlyingButton(), adjustedBaseColor);
        JavaFxUtils.setBackgroundWithHoverEffect(label.getUnderlyingButton(), adjustedBaseColor);
    }
}