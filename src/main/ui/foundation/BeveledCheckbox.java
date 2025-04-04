package main.ui.foundation;

import javafx.scene.paint.Color;
import main.game.stores.pools.ColorPalette;
import main.constants.JavaFxUtils;

public class BeveledCheckbox extends BeveledButton {
    private boolean isChecked = false;

    public BeveledCheckbox(int width, int height, Color baseColor) {
        super(width, height, "", baseColor);

        // 🔹 **Hover Effects (Maintains Text Outline)**
        JavaFxUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFxUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));


        mButton.setOnMousePressed(e -> setChecked(true));
        mButton.setOnMouseExited(e -> setChecked(false));
//

    }

    public void setChecked(boolean value) {
        isChecked = value;
        mTextNode.setText(isChecked ? "X" : "");
        if (isChecked) {
            mDropShadow.setInput(mInnerShadow);
            mTextNode.setEffect(mDropShadow);
        }
    }
}