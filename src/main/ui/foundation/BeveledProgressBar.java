package main.ui.foundation;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class BeveledProgressBar extends BevelStyle {
    private final Pane progressFill; // Progress bar fill
    private double progress = 0.0; // Value from 0.0 to 1.0

    public BeveledProgressBar(int width, int height, Color baseColor, Color progressColor) {
        super(width, height, baseColor);

        // ** Background of Progress Bar (Beveled) **
        setBorder(new Border(mOuterBevel.getStrokes().get(0), mInnerBevel.getStrokes().get(0)));
        setBackground(new Background(new BackgroundFill(baseColor, CornerRadii.EMPTY, Insets.EMPTY)));

        // ** Progress Fill (Colored Bar) **
        progressFill = new Pane();
        progressFill.setBackground(new Background(new BackgroundFill(progressColor, CornerRadii.EMPTY, Insets.EMPTY)));
//        progressFill.setPrefSize(0, height - (mBevelSize * 4)); // Start at 0 width

        mTextNode.setText("164 / 256");
        mTextNode.setTranslateX(getOuterBevelSize() + getInnerBevelSize());
        mTextNode.setTextAlignment(TextAlignment.LEFT);
        // ** Text (Centered inside Progress Bar) **
//        progressText = new Text("0%");
//        progressText.setFill(Color.WHITE);
//        progressText.setFont(Font.font(height * 0.5)); // Adjust font size
//        progressText.setTranslateY(height * 0.25); // Position text

        // ** Ensure Text & Progress Stay Inside Beveled Frame **
//        StackPane.setMargin(mTextNode, new Insets(0, mBevelSize * 2, 0, mBevelSize * 2));

        // ** Add Components to StackPane **
        getChildren().addAll(progressFill, mTextNode);

        // ** Animation to Smooth Progress Changes **
//        new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                updateProgressBar();
//            }
//        }.start();
    }

    /** 🔹 **Sets the progress value (0.0 - 1.0) with smooth animation** */
    public void setProgress(double newProgress) {
        progress = Math.max(0, Math.min(newProgress, 1)); // Clamp between 0 and 1
    }

    /** 🔹 **Updates the visual appearance of the progress bar** */
    private void updateProgressBar() {
        double newWidth = getWidth() * progress;
        progressFill.setPrefWidth(newWidth);
        mTextNode.setText((int) (progress * 100) + "%");
    }
}