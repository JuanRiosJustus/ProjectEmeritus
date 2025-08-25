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
import main.game.stores.ColorPalette;
import main.constants.JavaFXUtils;

public class BeveledButton extends BevelStyle {
    protected Button mButton = null;
    private boolean mDisabledBorder = false;

    public BeveledButton(int width, int height) { this(width, height, ColorPalette.getRandomColor()); }
    public BeveledButton(int width, int height, Color baseColor) {
        super(width, height, baseColor);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.05, width * 0.05, height * 0.05, width * 0.05));
        mButton.setFocusTraversable(false);

        // ** Apply Borders & Background **
        enableBevelEffect();
        enableMouseEnteredAndExitedEffect();

        mTextNode = new BevelText(width, height);
        mTextNodeContainer.getChildren().clear();
        mTextNodeContainer.getChildren().add(mTextNode);

        mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor));
//
        // ** Text Node ** with left alignment
        mButton.setGraphic(mTextNodeContainer);

        // ** Add Elements to StackPane **
        getChildren().addAll(mButton);

        // 🔹 **Hover Effects**
//        JavaFXUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
//        JavaFXUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));

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


    public void disableBevelEffect() { mButton.setBorder(null); }
    public void enableBevelEffect() { setBorder((int) (mWidth * 0.025), (int) (mHeight * 0.05), mBaseColor); }
    public void disableMouseEnteredAndExitedEffect() {
//        JavaFXUtils.removeMousePressedEvent(mButton, mButtonPressedHandler);
//        JavaFXUtils.removeMouseReleasedEvent(mButton, mButtonReleasedHandler);
        JavaFXUtils.removeMouseEnteredEvent(mButton, mButtonEnteredHandler);
        JavaFXUtils.removeMouseExitedEvent(mButton, mButtonExitedHandler);

//        mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor));
//        mButton.setBackground(new Background(new BackgroundFill(mBaseColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    public void enableMouseEnteredAndExitedEffect() {
        mButtonEnteredHandler = e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor.brighter()));
        JavaFXUtils.addMouseEnteredEvent(mButton, mButtonEnteredHandler);

        mButtonExitedHandler = e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor));
        JavaFXUtils.addMouseExitedEvent(mButton, mButtonExitedHandler);
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

    public String getButtonText() { return mTextNode.getText(); }
    //    public void setText(String te)
    public Button getUnderlyingButton() {
        return mButton;
    }

    public void setOnMousePressedV2(EventHandler<? super MouseEvent> value) {
        mButton.setOnMousePressed(value);
    }


    public void setBackground(Color color) {
        mBaseColor = color;
        mButton.setBorder(getBordering(mWidth, mHeight, mBaseColor));
        mButton.setStyle(ColorPalette.getJavaFxColorStyle(mBaseColor));
//
////        disableBevelEffect();
////        disableMouseEnteredAndExitedEffect();
//        enableMouseEnteredAndExitedEffect();
    }

    public void setTooltip(Tooltip toolTip) {
//        mTextNode.setMouseTransparent(true);
//        mTextNode.setFocusTraversable(false);
        setMouseTransparent(false);
        mButton.setMouseTransparent(false);
        mButton.setTooltip(toolTip);
    }
    public void setTextAlignment(Pos pos) {
        mTextNodeContainer.setAlignment(pos);
    }

    public void setFitText(String text) {

        mTextNode.setFitText(text);
    }
}