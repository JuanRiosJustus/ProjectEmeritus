package main.ui.foundation;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.ColorPalette;

public class BeveledLabel extends Beveled {

    public BeveledLabel(int width, int height, String text, Color baseColor) {
        super(width, height, baseColor);

        // ** Text Node (Separate from Button) **
        mTextNode = new Text(text);
        mTextNode.setFont(mFont);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFocusTraversable(false);
        mTextNode.setPickOnBounds(false);
        mTextNode.setMouseTransparent(true);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFill(ColorPalette.WHITE_LEVEL_4);

//        setBorder(new Border(
//                outerBevel.getStrokes().get(0),
//                innerBevel.getStrokes().get(0)
//        ));

        // ** Add Black Outline to Text (Scales with Font Size) **
//        mDropShadow = new DropShadow();
//        mDropShadow.setColor(Color.BLACK);
//        mDropShadow.setRadius(height * 0.08);
//        mDropShadow.setOffsetX(height * 0.05);
//        mDropShadow.setOffsetY(height * 0.05);
//        mDropShadow.setSpread(1.0);
//
//        mInnerShadow = new InnerShadow();
//        mInnerShadow.setColor(Color.WHITE);
//        mInnerShadow.setRadius(height * 0.08);
//        mInnerShadow.setOffsetX(-height * 0.03);
//        mInnerShadow.setOffsetY(-height * 0.03);

        // Chain Effects
//        mDropShadow.setInput(mInnerShadow);
        mTextNode.setEffect(mDropShadow);

        // ** Add Both Elements to StackPane **
        getChildren().addAll(mTextNode);
    }

    public void setText(String text) {
        mTextNode.setText(text);
    }

    public void setFont(Font font) {
        mTextNode.setFont(font);
    }

    public void setTextAlignment(Pos alignment) {
        StackPane.setAlignment(mTextNode, alignment);
    }
}