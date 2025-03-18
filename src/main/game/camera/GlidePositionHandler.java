package main.game.camera;

import main.constants.Vector3f;
import main.game.main.GameState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlidePositionHandler extends CameraPositionHandler {
    private static final float GLIDE_EASING = 0.05f;         // Interpolation factor for gliding
    private final Map<String, Vector3f> mCamerasToMove = new ConcurrentHashMap<>();
    @Override
    public void handle(GameState gameState, String camera, Vector3f toPosition, boolean ignored) {
        handle(gameState, camera, toPosition);
    }

//    @Override
//    public void handle(GameState gameState, Vector3f toPosition, boolean isMouseBeingPressed) {
//        handle(gameState, gameState.getMainCameraName(), toPosition);
//        handle(gameState, gameState.getTileSelectionCameraName(), toPosition);
//    }

    @Override
    public void handle(GameState gameState, Vector3f toPosition, boolean isMouseBeingPressed) {
        handle(gameState, gameState.getMainCameraID(), toPosition);
    }

    public void handle(GameState gameState, String camera, Vector3f toPosition) {
        // Adjust the target position relative to the camera's current coordinates
        int viewportWidth = gameState.getCameraWidth(camera);
        int viewportHeight = gameState.getCameraHeight(camera);

        int adjustedX = (int) (toPosition.x - viewportWidth / 2);
        int adjustedY = (int) (toPosition.y - viewportHeight / 2);

        int spriteWidthOffset = gameState.getSpriteWidth() / 2;
        int spriteHeightOffset = gameState.getSpriteHeight() / 2;

        int finalDestinationX = adjustedX + spriteWidthOffset;
        int finalDestinationY = adjustedY + spriteHeightOffset;

        mCamerasToMove.put(camera, new Vector3f(finalDestinationX, finalDestinationY));
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
        return Math.abs(currentX - targetPosition.x) == 0 && Math.abs(currentY - targetPosition.y) == 0;
//        return Math.abs(currentX - targetPosition.x) < .001 && Math.abs(currentY - targetPosition.y) < .001;
    }
}