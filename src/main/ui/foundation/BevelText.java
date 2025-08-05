package main.ui.foundation;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.game.stores.FontPool;

public class BevelText {
    private DropShadow mDropShadow = new DropShadow();
    private Text mText = new Text();
    private int mWidth = 10;
    private int mHeight = 5;
    public BevelText() { this(10, 5); }
    public BevelText(int width, int height) {
        mWidth = width;
        mHeight = height;

        mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);
        mDropShadow.setRadius(height * 0.05);
//        mDropShadow.setOffsetX(mHeight * 0.05);
//        mDropShadow.setOffsetY(mHeight * 0.05);
        mDropShadow.setOffsetX(height * 0.025);
        mDropShadow.setOffsetY(-height * 0.01);
        mDropShadow.setSpread(1.0);

        mText = new Text();
        mText.setText("");
        mText.setFill(Color.WHITE);

        mText.setFont(FontPool.getInstance().getFontForHeight(mHeight));

        mText.setFocusTraversable(false);
        mText.setPickOnBounds(false);
        mText.setMouseTransparent(true);
        mText.setEffect(mDropShadow);
    }

    public Text getContent() { return mText; }
    public void setText(String txt) { mText.setText(txt); mText.setEffect(mDropShadow); }
    public void setFitText(String txt) { setFitText(txt, 1); }
    public void setFitText(String txt, double multiplier) {
        if (mText.getText().equalsIgnoreCase(txt)) { return; }

        Font font = FontPool.getInstance().getFitFont(txt, mText.getFont(), mWidth, mHeight * multiplier);
        mText.setFont(font);
        mText.setText(txt);
    }
    public void setFont(Font font) { mText.setFont(font); }
    public void setAlignment(TextAlignment textAlignment) { mText.setTextAlignment(textAlignment); }
    public void setForegroundColor(Color color) { mText.setFill(color); }
    public void setScaleX(double x) { mText.setScaleX(x); }
    public void setScaleY(double y) { mText.setScaleY(y); }
    public void setTranslateX(double x) { mText.setTranslateX(x); }
    public void setTranslateY(double y) { mText.setTranslateY(y); }
    public String getText() { return mText.getText(); }
    public void setExtrusionFactor(double bFactor) {
        mDropShadow.setRadius(mHeight * bFactor);
        mText.setEffect(mDropShadow);
    }
}
