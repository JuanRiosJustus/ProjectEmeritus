package main.ui.foundation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.game.stores.FontPool;
import main.ui.game.GamePanel;

public class BevelStyle extends GamePanel {
    protected DropShadow mDropShadow = null;
    protected InnerShadow mInnerShadow = null;
    protected BevelText mTextNode = new BevelText();
    protected EventHandler<? super Event> mButtonPressedHandler = null;
    protected EventHandler<? super Event> mButtonReleasedHandler = null;
    protected final double originalScale = 1.0; // Store original scale for reset
    protected Color mBaseColor = null;
    protected Border mOuterBevel;
    protected Border mInnerBevel;
    protected double mBevelSize;

    protected HBox mTextNodeContainer = null;
    public BevelStyle(int width, int height, Color color) {
        this(0, 0, width, height, color);
    }
    public BevelStyle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);

        mBaseColor = color;

        // ** Compute Bevel Colors **
        Color highlightOuter = color.deriveColor(0, 1, 1.6, 1);
        Color highlightInner = color.deriveColor(0, 1, 1.3, 1);
        Color shadowInner = color.deriveColor(0, 1, 0.7, 1);
        Color shadowOuter = color.deriveColor(0, 1, 0.5, 1);

        mBevelSize = Math.min(width, height) * 0.06;
        // ** Outer Bevel **
        mOuterBevel = new Border(new BorderStroke(
                highlightOuter, shadowOuter, shadowOuter, highlightOuter,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(mBevelSize * 2),
                Insets.EMPTY
        ));

        // ** Inner Bevel **
        mInnerBevel = new Border(new BorderStroke(
                highlightInner, shadowInner, shadowInner, highlightInner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(mBevelSize),
                Insets.EMPTY
        ));

        // ** Text Outline Effect **
        mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);
        mDropShadow.setRadius(height * 0.08);
        mDropShadow.setOffsetX(height * 0.05);
        mDropShadow.setOffsetY(height * 0.05);
        mDropShadow.setSpread(1.0);


        mTextNode = new BevelText(width, height);
//        mTextNode.setFont(mFont);
//        mTextNode.setFill(Color.WHITE);
//        mTextNode.setFocusTraversable(false);
//        mTextNode.setPickOnBounds(false);
//        mTextNode.setMouseTransparent(true);

//        mDropShadow.setInput(mInnerShadow);
//        mTextNode.setEffect(mDropShadow);

        mTextNodeContainer = new HBox(mTextNode.getContent());
        mTextNodeContainer.setAlignment(Pos.CENTER);
        mTextNodeContainer.setPadding(new Insets(5, 1, 1, 1));

    }

    public double getOuterBevelSize() {
        if (mOuterBevel != null && !mOuterBevel.getStrokes().isEmpty()) {
            return mOuterBevel.getStrokes().getFirst().getWidths().getTop();
        }
        return 0; // Default to 0 if no bevel
    }

    public double getInnerBevelSize() {
        if (mInnerBevel != null && !mInnerBevel.getStrokes().isEmpty()) {
            return mInnerBevel.getStrokes().get(0).getWidths().getTop();
        }
        return 0;
    }

    public static Border getBordering(int width, int height, Color color) {
        Color highlightOuter = color.deriveColor(0, 1, 1.6, 1);
        Color highlightInner = color.deriveColor(0, 1, 1.3, 1);
        Color shadowInner = color.deriveColor(0, 1, 0.7, 1);
        Color shadowOuter = color.deriveColor(0, 1, 0.5, 1);

        double bevelWidth = width * 0.01;
        double bevelHeight = height * 0.04;

        // ** Outer Bevel **
        Border outerBevel = new Border(new BorderStroke(
                highlightOuter, shadowOuter, shadowOuter, highlightOuter,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(bevelHeight, bevelWidth, bevelHeight, bevelWidth),
                Insets.EMPTY
        ));

        // ** Inner Bevel **
        Border innerBevel = new Border(new BorderStroke(
                highlightInner, shadowInner, shadowInner, highlightInner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(bevelHeight, bevelWidth, bevelHeight, bevelWidth),
                Insets.EMPTY
        ));

        return new Border(
                outerBevel.getStrokes().getFirst(),
                innerBevel.getStrokes().getFirst()
        );
    }
    public double getTotalBevelSize() {
        return getOuterBevelSize() + getInnerBevelSize();
    }

    public String getText() { return mTextNode.getText(); }
    public void setTextColor(Color color) { mTextNode.setForegroundColor(color); }
