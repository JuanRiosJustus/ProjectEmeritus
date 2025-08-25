package main.ui.foundation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.game.stores.FontPool;

import static javafx.application.Application.launch;

public class BevelTextV2 extends Region {
    private final DropShadow mDropShadow;
    private final Text mText;
    private static final float DEFAULT_SHADOW_SPREAD = 1f;

    private int mWidth;
    private int mHeight;

    public BevelTextV2() {
        this(0, 0);
    }

    public BevelTextV2(int width, int height) {
        mWidth = width;
        mHeight = height;

        mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);
//        mDropShadow.setRadius(height * 0.05);
//        mDropShadow.setOffsetX(mHeight * 0.05);
//        mDropShadow.setOffsetY(mHeight * 0.05);
//        mDropShadow.setOffsetX(height * 0.025);
//        mDropShadow.setOffsetY(-height * 0.01);


//        mDropShadow.setRadius(contentH * 0.05);
////        mDropShadow.setSpread(1);
//        mDropShadow.setOffsetX(contentH * 0.025);
//        mDropShadow.setOffsetY(-contentH * 0.01);


        mDropShadow.setSpread(1);

        mText = new Text();
        mText.setFill(Color.WHITE);

        mText.setFont(FontPool.getInstance().getFontForHeight(mHeight));
        mText.setEffect(mDropShadow);

        getChildren().add(mText);
    }

    /** === Public API (kept same as before) === */
    public Text getContent() { return mText; }

    public void setText(String txt, int width, int height) {
        mText.setText(txt);
        mText.setEffect(mDropShadow);
        layoutChildren(width, height);
    }

    public void setText(String txt) {
        mText.setText(txt);
        mText.setEffect(mDropShadow);
        requestLayout(); // trigger re-layout
//        layoutChildren();
    }

    public void setFitText(String txt) { setFitText(txt, 1); }

    public void setFitText(String txt, double multiplier) {
        if (mText.getText().equalsIgnoreCase(txt)) return;
        Font font = FontPool.getInstance()
                .getFitFont(txt, mText.getFont(), mWidth, mHeight * multiplier);
        mText.setFont(font);
        mText.setText(txt);
        requestLayout();
//        layoutChildren();
    }

    public void setFont(Font font) { mText.setFont(font); }
    //    public void setFont(Font font) { mText.setFont(font); }
    public void setAlignment(TextAlignment textAlignment) { mText.setTextAlignment(textAlignment); }
    public void setForegroundColor(Color color) { mText.setFill(color); }
    public String getText() { return mText.getText(); }

    public void setExtrusionFactor(double bFactor) {
        mDropShadow.setRadius(mHeight * bFactor);
        mText.setEffect(mDropShadow);
    }

    /** === Resizing logic === */
    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutChildren(w, h);
    }

    protected void layoutChildren(double w, double h) {
        mWidth = (int) w;
        mHeight = (int) h;

        double padding = Math.min(w, h) * 0.05; // 5% inset
        double contentW = w - 2 * padding;
        double contentH = h - 2 * padding;

        if (contentW <= 0 || contentH <= 0) {
            return;
        }

        // Start from content height
        Font baseFont = mText.getFont();
        double size = contentH;
        mText.setFont(Font.font(baseFont.getFamily(), size));

        // Shrink until it fits inside the padded content box
        while ((mText.getLayoutBounds().getWidth() > contentW ||
                mText.getLayoutBounds().getHeight() > contentH) && size > 1) {
            size -= 1;
            mText.setFont(Font.font(baseFont.getFamily(), size));
        }

        // Update drop shadow relative to content height
        mDropShadow.setRadius(contentH * 0.05);
        mDropShadow.setSpread(1);
        mDropShadow.setOffsetX(contentH * 0.025);
        mDropShadow.setOffsetY(-contentH * 0.01);

        // Measure text again after resizing
        double textW = mText.getLayoutBounds().getWidth();
        double textH = mText.getLayoutBounds().getHeight();

        // Center within region (ignoring padding, since we shrunk to fit inside content box)
        mText.setLayoutX((w - textW) / 2);
        mText.setLayoutY((h + textH) / 2 - mText.getBaselineOffset());
        mText.setEffect(mDropShadow);
    }

    @Override
    protected double computePrefWidth(double height) {
        return mText.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width) {
        return mText.prefHeight(width);
    }

    /** === Test Application === */
    public static class BevelTextTest extends Application {
        @Override
        public void start(Stage stage) {
            BevelTextV2 bevelText = new BevelTextV2();
            bevelText.setText("Hello BevelText!");

            StackPane root = new StackPane(bevelText);
            Scene scene = new Scene(root, 400, 200);

            stage.setTitle("BevelText Test");
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}