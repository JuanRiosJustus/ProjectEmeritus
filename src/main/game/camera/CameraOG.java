package main.game.camera;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.main.GameState;

public class CameraOG {

    public enum Movement {
        DRAGGING,
        GLIDING,
        MOMENTUM,
        STATIONARY
    }

    private Movement currently = Movement.STATIONARY;

    private final Vector3f targetPosition = new Vector3f(); // Target position for gliding
    private final Vector3f previousMousePosition = new Vector3f(); // For drag calculations
    private final Vector3f momentumVelocity = new Vector3f(); // Momentum after dragging

    private long lastDragUpdateTime = 0L;
    private long momentumStartTime = 0L;

    private static final long DRAG_UPDATE_DELAY = 16L; // Delay for smoother updates
    private static final float DRAG_EASING = 0.6f;  // Smoother drag movement
    private static final float GLIDE_EASING = 0.1f;  // Glide interpolation factor
    private static final float MOMENTUM_DECAY = 0.9f; // Decay factor for momentum
    private static final long MOMENTUM_DURATION = 3000L; // 3 seconds of momentum

    public void glide(GameState gameState, Tile tile) {
        Vector3f tilePosition = tile.getLocalVector(gameState);
        glide(tilePosition);
    }

    public void glide(Vector3f toPosition) {
        currently = Movement.GLIDING;
        targetPosition.copy(toPosition);
    }

    public void drag(GameState gameState, Vector3f currentMousePosition, boolean isMouseBeingHeld) {

        if (!isMouseBeingHeld) {
            if (currently == Movement.DRAGGING) {
                // Transition to momentum
                momentumVelocity.copy(previousMousePosition.subtract(currentMousePosition));
                momentumStartTime = System.currentTimeMillis();
                currently = Movement.MOMENTUM;
            }
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDragUpdateTime < DRAG_UPDATE_DELAY) {
            return; // Throttle updates for smoother dragging
        }
        lastDragUpdateTime = currentTime;

        if (currently != Movement.DRAGGING) {
            // Initialize the previous mouse position when dragging starts
            previousMousePosition.copy(currentMousePosition);
            currently = Movement.DRAGGING;
            return;
        }

        // Calculate the drag offset based on mouse movement
        Vector3f dragOffset = currentMousePosition.subtract(previousMousePosition);

        // Apply the drag offset to the camera position
        int currentX = gameState.getMainCameraX();
        int currentY = gameState.getMainCameraY();

        int targetX = (int) (currentX - dragOffset.x * DRAG_EASING);
        int targetY = (int) (currentY - dragOffset.y * DRAG_EASING);

        gameState.setMainCameraX(targetX);
        gameState.setMainCameraY(targetY);

        // Update the previous mouse position
        previousMousePosition.copy(currentMousePosition);
        currently = Movement.DRAGGING;
    }

    public void update(GameState gameState) {
        switch (currently) {
            case GLIDING -> glideToTarget(gameState);
            case MOMENTUM -> applyMomentum(gameState);
        }
    }

    private void glideToTarget(GameState gameState) {
        // Calculate the intermediate position for a smooth glide
        int spriteWidth = gameState.getSpriteWidth();
        int spriteHeight = gameState.getSpriteHeight();
        int width = gameState.getMainCameraWidth();
        int height = gameState.getMainCameraHeight();

        int targetX = (int) (-targetPosition.x + (width / 2)) - spriteWidth;
        int targetY = (int) (-targetPosition.y + (height / 2)) - spriteHeight;

        int previousX = gameState.getMainCameraX();
        int previousY = gameState.getMainCameraY();

        int currentX = (int) (previousX + ((-targetX - previousX) * 0.03f));
        int currentY = (int) (previousY + ((-targetY - previousY) * 0.03f));

        gameState.setMainCameraX(currentX);
        gameState.setMainCameraY(currentY);

        // Check if glide is complete
        if (Math.abs(currentX - targetX) < 1 && Math.abs(currentY - targetY) < 1) {
            currently = CameraOG.Movement.STATIONARY;
        }
    }

    private void applyMomentum(GameState gameState) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - momentumStartTime > MOMENTUM_DURATION || momentumVelocity.magnitude() < 0.2) {
            // Stop momentum after 3 seconds or if velocity is negligible
            currently = Movement.STATIONARY;
            return;
        }

        int currentX = gameState.getMainCameraX();
        int currentY = gameState.getMainCameraY();

        // Apply the velocity to the camera position
        int targetX = (int) (currentX + momentumVelocity.x);
        int targetY = (int) (currentY + momentumVelocity.y);

        gameState.setMainCameraX(targetX);
        gameState.setMainCameraY(targetY);

        // Apply decay to the momentum velocity
        momentumVelocity.scale(MOMENTUM_DECAY);
    }
}