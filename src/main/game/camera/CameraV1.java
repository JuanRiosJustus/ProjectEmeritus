package main.game.camera;


import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.main.GameState;

import java.util.Random;

public class CameraV1 {

    public enum Movement {
        DRAGGING,
        GLIDING,
        SETTING,
        STATIONARY
    }

    private final Vector3f mEndCameraPosition = new Vector3f();
    private final Vector3f mStartCameraPosition = new Vector3f();
    private final Vector3f mCurrentCameraPosition = new Vector3f();
    private final Random mRandom = new Random();
    private Movement currently = Movement.SETTING;

    public void glide(GameState gameState, Tile tile) {
        Vector3f toGlideTo = tile.getLocalVector(gameState);
        glide(gameState, toGlideTo);
    }

    public void glide(GameState gameState, Vector3f toGlideTo) {
        // Transition to GLIDING
        currently = Movement.GLIDING;

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
        // Transition to DRAGGING
        currently = Movement.DRAGGING;

        // Sync the current camera position on the first frame of dragging
        if (!isMouseBeingHeld) {
            syncCurrentPosition(gameState);
        }

        // Update drag state
        if (isMouseBeingHeld) {
            mEndCameraPosition.copy(currentMousePosition);
        } else {
            mEndCameraPosition.copy(mStartCameraPosition);
        }

        mStartCameraPosition.copy(currentMousePosition);

        // Calculate the difference and apply to camera position
        Vector3f difference = new Vector3f(
                mEndCameraPosition.x - mStartCameraPosition.x,
                mEndCameraPosition.y - mStartCameraPosition.y
        );

        int currentX = gameState.getCameraX();
        int currentY = gameState.getCameraY();
        currentX += (int) difference.x;
        currentY += (int) difference.y;

        gameState.setCameraX(currentX);
        gameState.setCameraY(currentY);
    }

    public void update(GameState gameStateV2) {
        if (currently == Movement.GLIDING) {
            glideToPosition(gameStateV2);
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
            currently = Movement.STATIONARY;
        }
    }

    private void syncCurrentPosition(GameState gameState) {
        mCurrentCameraPosition.copy(gameState.getCameraX(), gameState.getCameraY());
    }
}