package main.ui.foundation;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import main.game.stores.FontPool;

public class BeveledKVP extends HBox {
    private int mWidth = 0;
    private int mHeight = 0;
    private BeveledButton mLeftLabel;
    private BeveledButton mRightLabel;
    public BeveledKVP(int width, int height, Color color) {
        mWidth = width;
        mHeight = height;

        // ✅ Create Beveled Labels
        mLeftLabel = new BeveledButton((int) (width * .666), height, color);
        mLeftLabel.setTextAlignment(Pos.CENTER_LEFT);
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
        mLeftLabel.disableBevelEffect();
        mLeftLabel.disableMouseEnteredAndExitedEffect();

        mRightLabel = new BeveledButton((int) (width * .333), height, color);
        mRightLabel.setTextAlignment(Pos.CENTER_RIGHT);
        mRightLabel.setFont(FontPool.getInstance().getFontForHeight(height));
        mRightLabel.disableBevelEffect();
        mRightLabel.disableMouseEnteredAndExitedEffect();

//        HBox contentPane = new HBox(leftLabel, rightLabel);
        getChildren().addAll(mLeftLabel, mRightLabel);
    }

    public BeveledButton getLeft() { return mLeftLabel; }
    public BeveledButton getRight() { return mRightLabel; }
}
