package main.ui.foundation;

import javafx.scene.paint.Color;
import main.game.stores.ColorPalette;
import main.constants.JavaFXUtils;

public class BeveledCheckbox extends BeveledButton {
    private boolean isChecked = false;

    public BeveledCheckbox(int width, int height, Color baseColor) {
        super(width, height, baseColor);

        // 🔹 **Hover Effects (Maintains Text Outline)**
        JavaFXUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFXUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));


        mButton.setOnMousePressed(e -> setChecked(true));
        mButton.setOnMouseExited(e -> setChecked(false));
//

    }

    public void setChecked(boolean value) {
        isChecked = value;
        mTextNode.setContent(isChecked ? "X" : "");
        if (isChecked) {
//            mDropShadow.setInput(mInnerShadow);
//            mTextNode.setEffect(mDropShadow);
        }
    }
}