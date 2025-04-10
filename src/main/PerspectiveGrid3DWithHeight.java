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
import java.util.Random;

public class PerspectiveGrid3DWithHeight extends Application {

    private PerspectiveCamera camera;
    // Camera translation and rotation variables
    private double camX = 0, camY = -200, camZ = -800;
    private Rotate rotateX = new Rotate(-30, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    // Random generator for tile heights
    private final Random random = new Random();

    @Override
    public void start(Stage primaryStage) {
        // Create a group to hold our grid tiles.
        Group gridGroup = new Group();
        int rows = 10;
        int cols = 10;
        double tileSize = 50;
        double gap = 10;
        double minHeight = 10;
        double maxHeight = 50; // Maximum elevation for a tile

        // Create grid using 3D boxes (tiles)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Random height for the tile between minHeight and maxHeight
                double tileHeight = minHeight + random.nextDouble() * (maxHeight - minHeight);

                // Create a box where height is the random elevation
                Box tile = new Box(tileSize, tileHeight, tileSize);
                // Set X and Z positions based on col and row
                tile.setTranslateX(col * (tileSize + gap));
                tile.setTranslateZ(row * (tileSize + gap));
                // Adjust Y so that the top of the tile is at y=0
                tile.setTranslateY(tileHeight / 2.0);

                // Use a PhongMaterial with a grey diffuse color
                PhongMaterial material = new PhongMaterial();
                material.setDiffuseColor(Color.GREY);
                tile.setMaterial(material);
                gridGroup.getChildren().add(tile);
            }
        }

        // Center the grid by shifting it left and forward
        gridGroup.setTranslateX(- (cols * (tileSize + gap)) / 2);
        gridGroup.setTranslateZ(- (rows * (tileSize + gap)) / 2);

        // Create the root group and add the grid
        Group root = new Group();
        root.getChildren().add(gridGroup);

        // Create a PerspectiveCamera and set its transforms.
        camera = new PerspectiveCamera(true);
        camera.setFieldOfView(35);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.getTransforms().addAll(
                rotateX,
                rotateY,
                new Translate(camX, camY, camZ)
        );

        // Create a scene with depth buffer enabled.
        Scene scene = new Scene(root, 800, 600, true);
        scene.setCamera(camera);

        // Add key handlers for simple camera navigation.
        scene.setOnKeyPressed(this::handleKeyPress);

        primaryStage.setTitle("3D Grid with Elevated Grey Tiles");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPress(KeyEvent event) {
        switch(event.getCode()){
            case UP:
                camZ += 20;
                break;
            case DOWN:
                camZ -= 20;
                break;
            case LEFT:
                camX += 20;
                break;
            case RIGHT:
                camX -= 20;
                break;
            case W:
                rotateX.setAngle(rotateX.getAngle() - 5);
                break;
            case S:
                rotateX.setAngle(rotateX.getAngle() + 5);
                break;
            case A:
                rotateY.setAngle(rotateY.getAngle() - 5);
                break;
            case D:
                rotateY.setAngle(rotateY.getAngle() + 5);
                break;
            default:
                break;
        }
        // Update the camera's transforms with new translation values.
        camera.getTransforms().setAll(
                rotateX,
                rotateY,
                new Translate(camX, camY, camZ)
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}