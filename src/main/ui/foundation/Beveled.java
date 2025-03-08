package main.ui.foundation;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.game.stores.pools.FontPool;
import main.ui.game.GamePanel;

public class Beveled extends GamePanel {
    protected DropShadow mDropShadow = null;
    protected InnerShadow mInnerShadow = null;
    protected Text mTextNode = null;
    protected EventHandler<? super Event> mButtonPressedHandler = null;
    protected EventHandler<? super Event> mButtonReleasedHandler = null;
    protected final double originalScale = 1.0; // Store original scale for reset
    protected Color mBaseColor = null;
    protected Border mOuterBevel;
    protected Border mInnerBevel;
    protected Font mFont;
    public Beveled(int width, int height, Color color) {
        this(0, 0, width, height, color);
    }
    public Beveled(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);


        mFont = FontPool.getInstance().getFontForHeight(height);

        mBaseColor = color;

        // ** Compute Bevel Colors **
        Color highlightOuter = color.deriveColor(0, 1, 1.6, 1);
        Color highlightInner = color.deriveColor(0, 1, 1.3, 1);
        Color shadowInner = color.deriveColor(0, 1, 0.7, 1);
        Color shadowOuter = color.deriveColor(0, 1, 0.5, 1);


        // ** Outer Bevel **
        mOuterBevel = new Border(new BorderStroke(
                highlightOuter, shadowOuter, shadowOuter, highlightOuter,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(height * 0.12),
                Insets.EMPTY
        ));

        // ** Inner Bevel **
        mInnerBevel = new Border(new BorderStroke(
                highlightInner, shadowInner, shadowInner, highlightInner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(height * 0.06),
                Insets.EMPTY
        ));

        // ** Text Outline Effect **
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
    }

    public static Border getBorder(int width, int height, Color color) {
        Beveled beveled = new Beveled(width, height, color);
        return new Border(
                beveled.mOuterBevel.getStrokes().get(0),
                beveled.mInnerBevel.getStrokes().get(0)
        );
    }
}
