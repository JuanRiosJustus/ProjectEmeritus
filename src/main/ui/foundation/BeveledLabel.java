package main.ui.foundation;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.game.stores.ColorPalette;
import main.game.stores.FontPool;

public class BeveledLabel extends BevelStyle {

    public BeveledLabel(int width, int height) {
        this(width, height, "", ColorPalette.getRandomColor());
    }

    public BeveledLabel(int width, int height, String text, Color baseColor) {
        super(width, height, baseColor);

        // ** Text Node (Separate from Button) **
        mTextNode.setForegroundColor(ColorPalette.WHITE_LEVEL_4);
//        setText(mTextNode, text, width, height, ColorPalette.WHITE_LEVEL_4, 0.08);

        getChildren().clear();
        getChildren().add(mTextNode.getContent());
    }
    public void setFitText(String text) {

        mTextNode.setFitText(text);
//        if (mTextNode.getText().equals(text)) { return; }
//        Font fitFont = FontPool.getInstance().getFitFont(text, getFont(), mWidth * .9, mHeight * .9);
//        setFont(fitFont);
//        setText(text);
    }

//    public void setText(String text) {
//        if (mTextNode.getText().equals(text)) { return; }
//        mTextNode.setText(text);
//    }
    public Font getFont() { return mTextNode.getContent().getFont(); }
}