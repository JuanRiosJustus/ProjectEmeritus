package main.ui.foundation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.ColorPalette;
import main.ui.game.JavaFxUtils;

public class BeveledButton extends Beveled {
    protected Button mButton = null;

    public BeveledButton(int width, int height, String text, Color baseColor) {
        super(width, height, baseColor);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.1));
        mButton.setFocusTraversable(false);

        // ** Apply Borders & Background **
        mButton.setBorder(new Border(
                mOuterBevel.getStrokes().get(0),
                mInnerBevel.getStrokes().get(0)
        ));

        mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor));


        // ** Text Node **
        mTextNode = new Text(text);
        mTextNode.setFont(mFont);
        mTextNode.setFill(Color.WHITE);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFocusTraversable(false);
        mTextNode.setPickOnBounds(false);
        mTextNode.setMouseTransparent(true);

        // Chain Effects
        mDropShadow.setInput(mInnerShadow);
        mTextNode.setEffect(mDropShadow);

        // ** Add Elements to StackPane **
        getChildren().addAll(mButton, mTextNode);

        // 🔹 **Hover Effects**
        JavaFxUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFxUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));

        // 🔹 **Pressed Effect (Shrink & Move Text)**
        mButtonPressedHandler = (EventHandler<Event>) event -> {
            mButton.setScaleX(0.95);
            mButton.setScaleY(0.95);
            mButton.setTranslateY(2);

            mTextNode.setScaleX(0.95);
            mTextNode.setScaleY(0.95);
            mTextNode.setTranslateY(2);
        };
        JavaFxUtils.addMousePressedEvent(mButton, mButtonPressedHandler);

        mButtonReleasedHandler = (EventHandler<Event>) event -> {
            mButton.setScaleX(originalScale);
            mButton.setScaleY(originalScale);
            mButton.setTranslateY(0);

            mTextNode.setScaleX(originalScale);
            mTextNode.setScaleY(originalScale);
            mTextNode.setTranslateY(0);
        };
        // 🔹 **Release Effect (Restore Text & Button Scale)**
        JavaFxUtils.addMouseReleasedEvent(mButton, mButtonReleasedHandler);
    }

    public Button getUnderlyingButton() {
        return mButton;
    }

    public void setText(String text) {
        mTextNode.setText(text);
        mTextNode.setEffect(mDropShadow);
    }

    public void setFont(Font font) {
        mTextNode.setFont(font);
        mTextNode.setEffect(mDropShadow);
    }
}