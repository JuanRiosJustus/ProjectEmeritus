package main.ui.foundation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.constants.JavaFXUtils;

public class BeveledKeyValue extends BevelStyle {
    private final HBox mContents;
    private final Text mLeftText;
    private final Text mRightText;
    private final Region spacer;
    private final Font mFont;

    public BeveledKeyValue(int width, int height, String left, String right, Color color) {
        super(width, height, color);

        mContents = new HBox();
        mContents.setPrefSize(width, height);
        mContents.setMinSize(width, height);
        mContents.setMaxSize(width, height);

        mContents.setAlignment(Pos.CENTER_LEFT);
        mContents.setSpacing(5); // Optional spacing between elements
        mContents.setPadding(new Insets(5));
        mContents.setBorder(BeveledButton.createBorder(2, 2, color));

        mFont = getFontForHeight(height);

        double fontWidth = JavaFXUtils.computeWidth(mFont, left);
        double fontHeight = JavaFXUtils.computeHeight(mFont, left);
        mLeftText = BevelStyle.createText(left, (int) fontWidth, (int) fontHeight, Color.WHITE, 0.2f);
        mLeftText.setFont(mFont);
        mLeftText.setTextAlignment(TextAlignment.LEFT);

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        fontWidth = JavaFXUtils.computeWidth(mFont, right);
        fontHeight = JavaFXUtils.computeHeight(mFont, right);
        mRightText = BevelStyle.createText(right, (int) fontWidth, (int) fontHeight, Color.WHITE, 0.2f);
        mRightText.setFont(mFont);
        mRightText.setTextAlignment(TextAlignment.RIGHT);

        mContents.getChildren().addAll(mLeftText, spacer, mRightText);
        getChildren().add(mContents);
    }
}

