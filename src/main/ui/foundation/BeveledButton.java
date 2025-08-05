package main.ui.foundation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
        import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.game.stores.ColorPalette;
import main.constants.JavaFXUtils;
import main.game.stores.FontPool;

public class BeveledButton extends BevelStyle {
    protected Button mButton = null;

    public BeveledButton(int width, int height) { this(width, height, "", ColorPalette.getRandomColor()); }
    public BeveledButton(int width, int height, String text, Color baseColor) {
        super(width, height, baseColor);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.1));
        mButton.setFocusTraversable(false);

        // ** Apply Borders & Background **
        setBorder((int) (width * 0.025), (int) (height * 0.05), baseColor);
        mTextNode.setFont(FontPool.getInstance().getFontForHeight((int) (height * .8)));

        mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor));
//
        // ** Text Node ** with left alignment
        mButton.setGraphic(mTextNodeContainer);

        // ** Add Elements to StackPane **
        getChildren().addAll(mButton);

        // 🔹 **Hover Effects**
        JavaFXUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFXUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));

        // 🔹 **Pressed Effect (Shrink & Move Text)**
        mButtonPressedHandler = (EventHandler<Event>) event -> {
            mButton.setScaleX(0.95);
            mButton.setScaleY(0.95);
            mButton.setTranslateY(2);

            mTextNode.setScaleX(0.95);
            mTextNode.setScaleY(0.95);
            mTextNode.setTranslateY(2);
        };
        JavaFXUtils.addMousePressedEvent(mButton, mButtonPressedHandler);

        mButtonReleasedHandler = (EventHandler<Event>) event -> {
            mButton.setScaleX(originalScale);
            mButton.setScaleY(originalScale);
            mButton.setTranslateY(0);

            mTextNode.setScaleX(originalScale);
            mTextNode.setScaleY(originalScale);
            mTextNode.setTranslateY(0);
        };
        // 🔹 **Release Effect (Restore Text & Button Scale)**
        JavaFXUtils.addMouseReleasedEvent(mButton, mButtonReleasedHandler);
    }

    public void setBorder(int borderWidth, int borderHeight, Color baseColor) {
        Border border = createBorder(borderWidth, borderHeight, baseColor);
        mButton.setBorder(border);
    }


    public static Border createBorder(int borderWidth, int borderHeight, Color baseColor) {
        // Outer border color (darker for contrast)
        Color outer = baseColor.darker();

        // Inner accent color (slightly lighter than base)
        Color inner = baseColor.brighter();

        // === Flat Fill ===
//        mButton.setBackground(new Background(
//                new BackgroundFill(baseColor, CornerRadii.EMPTY, Insets.EMPTY)
//        ));

        // === Outer Thick Border ===
        BorderStroke outerStroke = new BorderStroke(
                outer, outer, outer, outer,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(borderHeight, borderWidth, borderHeight, borderWidth),
                Insets.EMPTY
        );

        // === Inner Border Stroke (inset, for pixel-style frame detail) ===
        BorderStroke innerStroke = new BorderStroke(
                inner, inner, inner, inner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1),
                new Insets(borderHeight, borderWidth, borderHeight, borderWidth)
        );


//        mButton.setBorder(new Border(outerStroke, innerStroke));

        // Optional: spacing and font for pixel feel
//        mButton.setPadding(new Insets(3));

        Border border = new Border(outerStroke, innerStroke);
        return border;
    }

    public String getText() { return mTextNode.getText(); }
    //    public void setText(String te)
    public Button getUnderlyingButton() {
        return mButton;
    }

    public void setOnMousePressedV2(EventHandler<? super MouseEvent> value) {
        mButton.setOnMousePressed(value);
    }


    public void setBackgroundColor(Color color) {
        mBaseColor = color;
        mButton.setBorder(getBordering(mWidth, mHeight, mBaseColor));
        mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor));
        JavaFXUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor.brighter())));
        JavaFXUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor)));
    }

    public void setToolTip(Tooltip toolTip) { mButton.setTooltip(toolTip); }
    public void setTextAlignment(Pos pos) {
        mTextNodeContainer.setAlignment(pos);
    }

//    public void setFitText(String text) {
//        mTextNode.setFont(FontPool.getInstance().getFitFont(text, mTextNode.getFont(), mWidth * .9, mHeight));
//        mTextNode.setText(text);
//        mTextNode.setEffect(mDropShadow);
//    }

    public void setFitText(String text) {
//        double maxSize = JavaFXUtils.findMaxFontSize(text, mWidth, mHeight, mTextNode.getFont().getFamily());
//        mTextNode.setFont(FontPool.getInstance().getFont(maxSize));
//        mTextNode.setText(text);
//        mTextNode.setEffect(mDropShadow);
//        int current = (int) mTextNode.getFont().getSize();


        mTextNode.setFitText(text);
//        double fontMultiplier = 0.75;
//        Font font = FontPool.getInstance().getFitFont(text, mTextNode.getFont(), mWidth * fontMultiplier, mHeight);
//        mTextNode.setFont(font);
//        mTextNode.setText(text);
    }

    public void setFitText(String text, double multiplier) {
        mTextNode.setFitText(text, multiplier);
    }
    public void setFont(Font font, double bFactor) {
        mTextNode.setFont(font);
        mTextNode.setExtrusionFactor(bFactor);
    }
}