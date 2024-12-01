package main.game.camera;


import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.main.GameState;

import java.util.Random;

public class Camera {

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

    public void glide(GameState gameStateV2, Tile tile) {
        Vector3f toGlideTo = tile.getLocalVector(gameStateV2);
        glide(gameStateV2, toGlideTo);
    }

    public void glide(GameState gameStateV2, Vector3f toGlideTo) {
        // Transition to GLIDING
        currently = Movement.GLIDING;

        // Sync the current camera position
        syncCurrentPosition(gameStateV2);

        // Set starting position for glide
        mStartCameraPosition.copy(mCurrentCameraPosition);

        // Calculate the target position
        int extraY = gameStateV2.getSpriteHeight();
        int extraX = gameStateV2.getSpriteWidth();
        mEndCameraPosition.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void drag(GameState gameStateV2, Vector3f currentMousePosition, boolean isMouseBeingHeld) {
        // Transition to DRAGGING
        currently = Movement.DRAGGING;

        // Sync the current camera position on the first frame of dragging
        if (!isMouseBeingHeld) {
            syncCurrentPosition(gameStateV2);
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

        int currentX = gameStateV2.getCameraX();
        int currentY = gameStateV2.getCameraY();
        currentX += (int) difference.x;
        currentY += (int) difference.y;

        gameStateV2.setCameraX(currentX);
        gameStateV2.setCameraY(currentY);
    }

    public void update(GameState gameStateV2) {
        if (currently == Movement.GLIDING) {
            glideToPosition(gameStateV2);
        }
    }

    private void glideToPosition(GameState gameStateV2) {
        // Calculate the intermediate position for a smooth glide
        int spriteWidth = gameStateV2.getSpriteWidth();
        int spriteHeight = gameStateV2.getSpriteHeight();
        int width = gameStateV2.getViewportWidth();
        int height = gameStateV2.getViewportHeight();

        int targetX = (int) (-mEndCameraPosition.x + (width / 2)) + spriteWidth;
        int targetY = (int) (-mEndCameraPosition.y + (height / 2)) + spriteHeight;

        int previousX = gameStateV2.getCameraX();
        int previousY = gameStateV2.getCameraY();

//        int currentX = (int) (previousX + ((-targetX - previousX) * 0.05f));
//        int currentY = (int) (previousY + ((-targetY - previousY) * 0.05f));

        int currentX = (int) (previousX + ((-targetX - previousX) * mRandom.nextFloat(0.02f, 0.05f)));
        int currentY = (int) (previousY + ((-targetY - previousY) * mRandom.nextFloat(0.02f, 0.05f)));

        gameStateV2.setCameraX(currentX);
        gameStateV2.setCameraY(currentY);

        // Sync the current position
        mCurrentCameraPosition.copy(-currentX, -currentY);

        // Check if glide is complete
        if (Math.abs(currentX - targetX) < 1 && Math.abs(currentY - targetY) < 1) {
            currently = Movement.STATIONARY;
        }
    }

    private void syncCurrentPosition(GameState gameStateV2) {
        mCurrentCameraPosition.copy(gameStateV2.getCameraX(), gameStateV2.getCameraY());
    }
}