package main.ui.foundation;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.ColorPalette;

public class BeveledLabel extends BeveledButton {

    public BeveledLabel(int width, int height, String text, Color baseColor) {
        super(width, height, "", baseColor);

        // ** Text Node (Separate from Button) **
        mTextNode.setText(text);
        mTextNode.setFill(ColorPalette.WHITE_LEVEL_4);


        getChildren().clear();
        getChildren().add(mTextNode);
    }

    public void setText(String text) {
        if (mTextNode.getText().equals(text)) { return; }
        mTextNode.setText(text);
    }

    public void setFont(Font font) {
        mTextNode.setFont(font);
    }

    public void setTextAlignment(Pos alignment) {
        StackPane.setAlignment(mTextNode, alignment);
    }
}