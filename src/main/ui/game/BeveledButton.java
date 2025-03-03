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

public class BeveledButton extends StackPane {
    private DropShadow mTextOutline = null;
    private InnerShadow mInnerOutline = null;
    protected Text mTextNode = null;
    protected Button mButton = null;
    public BeveledButton(int width, int height, String text, Color baseColor) {
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
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.1));

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
        mButton.setBorder(new Border(
                outerBevel.getStrokes().get(0),
                innerBevel.getStrokes().get(0)
        ));

        // ** Ensure Background is Set **
        mButton.setStyle("-fx-background-color: " + toRgbString(baseColor) + "; -fx-background-insets: 0; -fx-background-radius: 0;");

        // ** Text Node (Separate from Button) **
        mTextNode = new Text(text);
        mTextNode.setFont(font);
        mTextNode.setFill(Color.WHITE);
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
        getChildren().addAll(mButton, mTextNode);

        // 🔹 **Hover Effects (Maintains Text Outline)**
        mButton.setOnMouseEntered(e -> mButton.setStyle("-fx-background-color: " + toRgbString(baseColor.brighter()) + ";"));
        mButton.setOnMouseExited(e -> mButton.setStyle("-fx-background-color: " + toRgbString(baseColor) + ";"));

        // 🔹 **Pressed Effect (Shrink & Move Text)**
        mButton.setOnMousePressed(e -> {
            mButton.setStyle("-fx-background-color: " + toRgbString(baseColor.darker()) + ";");
            mTextNode.setScaleX(0.9); // Slightly shrink text
            mTextNode.setScaleY(0.9);
            mTextNode.setTranslateY(2); // Move text slightly downward
        });

        // 🔹 **Release Effect (Restore Text Size & Position)**
        mButton.setOnMouseReleased(e -> {
            mButton.setStyle("-fx-background-color: " + toRgbString(baseColor) + ";");
            mTextNode.setScaleX(1.0);
            mTextNode.setScaleY(1.0);
            mTextNode.setTranslateY(0);
        });
    }

    public Button getUnderlyingButton() { return mButton; }

    public void setText(String text) {
        mTextNode.setText(text);
        mTextNode.setEffect(mTextOutline);
    }
    public void setFont(Font font) {
        mTextNode.setFont(font);
        mTextNode.setEffect(mTextOutline);
    }
}