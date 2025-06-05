package main;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class SmoothCameraRotationExample extends Application {

    private PerspectiveCamera camera;
    private double camX = 0, camY = -200, camZ = -800;
    private Rotate rotateX = new Rotate(-30, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    // Use target angles for smooth interpolation
    private double targetAngleX = rotateX.getAngle();
    private double targetAngleY = rotateY.getAngle();

    @Override
    public void start(Stage primaryStage) {
        // Create a simple grid of boxes
        Group gridGroup = new Group();
        int rows = 10;
        int cols = 10;
        double tileSize = 50;
        double gap = 10;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Box tile = new Box(tileSize, 10, tileSize);
                tile.setTranslateX(col * (tileSize + gap));
                tile.setTranslateZ(row * (tileSize + gap));
                tile.setTranslateY(5);  // so the top is flush
                PhongMaterial material = new PhongMaterial(Color.LIGHTGRAY);
                tile.setMaterial(material);
                gridGroup.getChildren().add(tile);
            }
        }
        gridGroup.setTranslateX(-((cols * (tileSize + gap)) / 2));
        gridGroup.setTranslateZ(-((rows * (tileSize + gap)) / 2));

        Group root = new Group(gridGroup);

        // Create the camera and set its initial transforms
        camera = new PerspectiveCamera(true);
        camera.setFieldOfView(35);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.getTransforms().addAll(rotateX, rotateY, new Translate(camX, camY, camZ));

        Scene scene = new Scene(root, 800, 600, true);
        scene.setCamera(camera);

        // Handle key events to update target rotation angles
        scene.setOnKeyPressed(this::handleKeyPress);

        // Use an AnimationTimer to smooth the rotation transitions
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                // Lerp the current angles toward the target angles
                double lerpSpeed = 0.1; // Adjust to control smoothness
                double newAngleX = lerp(rotateX.getAngle(), targetAngleX, lerpSpeed);
                double newAngleY = lerp(rotateY.getAngle(), targetAngleY, lerpSpeed);
                rotateX.setAngle(newAngleX);
                rotateY.setAngle(newAngleY);

                // Update the camera transforms (translate remains unchanged)
                camera.getTransforms().setAll(rotateX, rotateY, new Translate(camX, camY, camZ));
            }
        }.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Smooth Camera Rotation Example");
        primaryStage.show();
    }

    // Linear interpolation function
    private double lerp(double start, double end, double fraction) {
        return start + (end - start) * fraction;
    }

    private void handleKeyPress(KeyEvent event) {
        // Update target angles based on key presses
        switch (event.getCode()) {
            case W:
                targetAngleX -= 5;
                break;
            case S:
                targetAngleX += 5;
                break;
            case A:
                targetAngleY -= 5;
                break;
            case D:
                targetAngleY += 5;
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}