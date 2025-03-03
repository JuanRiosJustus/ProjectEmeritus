package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.pools.FontPoolV2;

import static main.ui.game.JavaFxUtils.toRgbString;

public class BeveledCheckbox extends StackPane {
    private boolean isChecked = false;
    private Text mTextNode = null;
    private DropShadow mDropShadow = null;
    private InnerShadow mInnerShadow = null;

    public BeveledCheckbox(int width, int height, Color baseColor) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        // ** Scale Font Size Dynamically **
        Font font = FontPoolV2.getInstance().getFontForHeight(height);

        // ** Compute Dynamic Colors for Bevel **
        Color highlightOuter = baseColor.deriveColor(0, 1, 1.6, 1); // Brighten for top-left outer bevel
        Color highlightInner = baseColor.deriveColor(0, 1, 1.3, 1); // Slightly lighter for inner bevel
        Color shadowInner = baseColor.deriveColor(0, 1, 0.7, 1); // Slightly darker for inner shadow
        Color shadowOuter = baseColor.deriveColor(0, 1, 0.5, 1); // Darkest for bottom-right shadow

        // ** Create Background Button **
        Button button = new Button();
        button.setPrefSize(width, height);
        button.setMinSize(width, height);
        button.setMaxSize(width, height);
        button.setPadding(new Insets(height * 0.1));

        // ** Outer Bevel **
        Border outerBevel = new Border(new BorderStroke(
                highlightOuter, shadowOuter, shadowOuter, highlightOuter,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(height * 0.12),
                Insets.EMPTY
        ));

        // ** Inner Bevel **
        Border innerBevel = new Border(new BorderStroke(
                highlightInner, shadowInner, shadowInner, highlightInner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(height * 0.06),
                Insets.EMPTY
        ));

        // ** Apply Borders **
        button.setBorder(new Border(
                outerBevel.getStrokes().get(0),
                innerBevel.getStrokes().get(0)
        ));

        // ** Ensure Background is Set **
        button.setStyle("-fx-background-color: " + toRgbString(baseColor) + "; -fx-background-insets: 0; -fx-background-radius: 0;");

        // ** Text Node (Separate from Button) **
        mTextNode = new Text();
        mTextNode.setFont(font);
        mTextNode.setFill(Color.WHITE);
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFocusTraversable(false);
        mTextNode.setPickOnBounds(false);
        mTextNode.setMouseTransparent(true);

        // ** Add Black Outline to Text (Scales with Font Size) **
        mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);
        mDropShadow.setRadius(height * 0.08);
        mDropShadow.setOffsetX(height * 0.05);
        mDropShadow.setOffsetY(height * 0.05);
        mDropShadow.setSpread(1.0);

        mInnerShadow = new InnerShadow();
        mInnerShadow.setColor(Color.WHITE);
        mInnerShadow.setRadius(height * 0.08);
        mInnerShadow.setOffsetX(-height * 0.03);
        mInnerShadow.setOffsetY(-height * 0.03);

        // Chain Effects
        mDropShadow.setInput(mInnerShadow);
        mTextNode.setEffect(mDropShadow);

        // ** Add Both Elements to StackPane **
        getChildren().addAll(button, mTextNode);

        // 🔹 **Hover Effects (Maintains Text Outline)**
//        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + toRgbString(baseColor.brighter()) + ";"));
//        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + toRgbString(baseColor) + ";"));

        // 🔹 **Pressed Effect (Shrink & Move Text)**
        button.setOnMousePressed(e -> {
            return;
//            isChecked = !isChecked;
//            setChecked(isChecked);
        });
    }

    public void setChecked(boolean value) {
        isChecked = value;
        mTextNode.setText(isChecked ? "X" : "");
        if (isChecked) {
            mTextNode.setEffect(mDropShadow);
        }
    }
}