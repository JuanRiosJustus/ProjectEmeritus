package main.game.camera;

import main.constants.Vector3f;
import main.game.main.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlidePositionHandler extends CameraPositionHandler {
    private static final float GLIDE_EASING = 0.05f;         // Interpolation factor for gliding
    private final Map<String, Vector3f> mCamerasToMove = new HashMap<>();
    @Override
    public void handle(GameState gameState, Vector3f toPosition, boolean isMouseBeingPressed) {
        handle(gameState, gameState.getMainCameraName(), toPosition, false);
        handle(gameState, gameState.getTileSelectionCameraName(), toPosition, false);
    }

    public void handle(GameState gameState, String camera, Vector3f toPosition, boolean isMouseBeingPressed) {
        // Adjust the target position relative to the camera's current coordinates
        int viewportWidth = gameState.getCameraWidth(camera);
        int viewportHeight = gameState.getCameraHeight(camera);

        int adjustedX = (int) (toPosition.x - viewportWidth / 2);
        int adjustedY = (int) (toPosition.y - viewportHeight / 2);

        mCamerasToMove.put(camera, new Vector3f(adjustedX, adjustedY));
    }


    @Override
    public void update(GameState gameState) {
        for (Map.Entry<String, Vector3f> entry : mCamerasToMove.entrySet()) {
            String camera = entry.getKey();
            Vector3f targetPosition = entry.getValue();
            int currentX = gameState.getCameraX(camera);
            int currentY = gameState.getCameraY(camera);

            // Interpolate the camera position toward the target position
            int newX = (int) (currentX + (targetPosition.x - currentX) * GLIDE_EASING);
            int newY = (int) (currentY + (targetPosition.y - currentY) * GLIDE_EASING);

            // Update the camera position
            gameState.setCameraX(camera, newX);
            gameState.setCameraY(camera, newY);

            // Stop gliding when the camera is close to the target position
            if (isGlideComplete(targetPosition, newX, newY)) {
                gameState.setCameraX(camera, (int) targetPosition.x);
                gameState.setCameraY(camera, (int) targetPosition.y);
                mCamerasToMove.remove(camera);
            }
        }
    }

    private boolean isGlideComplete(Vector3f targetPosition, int currentX, int currentY) {
        return Math.abs(currentX - targetPosition.x) < 1 && Math.abs(currentY - targetPosition.y) < 1;
    }
}