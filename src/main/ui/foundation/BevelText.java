package main.ui.foundation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.game.stores.FontPool;

public class BevelText extends Text {
    private final DropShadow mDropShadow;
    private static final float DEFAULT_SHADOW_SPREAD = 0.85f;
    private static final float DEFAULT_SHADOW_RADIUS = 0.085f;

    private int mWidth;
    private int mHeight;
    private float mShadowSpread;
    private float mShadowRadius;

    public BevelText() {
        this(-1, -1);
    }

    public BevelText(int width, int height) {
        mWidth = width;
        mHeight = height;

        mShadowSpread = DEFAULT_SHADOW_SPREAD;
        mShadowRadius = DEFAULT_SHADOW_RADIUS;

        mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);

        setFill(Color.WHITE);
        setFont(FontPool.getInstance().getFontForHeight(mHeight));
        setEffect(mDropShadow);
        update(width, height);
    }

    /** === Public API (kept same as before) === */
    public void setContent(String txt, int width, int height) {
        setText(txt);
        setEffect(mDropShadow);
        update(width, height);
    }

    public void setContent(String txt) {
        setText(txt);
        setEffect(mDropShadow);
        update(mWidth, mHeight);
    }

    public void setFitText(String txt) { setFitText(txt, 1); }

    public void setFitText(String txt, double multiplier) {
        if (getText().equalsIgnoreCase(txt)) return;
        Font font = FontPool.getInstance()
                .getFitFont(txt, getFont(), mWidth, mHeight * multiplier);
        setFont(font);
        setContent(txt);
        update(mWidth, mHeight);
    }

//    public void setFont(Font font) { setFont(font); }
    public void setAlignment(TextAlignment textAlignment) { setTextAlignment(textAlignment); }
    public void setForeground(Color color) { setFill(color); }

    public void setExtrusionFactor(double bFactor) {
        mDropShadow.setRadius(mHeight * bFactor);
        setEffect(mDropShadow);
    }

    private void update(double w, double h) {
        double padding = Math.min(w, h) * 0.05; // 5% inset

        // Estimate shadow size relative to height
        double shadowRadius = h * mShadowRadius;
        double shadowSpread = mShadowSpread;
        double shadowOffsetX = h * 0.025;
        double shadowOffsetY = -h * 0.01;

        // The shadow can extend beyond text bounds in every direction.
        double shadowExtra = shadowRadius * (1 + shadowSpread);

        // Available space = region size - padding - shadow margins
        double contentW = w - 2 * (padding + shadowExtra + Math.abs(shadowOffsetX));
        double contentH = h - 2 * (padding + shadowExtra + Math.abs(shadowOffsetY));

        if (contentW <= 0 || contentH <= 0) {
            return;
        }

        // Start from max height
        Font baseFont = getFont();
        double size = contentH;
        setFont(Font.font(baseFont.getFamily(), size));

        // Shrink until text fits WITHIN content box
        while ((getLayoutBounds().getWidth() > contentW ||
                getLayoutBounds().getHeight() > contentH) && size > 1) {
            size -= 1;
            setFont(Font.font(baseFont.getFamily(), size));
        }

        // Apply final shadow
        mDropShadow.setRadius(shadowRadius);
        mDropShadow.setSpread(shadowSpread);
        mDropShadow.setOffsetX(shadowOffsetX);
        mDropShadow.setOffsetY(shadowOffsetY);

        // Measure final text
        double textW = getLayoutBounds().getWidth();
        double textH = getLayoutBounds().getHeight();

        // Center it (still visually centered even with shadow)
        setLayoutX((w - textW) / 2);
        setLayoutY((h + textH) / 2 - getBaselineOffset());
        setEffect(mDropShadow);
    }

//    protected void setupContents(double w, double h) {
//        mWidth = (int) w;
//        mHeight = (int) h;
//
//        double padding = Math.min(w, h) * 0.05; // 5% inset
//        double contentW = w - 2 * padding;
//        double contentH = h - 2 * padding;
//
//        if (contentW <= 0 || contentH <= 0) {
//            return;
//        }
//
//        // Start from content height
//        Font baseFont = getFont();
//        double size = contentH;
//        setFont(Font.font(baseFont.getFamily(), size));
//
//        // Shrink until it fits inside the padded content box
//        while ((getLayoutBounds().getWidth() > contentW ||
//                getLayoutBounds().getHeight() > contentH) && size > 1) {
//            size -= 1;
//            setFont(Font.font(baseFont.getFamily(), size));
//        }
//
//        // Update drop shadow relative to content height
//        mDropShadow.setRadius(contentH * 0.085);
//        mDropShadow.setSpread(1);
//        mDropShadow.setOffsetX(contentH * 0.025);
//        mDropShadow.setOffsetY(-contentH * 0.01);
//
//        // Measure text again after resizing
//        double textW = getLayoutBounds().getWidth();
//        double textH = getLayoutBounds().getHeight();
//
//        // Center within region (ignoring padding, since we shrunk to fit inside content box)
//        setLayoutX((w - textW) / 2);
//        setLayoutY((h + textH) / 2 - getBaselineOffset());
//        setEffect(mDropShadow);
//    }

//    @Override
//    protected double computePrefWidth(double height) {
//        return prefWidth(height);
//    }
//
//    @Override
//    protected double computePrefHeight(double width) {
//        return prefHeight(width);
//    }

    public void setSpread(float v) {
    }

    /** === Test Application === */
    public static class BevelTextTest extends Application {
        @Override
        public void start(Stage stage) {
            BevelText bevelText = new BevelText();
            bevelText.setContent("Hello BevelText!");

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