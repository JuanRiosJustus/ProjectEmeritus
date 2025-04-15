package main.ui.foundation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class BeveledProgressBar extends BevelStyle {
    private final Pane progressFill; // Progress bar fill
    private double targetProgress = 0.0; // Target progress (0.0 - 1.0)
    private double currentProgress = 0.0; // Current progress (used for lerping)

    public BeveledProgressBar(int width, int height, Color baseColor, Color progressColor) {
        super(width, height, baseColor);

        // ** Background of Progress Bar (Beveled) **
        setBorder(new Border(mOuterBevel.getStrokes().get(0), mInnerBevel.getStrokes().get(0)));
        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        // ** Progress Fill (Colored Bar) **
        progressFill = new Pane();
        progressFill.setBackground(new Background(new BackgroundFill(progressColor, CornerRadii.EMPTY, Insets.EMPTY)));
        progressFill.setPrefSize(width - (getTotalBevelSize() * 2), height - (getTotalBevelSize() * 2)); // Ensure it doesn't cover bevels
        progressFill.setPickOnBounds(false);
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);

        // ** Text (Centered Inside Progress Bar) **
        mTextNode.setTextAlignment(TextAlignment.CENTER);
        mTextNode.setFill(Color.WHITE);
        setProgress(100, 100, 100 + "/" + 100);

        // ** Ensure Text & Progress Stay Inside Beveled Frame **
        StackPane.setMargin(mTextNode, new Insets(0, getOuterBevelSize() * 2, 0, getOuterBevelSize() * 2));

        // ** Add Components to StackPane **
        getChildren().addAll(progressFill, mTextNode);

        // ** Smooth Progress Update (LERP) **
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                smoothProgressUpdate();
            }
        }.start();
    }

    /** 🔹 **Sets the current and max values, updates the target progress** */
    public void setProgress(int current, int max, String txt) {
        if (current <= 0 || current > max) { return; }
        targetProgress = Math.max(0, Math.min((double) current / max, 1)); // Clamp between 0 and 1
        mTextNode.setText(txt);
    }

    /** 🔹 **Smoothly interpolates progress for animation** */
    private void smoothProgressUpdate() {
        double lerpSpeed = 0.1; // Adjust this value for smoother or faster animations
        currentProgress += (targetProgress - currentProgress) * lerpSpeed;

        if (currentProgress >= 1) { return; }
        double newWidth = getWidth() * currentProgress;
        progressFill.setPrefWidth(newWidth - (getTotalBevelSize() * 1.5));
        progressFill.setMinWidth(newWidth - (getTotalBevelSize() * 1.5));
        progressFill.setMaxWidth(newWidth - (getTotalBevelSize() * 1.5));
    }

    // ---------------------- Test Application ----------------------

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            // Create an instance of BeveledProgressBar with desired parameters.
            BeveledProgressBar progressBar = new BeveledProgressBar(400, 50, Color.DARKSLATEGRAY, Color.LIMEGREEN);

            // Simulate setting progress to 75% (e.g., 75 out of 100).
            // Use Platform.runLater to update after the stage is shown.
            progressBar.setOnMouseClicked(e -> { progressBar.setProgress(75, 100, "75/100"); });
//            Platform.runLater(() -> progressBar.setProgress(75, 100, "75/100"));

            // Create a root container and add the progress bar.
            StackPane root = new StackPane(progressBar);
            root.setPadding(new Insets(20));
            Scene scene = new Scene(root, 600, 200);
            primaryStage.setTitle("Beveled Progress Bar Test");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}