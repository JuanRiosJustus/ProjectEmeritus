package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import static main.ui.game.JavaFxUtils.toRgbString;

public class GraphicButton extends StackPane {
    protected Button mButton = null;
    private Color mBaseColor;

    public GraphicButton(int width, int height, Color baseColor) {
        this.mBaseColor = baseColor;

        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.1));

        // ** Set Initial Background Color **
        updateBackground(mBaseColor);

        // ** Add Button to StackPane **
        getChildren().add(mButton);

        // 🔹 **Hover Effects**
        mButton.setOnMouseEntered(e -> updateBackground(mBaseColor.brighter()));
        mButton.setOnMouseExited(e -> updateBackground(mBaseColor));

        // 🔹 **Pressed Effect (Slight Shrink & Darken Background)**
        mButton.setOnMousePressed(e -> {
            mButton.setScaleX(0.9);
            mButton.setScaleY(0.9);
            updateBackground(mBaseColor.darker());
        });

        // 🔹 **Release Effect (Restore Scale & Background)**
        mButton.setOnMouseReleased(e -> {
            mButton.setScaleX(1.0);
            mButton.setScaleY(1.0);
            updateBackground(mBaseColor);
        });
    }

    public Button getUnderlyingButton() {
        return mButton;
    }

    public void setImageView(Node node) {
        mButton.setGraphic(node);
    }

    public void setBackgroundColor(Color color) {
        this.mBaseColor = color; // Store new base color for hover effects
        updateBackground(color);
    }

    private void updateBackground(Color color) {
        mButton.setStyle("-fx-background-color: " + toRgbString(color) + "; -fx-background-insets: 0; -fx-background-radius: 0;");
    }
}