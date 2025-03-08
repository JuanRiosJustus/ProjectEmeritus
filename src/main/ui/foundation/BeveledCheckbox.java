package main.ui.foundation;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.ui.game.JavaFxUtils;

import static main.ui.game.JavaFxUtils.toRgbString;

public class BeveledCheckbox extends Beveled {
    private boolean isChecked = false;

    public BeveledCheckbox(int width, int height, Color baseColor) {
        super(width, height, baseColor);

        mFont = FontPool.getInstance().getFontForHeight((int) (height * 1.2));

        // ** Create Background Button **
        Button button = new Button();
        button.setPrefSize(width, height);
        button.setMinSize(width, height);
        button.setMaxSize(width, height);

        // ** Apply Borders **
        button.setBorder(new Border(
                mOuterBevel.getStrokes().get(0),
                mInnerBevel.getStrokes().get(0)
        ));

        // ** Ensure Background is Set **
        button.setStyle("-fx-background-color: " + toRgbString(baseColor) + "; -fx-background-insets: 0; -fx-background-radius: 0;");

        // ** Text Node (Separate from Button) **
        mTextNode = new Text();
        mTextNode.setFont(mFont);
        mTextNode.setFill(Color.WHITE);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFocusTraversable(false);
        mTextNode.setPickOnBounds(false);
        mTextNode.setMouseTransparent(true);

        // Chain Effects
        mDropShadow.setInput(mInnerShadow);
        mTextNode.setEffect(mDropShadow);

        // ** Add Both Elements to StackPane **
        getChildren().addAll(button, mTextNode);

        // 🔹 **Hover Effects (Maintains Text Outline)**
        JavaFxUtils.addMouseEnteredEvent(button, e -> button.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFxUtils.addMouseExitedEvent(button, e -> button.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));


        button.setOnMousePressed(e -> setChecked(true));
        button.setOnMouseExited(e -> setChecked(false));
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