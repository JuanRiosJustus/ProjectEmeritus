package main.game.camera;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.main.GameState;

import java.util.Random;

public class CameraV2 {

    public enum Movement {
        DRAGGING,
        GLIDING,
        SETTING,
        STATIONARY
    }

    private final Vector3f mEndCameraPosition = new Vector3f();
    private final Vector3f mStartCameraPosition = new Vector3f();
    private final Vector3f mCurrentCameraPosition = new Vector3f();
    private Movement currently = Movement.SETTING;

    private long lastDragUpdateTime = 0L;
    private final long DRAG_UPDATE_DELAY = 8L; // Delay for smoother updates

    private final Random mRandom = new Random();

    public void glide(GameState gameState, Tile tile) {
        Vector3f toGlideTo = tile.getLocalVector(gameState);
        glide(gameState, toGlideTo);
    }

//    public void glide(GameState gameState, Vector3f toGlideTo) {
//        currently = Movement.GLIDING;
//        syncCurrentPosition(gameState);
//        mStartCameraPosition.copy(mCurrentCameraPosition);
//        mEndCameraPosition.copy(toGlideTo);
//    }

    public void glide(GameState gameState, Vector3f toGlideTo) {
        // Transition to GLIDING
        currently = CameraV2.Movement.GLIDING;

        // Sync the current camera position
        syncCurrentPosition(gameState);

        // Set starting position for glide
        mStartCameraPosition.copy(mCurrentCameraPosition);

        // Calculate the target position
        int extraY = gameState.getSpriteHeight();
        int extraX = gameState.getSpriteWidth();
        mEndCameraPosition.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }


    public void drag(GameState gameState, Vector3f currentMousePosition, boolean isMouseBeingHeld) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDragUpdateTime < DRAG_UPDATE_DELAY) {
            return; // Skip update if not enough time has passed
        }
        lastDragUpdateTime = currentTime;

        if (!isMouseBeingHeld) {
            // Initialize positions at the start of dragging
            syncCurrentPosition(gameState);
            mStartCameraPosition.copy(currentMousePosition);
            mEndCameraPosition.copy(mCurrentCameraPosition);
            return; // Skip further processing on the first frame
        }

        // Calculate the offset based on mouse movement
        Vector3f dragOffset = new Vector3f(
                currentMousePosition.x - mStartCameraPosition.x,
                currentMousePosition.y - mStartCameraPosition.y
        );

        // Apply easing for smoother dragging
        float easingFactor = 0.04f; // Adjust this value for smoothness (0 = no movement, 1 = immediate snapping)
        int currentX = gameState.getCameraX();
        int currentY = gameState.getCameraY();

        int targetX = (int) (mEndCameraPosition.x - dragOffset.x);
        int targetY = (int) (mEndCameraPosition.y - dragOffset.y);

        int easedX = (int) (currentX + (targetX - currentX) * easingFactor);
        int easedY = (int) (currentY + (targetY - currentY) * easingFactor);

        // Update the camera's position
        gameState.setCameraX(easedX);
        gameState.setCameraY(easedY);

        // Sync the updated position
        syncCurrentPosition(gameState);

        // Update the movement state
        currently = Movement.DRAGGING;
    }

    public void update(GameState gameState) {
        if (currently == Movement.GLIDING) {
            glideToPosition(gameState);
        }
    }

    private void glideToPosition(GameState gameState) {
        // Calculate the intermediate position for a smooth glide
        int spriteWidth = gameState.getSpriteWidth();
        int spriteHeight = gameState.getSpriteHeight();
        int width = gameState.getViewportWidth();
        int height = gameState.getViewportHeight();

        int targetX = (int) (-mEndCameraPosition.x + (width / 2)) + spriteWidth;
        int targetY = (int) (-mEndCameraPosition.y + (height / 2)) + spriteHeight;

        int previousX = gameState.getCameraX();
        int previousY = gameState.getCameraY();

        int currentX = (int) (previousX + ((-targetX - previousX) * mRandom.nextFloat(0.02f, 0.05f)));
        int currentY = (int) (previousY + ((-targetY - previousY) * mRandom.nextFloat(0.02f, 0.05f)));

        gameState.setCameraX(currentX);
        gameState.setCameraY(currentY);

        // Sync the current position
        mCurrentCameraPosition.copy(-currentX, -currentY);

        // Check if glide is complete
        if (Math.abs(currentX - targetX) < 1 && Math.abs(currentY - targetY) < 1) {
            currently = CameraV2.Movement.STATIONARY;
        }
    }

    private void syncCurrentPosition(GameState gameState) {
        mCurrentCameraPosition.copy(gameState.getCameraX(), gameState.getCameraY());
    }
}