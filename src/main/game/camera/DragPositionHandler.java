package main.game.camera;

import main.constants.Vector3f;
import main.game.main.GameState;

public class DragPositionHandler extends CameraPositionHandler {

    private final Vector3f previousMousePosition = new Vector3f(); // Last recorded mouse position
    private final Vector3f momentumVelocity = new Vector3f();     // Velocity for momentum after drag ends

    private long lastUpdateTime = 0L;    // For throttling updates
    private long momentumStartTime = 0L; // When momentum starts

    private static final long UPDATE_DELAY = 16L;        // Throttle drag updates for smoothness
    private static final float MOMENTUM_DECAY = 0.95f;   // Decay factor for momentum over time
    private static final long MOMENTUM_DURATION = 5000L; // Momentum lasts 3 seconds after release
    private static final float MINIMUM_MOMENTUM = 2.0f;  // Minimum momentum to ensure some noticeable movement

    private boolean dragging = false;

    @Override
    public void handle(GameState gameState, Vector3f currentMousePosition, boolean isMouseBeingPressed) {
        long currentTime = System.currentTimeMillis();

        // Throttle updates to avoid jittery movements
        if (currentTime - lastUpdateTime < UPDATE_DELAY) {
            return;
        }
        lastUpdateTime = currentTime;

        if (!isMouseBeingPressed) {
            // Mouse button released
            if (dragging) {
                // Transition to momentum when dragging stops
                momentumStartTime = currentTime;
                dragging = false;
            }
            return;
        }

        // If we reach here, mouse is being pressed
        if (!dragging) {
            // Initialize the previous position and reset momentum when starting a new drag
            previousMousePosition.copy(currentMousePosition);
            momentumVelocity.copy(0, 0, 0);
            dragging = true;
        }

        // Calculate how far the mouse moved since last update
        Vector3f dragOffset = currentMousePosition.subtract(previousMousePosition);

        // Move the camera by the offset
        gameState.setCameraX(gameState.getCameraX() - (int) dragOffset.x);
        gameState.setCameraY(gameState.getCameraY() - (int) dragOffset.y);

        // Ensure minimum momentum for a noticeable release movement
        if (dragOffset.magnitude() < MINIMUM_MOMENTUM) {
            dragOffset.normalize().scale(MINIMUM_MOMENTUM);
        }

        // Update momentum velocity for after the user releases the mouse
        momentumVelocity.copy(dragOffset);

        // Update previous mouse position for the next calculation
        previousMousePosition.copy(currentMousePosition);
    }

    @Override
    public void update(GameState gameState) {
        // Apply momentum if we're not currently dragging and we have momentum left
        if (!dragging && momentumVelocity.magnitude() > 0.1f &&
                System.currentTimeMillis() - momentumStartTime <= MOMENTUM_DURATION) {

            // Apply momentum to camera
            gameState.setCameraX(gameState.getCameraX() - (int) momentumVelocity.x);
            gameState.setCameraY(gameState.getCameraY() - (int) momentumVelocity.y);

            // Decay momentum over time
            momentumVelocity.scale(MOMENTUM_DECAY);
        } else if (!dragging) {
            // Reset momentum once negligible or time expired
            momentumVelocity.copy(0, 0, 0);
        }
    }
}