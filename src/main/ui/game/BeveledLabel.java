package main.ui.game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.FontPoolV2;

import static main.ui.game.JavaFxUtils.toRgbString;

public class BeveledLabel extends StackPane {
    private final Label mTextNode;
    private final DropShadow mTextOutline;
    private final InnerShadow mInnerOutline;

    public BeveledLabel(int width, int height, String text, Color baseColor) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        // ** Scale Font Size Dynamically **
        Font font = FontPoolV2.getInstance().getFontForHeight((int) (height));

        // ** Ensure Background is Set **
        setStyle("-fx-background-color: " + toRgbString(baseColor) + "; -fx-background-insets: 0; -fx-background-radius: 0;");

        // ** Text Node (Separate from Button) **
        mTextNode = new Label(text);
        mTextNode.setFont(font);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFocusTraversable(false);
        mTextNode.setPickOnBounds(false);
        mTextNode.setMouseTransparent(true);

        // ** Add Black Outline to Text (Scales with Font Size) **
        mTextOutline = new DropShadow();
        mTextOutline.setColor(Color.BLACK);
        mTextOutline.setRadius(height * 0.08);
        mTextOutline.setOffsetX(height * 0.05);
        mTextOutline.setOffsetY(height * 0.05);
        mTextOutline.setSpread(1.0);

        mInnerOutline = new InnerShadow();
        mInnerOutline.setColor(Color.WHITE);
        mInnerOutline.setRadius(height * 0.08);
        mInnerOutline.setOffsetX(-height * 0.03);
        mInnerOutline.setOffsetY(-height * 0.03);

        // Chain Effects
        mTextOutline.setInput(mInnerOutline);
        mTextNode.setEffect(mTextOutline);

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