//    public void setFont(Font font)
    public void setText(String txt) {
//        if (txt.equalsIgnoreCase(mTextNode.getText())) { return; }
        mTextNode.setText(txt);
    }
    public void setExtrusionFactor(double factor) { mTextNode.setExtrusionFactor(factor); }
    public void setText(String txt, Color color) {
        setText(mTextNode.getContent(), txt, mWidth, mHeight, color);
    }

//    public void setText(String txt, Color color, Font font) {
//        setText(mTextNode, txt, mWidth, mHeight, color);
//        mTextNode = createText(txt, mWidth, mHeight, color);
//        mTextNode.setFont(font);
//        mTextNodeContainer.getChildren().clear();
//        mTextNodeContainer.getChildren().add(mTextNode);
//    }

    public static Text createText(String txt, int width, int height, Color color) {
        Text newText = new Text();
        setText(newText, txt, width, height, color, 0.05);
        return newText;
    }
    public static Text createText(String txt, int width, int height, Color color, double bFactor) {
        Text newText = new Text();
        setText(newText, txt, width, height, color, bFactor, getFontForHeight(height));
        return newText;
    }

    public static void setText(Text node, String txt, int width, int height, Color color, Font font) {
        setText(node, txt, width, height, color, 0.05, font);
    }
    public static void setText(Text node, String txt, int width, int height, Color color) {
        setText(node, txt, width, height, color, 0.05);
    }
    public static void setText(Text node, String txt, int width, int height, Color color, double bFactor) {
//        node.setText(txt);

        setText(node, txt, width, height, color, bFactor, FontPool.getInstance().getFitFont(txt, node.getFont(), width, height));
    }

    public static void setText(Text node, String txt, int width, int height, Color color, double bFactor, Font font) {
        // ** Text Outline Effect **
        DropShadow mDropShadow = new DropShadow();
        mDropShadow.setColor(Color.BLACK);
        mDropShadow.setRadius(height * bFactor);
//        mDropShadow.setOffsetX(mHeight * 0.05);
//        mDropShadow.setOffsetY(mHeight * 0.05);
        mDropShadow.setOffsetX(height * 0.025);
        mDropShadow.setOffsetY(-height * 0.01);
        mDropShadow.setSpread(1.0);

        node.setText(txt);
        node.setFill(color);
        node.setFont(font);
        node.setFocusTraversable(false);
        node.setPickOnBounds(false);
        node.setMouseTransparent(true);
        node.setEffect(mDropShadow);
    }

    public void setFont(Font font) {

//        setFont(mTextNode, font);
        mTextNode.setFont(font);
    }

    public static void setFont(Text text, Font font) { text.setFont(font); }

//    public void setBFactor(Region text, double v) {
//        setText(mTextNode, mWidth, mHeight, "", mBaseColor, "", "");
//    }
//    public static Text createText(String txt, int width, int height, Color color) {
//        return createText(txt, width, height, color, 0.05);
//    }
//    public static Text createText(String txt, int width, int height, Color color, double bFactor) {
//        return createText(txt, width, height, color, bFactor, getFontForHeight(height));
//    }
//    public static Text createText(String txt, int width, int height, Color color, double bFactor, Font font) {
//        // ** Text Outline Effect **
//        DropShadow mDropShadow = new DropShadow();
//        mDropShadow.setColor(Color.BLACK);
//        mDropShadow.setRadius(height * bFactor);
////        mDropShadow.setOffsetX(mHeight * 0.05);
////        mDropShadow.setOffsetY(mHeight * 0.05);
//        mDropShadow.setOffsetX(height * 0.025);
//        mDropShadow.setOffsetY(-height * 0.01);
//        mDropShadow.setSpread(1.0);
//
//        Text textNode = new Text(txt);
//        textNode.setFill(color);
//        textNode.setFont(font);
//        textNode.setFocusTraversable(false);
//        textNode.setPickOnBounds(false);
//        textNode.setMouseTransparent(true);
//        textNode.setEffect(mDropShadow);
//
//        return textNode;
//    }
}
