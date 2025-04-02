package main.game.events;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.main.GameState;
import main.game.stores.factories.EntityStore;
import main.game.systems.GameSystem;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CameraSystem extends GameSystem {
    private String mLastCameraMovementEvent = "";

    private final Vector3f previousMousePosition = new Vector3f(); // Last recorded mouse position
    private final Vector3f momentumVelocity = new Vector3f();     // Velocity for momentum after drag ends

    private long lastUpdateTime = 0L;    // For throttling updates
    private long momentumStartTime = 0L; // When momentum starts

    private static final long UPDATE_DELAY = 16L;        // Throttle drag updates for smoothness
    private static final float MOMENTUM_DECAY = 0.95f;   // Decay factor for momentum over time
    private static final long MOMENTUM_DURATION = 5000L; // Momentum lasts 3 seconds after release
    private static final float MINIMUM_MOMENTUM = 2.0f;  // Minimum momentum to ensure some noticeable movement

    private boolean dragging = false;
    private static final float GLIDE_EASING = 0.05f;         // Interpolation factor for gliding
    private final Map<String, Vector3f> mCamerasToMove = new ConcurrentHashMap<>();

    public static final String CAMERA_GLIDE = "camera_zoom";
    public static final String CAMERA_DRAG = "camera_drag";
    public CameraSystem(GameModel gameModel) {
        super(gameModel);

        mEventBus.subscribe(CAMERA_GLIDE, this::onCameraGlideEvent);
        mEventBus.subscribe(CAMERA_DRAG, this::onCameraDragEvent);
    }

    public static JSONObject createCameraGlideEvent(String camera, String tileID) {
        return createCameraGlideEvent(camera, tileID, -1, -1);
    }
    public static JSONObject createCameraGlideEvent(String camera, int x, int y) {
        return createCameraGlideEvent(camera, "", x, y);
    }
    private static JSONObject createCameraGlideEvent(String camera, String tileID, int x, int y) {
        JSONObject event = new JSONObject();
        event.put("camera", camera);
        event.put("tile_id", tileID);
        event.put("x", x);
        event.put("y", y);
        return event;
    }
    private void onCameraGlideEvent(JSONObject event) {
        mLastCameraMovementEvent = CAMERA_GLIDE;
        String cameraToGlideWith = event.getString("camera");
        String tileToGlideToID = event.optString("tile_id", "");
        int positionX = event.optInt("x");
        int positionY = event.optInt("y");

        Vector3f tilePosition = new Vector3f(positionX, positionY);
        if (!tileToGlideToID.isEmpty()) {
            Entity tileEntity = EntityStore.getInstance().get(tileToGlideToID);
            Tile tile = tileEntity.get(Tile.class);
            tilePosition = tile.getLocalVector(mGameState);
        }

        handleGlideEvent(mGameState, cameraToGlideWith, tilePosition);
    }



    private void handleGlideEvent(GameState gameState, String camera, Vector3f toPosition) {
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

    public static JSONObject createCameraDragEvent(int x, int y, boolean isMouseBeingHeldDown) {
        JSONObject event = new JSONObject();
        event.put("x", x);
        event.put("y", y);
        event.put("is_mouse_being_held_down", isMouseBeingHeldDown);
        return event;
    }
    private void onCameraDragEvent(JSONObject event) {
        mLastCameraMovementEvent = CAMERA_DRAG;
        int positionX = event.getInt("x");
        int positionY = event.getInt("y");
        boolean isMouseBeingHeldDown = event.getBoolean("is_mouse_being_held_down");

        handleDragEvent(mGameState, new Vector3f(positionX, positionY), isMouseBeingHeldDown);
    }

    private void handleDragEvent(GameState gameState, Vector3f currentMousePosition, boolean isMouseBeingPressed) {
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
        gameState.setMainCameraX(gameState.getMainCameraX() - (int) dragOffset.x);
        gameState.setMainCameraY(gameState.getMainCameraY() - (int) dragOffset.y);

        // Ensure minimum momentum for a noticeable release movement
        if (dragOffset.magnitude() < MINIMUM_MOMENTUM) {
            dragOffset.normalize().scale(MINIMUM_MOMENTUM);
        }

        // Update momentum velocity for after the user releases the mouse
        momentumVelocity.copy(dragOffset);

        // Update the previous mouse position for the next calculation
        previousMousePosition.copy(currentMousePosition);
    }

    @Override
    public void update(GameModel model, String id) {
        if (mLastCameraMovementEvent.equals(CAMERA_DRAG)) {
            handleCameraDragUpdate();
        } else if (mLastCameraMovementEvent.equals(CAMERA_GLIDE)) {
            handleCameraGlideUpdate();
        }
    }

    private void handleCameraDragUpdate() {
        GameState gameState = mGameState;
        // Apply momentum if we're not currently dragging and we have momentum left
        if (!dragging && momentumVelocity.magnitude() > 0.1f &&
                System.currentTimeMillis() - momentumStartTime <= MOMENTUM_DURATION) {

            // Apply momentum to camera
            gameState.setMainCameraX(gameState.getMainCameraX() - (int) momentumVelocity.x);
            gameState.setMainCameraY(gameState.getMainCameraY() - (int) momentumVelocity.y);

            // Decay momentum over time
            momentumVelocity.scale(MOMENTUM_DECAY);
        } else if (!dragging) {
            // Reset momentum once negligible or time expired
            momentumVelocity.copy(0, 0, 0);
        }
    }

    private void handleCameraGlideUpdate() {
        GameState gameState = mGameState;
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
