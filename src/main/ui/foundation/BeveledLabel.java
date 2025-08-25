package main.ui.foundation;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.game.stores.ColorPalette;

public class BeveledLabel extends BevelStyle {

    public BeveledLabel(int width, int height) {
        this(width, height, "", ColorPalette.getRandomColor());
    }

    public BeveledLabel(int width, int height, String text, Color baseColor) {
        super(width, height, baseColor);

        // ** Text Node (Separate from Button) **
        mTextNode = new BevelText(width, height);
        mTextNode.setForeground(ColorPalette.WHITE_LEVEL_4);

        getChildren().clear();
        getChildren().add(mTextNode);
    }
    public void setFitText(String text) {
        mTextNode.setFitText(text);
    }

    public Font getFont() { return mTextNode.getFont(); }
}