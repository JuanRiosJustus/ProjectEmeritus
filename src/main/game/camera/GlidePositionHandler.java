package main.game.camera;

import main.constants.Vector3f;
import main.game.main.GameState;

public class GlidePositionHandler extends CameraPositionHandler {

    private final Vector3f targetPosition = new Vector3f(); // Target position to glide towards
    private static final float GLIDE_EASING = 0.05f;         // Interpolation factor for gliding

    private boolean gliding = false; // Track whether a glide is active
    @Override
    public void handle(GameState gameState, Vector3f toPosition, boolean isMouseBeingPressed) {
        // Adjust the target position relative to the camera's current coordinates
        int viewportWidth = gameState.getViewportWidth();
        int viewportHeight = gameState.getViewportHeight();

        int adjustedX = (int) (toPosition.x - viewportWidth / 2);
        int adjustedY = (int) (toPosition.y - viewportHeight / 2);

        targetPosition.copy(adjustedX, adjustedY);
        gliding = true; // Set the glide state to active
    }

    @Override
    public void update(GameState gameState) {
        if (!gliding) return; // If not gliding, exit

        int currentX = gameState.getCameraX();
        int currentY = gameState.getCameraY();

        // Interpolate the camera position toward the target position
        int newX = (int) (currentX + (targetPosition.x - currentX) * GLIDE_EASING);
        int newY = (int) (currentY + (targetPosition.y - currentY) * GLIDE_EASING);

        // Update the camera position
        gameState.setCameraX(newX);
        gameState.setCameraY(newY);

        // Stop gliding when the camera is close to the target position
        if (isGlideComplete(newX, newY)) {
            gliding = false;
            gameState.setCameraX((int) targetPosition.x);
            gameState.setCameraY((int) targetPosition.y);
        }
    }

    private boolean isGlideComplete(int currentX, int currentY) {
        return Math.abs(currentX - targetPosition.x) < 1 && Math.abs(currentY - targetPosition.y) < 1;
    }
}