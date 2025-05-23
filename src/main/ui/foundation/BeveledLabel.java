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
        mTextNode.setText(text);
        mTextNode.setFill(ColorPalette.WHITE_LEVEL_4);


        getChildren().clear();
        getChildren().add(mTextNode);
    }
    public void setFitText(String text) {
        if (mTextNode.getText().equals(text)) { return; }
        Font fitFont = FontPool.getInstance().getFitFont(text, getFont(), mWidth * .9, mHeight * .9);
        setFont(fitFont);
        setText(text);
    }

    public void setText(String text) {
        if (mTextNode.getText().equals(text)) { return; }
        mTextNode.setText(text);
    }
    public String getText() { return mTextNode.getText(); }

    public void setFont(Font font) {
        mTextNode.setFont(font);
    }
    public Font getFont() { return mTextNode.getFont(); }
    public void setTextColor(Color color) { mTextNode.setFill(color); }
    public void setTextAlignment(Pos alignment) {
        StackPane.setAlignment(mTextNode, alignment);
    }
}