package main.ui.foundation;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.game.stores.ColorPalette;
import main.constants.JavaFXUtils;

import static main.constants.JavaFXUtils.toRgbString;

public class GraphicButton extends StackPane {
    protected Button mButton = null;
    private Color mBaseColor;

    public GraphicButton(int width, int height, Color baseColor) {
        mBaseColor = baseColor;

        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.01));
        mButton.setStyle(ColorPalette.getJavaFxColorStyle(ColorPalette.TRANSPARENT));

        // ** Set Initial Background and Bevel **
        updateBackground(mBaseColor);

        // ** Add Button to StackPane **
        getChildren().add(mButton);

        // 🔹 **Hover Effects (Subtle Lightening)**
        JavaFXUtils.setOnMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFXUtils.setOnMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));

        // 🔹 **Pressed Effect (Shrink & Move Text)**
        JavaFXUtils.addMousePressedEvent(mButton, e -> applyPressedState(baseColor));
//         🔹 **Release Effect (Restore Text & Button Scale)**
        JavaFXUtils.addMouseReleasedEvent(mButton, e -> restoreNormalState(baseColor));
    }

    public void setImageView(Node node) {
        mButton.setGraphic(node);
    }

    public Button getUnderlyingButton() { return mButton; }
    public void setBackgroundColor(Color color) {
        mBaseColor = color; // Store new base color for hover effects
//        updateBackground(color);

        mButton.setStyle(ColorPalette.getJavaFxColorStyle(color));

        // 🔹 **Hover Effects**
        JavaFXUtils.setOnMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(color.brighter())));
        JavaFXUtils.setOnMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(color)));
    }

    private void updateBackground(Color color) {
        mButton.setStyle("-fx-background-color: " + toRgbString(color) + "; -fx-background-insets: 0; -fx-background-radius: 0;");
    }

    private void applyPressedState(Color color) {
        mButton.setScaleX(0.75);
        mButton.setScaleY(0.75);

//        updateBackground(color.deriveColor(0, 1, 0.9, 1));
    }

    private void restoreNormalState(Color color) {
        mButton.setScaleX(1.0);
        mButton.setScaleY(1.0);
//        updateBackground(color);
    }
}