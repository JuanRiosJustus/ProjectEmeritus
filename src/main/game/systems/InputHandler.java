package main.game.systems;

import main.constants.HashSlingingSlasher;
import main.game.components.*;
import main.game.components.tile.TileComponent;
import main.game.main.GameState;
import main.constants.SecondTimer;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.input.*;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.awt.event.KeyEvent;


public class InputHandler {

    private final int speed = 128;
    private final Vector3f selected = new Vector3f();
    private final SecondTimer selectionTimer = new SecondTimer();
    private final HashSlingingSlasher mHoveredTileHashSlingingSlasher = new HashSlingingSlasher();
    private final HashSlingingSlasher mAbilityMonitor = new HashSlingingSlasher();
    private final HashSlingingSlasher mMovementMonitor = new HashSlingingSlasher();
    private final EmeritusLogger mLogger = EmeritusLogger.create(InputHandler.class);
    private Vector3f mLastMousePosition = new Vector3f();
    private boolean mPreviousMouseButtonIsBeingHeldDownState = false;
    private int mConsecutiveSameStateFrames = 0;
    private JSONEventBus mEventBus = null;

    public InputHandler(JSONEventBus eventBus) { mEventBus = eventBus; }

//    private void handleCamera(GameState gameState, CameraHandler cameraHandler, InputControllerV1 controls, GameModel model) {
//        Keyboard keyboard = controls.getKeyboard();
//        MouseV1 mouseV1 = controls.getMouse();
//        Vector3f currentMousePosition = mouseV1.getPosition();
//        boolean mouseButtonIsBeingHeldDown = mouseV1.isButtonBeingHeldDown();
//
//
//        boolean hasTileToGlideTo = gameState.hasTileToGlideTo();
//        if (hasTileToGlideTo) {
//            String currentTileID = gameState.getTileToGlideTo();
//            Entity tileEntity = EntityStore.getInstance().get(currentTileID);
//            Tile tile = tileEntity.get(Tile.class);
//            cameraHandler.glide(gameState, tile);
//            gameState.setTileToGlideTo(null);
//            System.out.println("Setting Glide");
//        }
//
//        cameraHandler.drag(gameState, currentMousePosition, mouseButtonIsBeingHeldDown);
//
//        if (keyboard.isPressed()){
//            boolean cornering = false;
//            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
//            if (cornering) { cameraHandler.glide(gameState, selected); }
//        }
//    }

    private void handleCamera(GameState gameState, InputController controls) {
        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();
        Vector3f currentMousePosition = mouse.getPosition();
        boolean mouseButtonIsBeingHeldDown = mouse.isBeingHeld();

        JSONObject tilesToGlideTo = gameState.consumeTilesToGlideTo();
        if (tilesToGlideTo != null) {
            if (tilesToGlideTo.size() > 1) {
//                System.out.println("yoo");
            }
            for (String key : tilesToGlideTo.keySet()) {
                JSONObject tileToGlideToData = tilesToGlideTo.getJSONObject(key);
                String tileToGlideToID = tileToGlideToData.getString("id");
                String cameraToGlideWith = tileToGlideToData.getString("camera");
                mEventBus.publish(CameraSystem.CAMERA_GLIDE, CameraSystem.createCameraGlideEvent(
                        key, tileToGlideToID
                ));
                mLogger.info("Gliding {} camera to new tile {}", cameraToGlideWith, tileToGlideToID);

            }
        }

        tryHandlingMouseDraggedEvent(mouseButtonIsBeingHeldDown, currentMousePosition);
        tryHandlingKeyBoardPressedEvent(gameState, keyboard);

        mLastMousePosition = currentMousePosition;
        mPreviousMouseButtonIsBeingHeldDownState = mouseButtonIsBeingHeldDown;
    }

    private void tryHandlingKeyBoardPressedEvent(GameState gameState, Keyboard keyboard) {
        if (!keyboard.isPressed()) { return; }
        boolean cornering = false;
        if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
        if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
        if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
        if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
        if (cornering) {
            mEventBus.publish(CameraSystem.CAMERA_GLIDE, CameraSystem.createCameraGlideEvent(
                    gameState.getMainCameraID(), (int) selected.x, (int) selected.y
            ));
        }
    }

    private void tryHandlingMouseDraggedEvent(boolean mouseButtonIsBeingHeldDown, Vector3f currentMousePosition) {

        if (mPreviousMouseButtonIsBeingHeldDownState == mouseButtonIsBeingHeldDown) { mConsecutiveSameStateFrames++;
        } else { mConsecutiveSameStateFrames = 0; }
        if (mConsecutiveSameStateFrames > 3 && !mouseButtonIsBeingHeldDown) {
            return;
        }


        mEventBus.publish(CameraSystem.CAMERA_DRAG, CameraSystem.createCameraDragEvent(
                (int) currentMousePosition.x,
                (int) currentMousePosition.y,
                mouseButtonIsBeingHeldDown
        ));
    }

    public void input(GameState gameState, InputController controls, GameModel model) {

        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();

        boolean isPressed = mouse.isPressed();

        boolean isMovementPanelOpen = model.getGameState().isMovementPanelOpen();
        boolean isAbilityPanelOpen = model.getGameState().isAbilityPanelOpen();



        String currentEntityID = model.getSpeedQueue().peek();
        Entity currentEntity = EntityStore.getInstance().get(currentEntityID);

        String hoveredTileID = model.tryFetchingMousedAtTileID();
        Entity hoveredTileEntity = EntityStore.getInstance().get(hoveredTileID);
        boolean isHoveredTileChanged = mHoveredTileHashSlingingSlasher.setOnDifference(hoveredTileEntity);

        ActionsComponent actionsComponent = currentEntity == null ? null : currentEntity.get(ActionsComponent.class);
        AIComponent aiComponent = currentEntity == null ? null : currentEntity.get(AIComponent.class);
        boolean isUserCharacter = actionsComponent != null && aiComponent != null && aiComponent.isUserControlled();

        String selectedAbility = null;
        if (currentEntity != null) {
            selectedAbility = currentEntity.get(AbilityComponent.class).getAbility();
        }


        handleCamera(gameState, controls);



//        String mousedAtTileId = model.tryFetchingMousedAtTileID();
//        if (mousedAtTileId != null) {
//            gameState.setHoveredTiles(mousedAtTileId);
//        }



        Entity hoveredTile = model.tryFetchingMousedAtTileEntity();
        if (hoveredTile != null) {
            TileComponent tile = hoveredTile.get(TileComponent.class);
            IdentityComponent identityComponent = hoveredTile.get(IdentityComponent.class);
//            model.getGameState().setHoveredTiles(identityComponent.getID());
            model.getGameState().setHoveredTile(identityComponent.getID());


            int mapEditorCursorSize = model.getGameState().getMapEditorCursorSize();
            if (mapEditorCursorSize > 1) {
                JSONArray newHovered = new JSONArray();
                for (int row = tile.getRow() - mapEditorCursorSize ; row <= tile.getRow() + mapEditorCursorSize; row++) {
                    for (int column = tile.getColumn() - mapEditorCursorSize; column <= tile.getColumn() + mapEditorCursorSize; column++) {
                        String nextTileID = model.tryFetchingTileEntityID(row, column);
                        if (nextTileID == null) {
                            continue;
                        }
                        newHovered.add(nextTileID);
                    }
                }
                model.getGameState().setMapEditorHoveredTiles(newHovered);
            }
        } else {
            model.getGameState().setHoveredTile(null);
        }


        if (keyboard.isPressed(KeyEvent.VK_9)) {
            JSONObject cameraZoomData = model.getViewportData();
            float zoom = cameraZoomData.getFloatValue("zoom") * .8f;
            model.setViewportZoom(new JSONObject().fluentPut("zoom", zoom));
//            System.out.println(gameState.getSpriteWidth() + ", " + gameState.getSpriteHeight() + " GETTING SMALLER " + Platform.isFxApplicationThread());
            return;
        }

        if (keyboard.isPressed(KeyEvent.VK_0)) {
            JSONObject cameraZoomData = model.getViewportData();
            float zoom = cameraZoomData.getFloatValue("zoom") * 1.2f;
            model.setViewportZoom(new JSONObject().fluentPut("zoom", zoom));
//            System.out.println(gameState.getSpriteWidth() + ", " + gameState.getSpriteHeight() + " GETTING BIGGER " + Platform.isFxApplicationThread());
            return;
        }


        // Handles Unit Movement. This triggers 2x TODO, make sure only triggers once
        boolean shouldPublish = mMovementMonitor.setOnDifference(isMovementPanelOpen, isHoveredTileChanged, isPressed);
        if (isMovementPanelOpen && shouldPublish && isUserCharacter) {

            JSONObject request = new JSONObject();
            request.put("unit_id", currentEntityID);
            request.put("tile_id", hoveredTileID);
            request.put("commit", isPressed);

            model.useMove(request);

//            mEventBus.publish(MovementSystem.createMoveEntityEvent(currentEntityID, hoveredTileID, isPressed));
            return;
        }

        shouldPublish = mAbilityMonitor.setOnDifference(isAbilityPanelOpen, isHoveredTileChanged, isPressed, selectedAbility);
        if (isAbilityPanelOpen && shouldPublish && isUserCharacter) {
            AbilityComponent abilityComponent = currentEntity.get(AbilityComponent.class);
            String abilitySelected = abilityComponent.getAbility();

            JSONObject request = new JSONObject();
            request.put("unit_id", currentEntityID);
            request.put("ability", abilitySelected);
            request.put("tile_id", hoveredTileID);
            request.put("commit", isPressed);

            model.useAbility(request);

//            mEventBus.publish(AbilitySystem.createUseAbilityEvent(currentEntityID, abilitySelected, hoveredTileID, isPressed));
            return;
        }


        if (mouse.isBeingHeld()) {


//            camera.drag(gameState, currentMousePosition, true);
//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());

//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
//            camera.drag(gameState, currentMousePosition, mouse.isHeld());

            Entity selected = model.tryFetchingMousedAtTileEntity();
            if (selected == null) { return; }

            boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
            if (mouse.isLeftButtonPressed() && !isActionPanelOpen) {
                TileComponent tile = selected.get(TileComponent.class);
                IdentityComponent identityComponent = selected.get(IdentityComponent.class);
                model.getGameState().setSelectedTileIDs(identityComponent.getID());
            }
        } else {
//
//            boolean cornering = false;
//            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
//            if (cornering) { cameraHandler.glide(gameState, selected); }
        }
    }

//    public void input(GameState gameState, CameraHandler cameraHandler, InputControllerV1 controls, GameModel model) {
//
//        Keyboard keyboard = controls.getKeyboard();
//        MouseV1 mouseV1 = controls.getMouse();
//
//
//        handleCamera(gameState, cameraHandler, controls, model);
//
//
//        Entity hoveredTile = model.tryFetchingMousedAtTileEntity();
//        if (hoveredTile != null) {
//            Tile tile = hoveredTile.get(Tile.class);
//            IdentityComponent identityComponent = hoveredTile.get(IdentityComponent.class);
//            model.getGameState().setHoveredTiles(identityComponent.getID());
//        }
//
//
//        if (keyboard.isPressed(KeyEvent.VK_9)) {
//            int newSpriteWidth = (int) (gameState.getSpriteWidth() * .8);
//            int newSpriteHeight = (int) (gameState.getSpriteHeight() * .8);
//            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
//            System.out.println("GETTING SMALLER");
//            return;
//        }
//
//        if (keyboard.isPressed(KeyEvent.VK_0)) {
//            int newSpriteWidth = (int) (gameState.getSpriteWidth() * 1.2);
//            int newSpriteHeight = (int) (gameState.getSpriteHeight() * 1.2);
//            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
//            System.out.println("GETTING BIGGER");
//            return;
//        }
//
//
//        if (mouseV1.isButtonBeingHeldDown()) {
////            camera.drag(gameState, currentMousePosition, true);
////            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
////            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
//
////            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
////            camera.drag(gameState, currentMousePosition, mouse.isHeld());
//
//            Entity selected = model.tryFetchingMousedAtTileEntity();
//            if (selected == null) { return; }
//
//            boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
//            if (mouseV1.isLeftButtonPressed() && !isActionPanelOpen) {
//                Tile tile = selected.get(Tile.class);
////                model.getGameState().setSelectedTiles(tile);
//                IdentityComponent identityComponent = selected.get(IdentityComponent.class);
//                model.getGameState().setSelectedTiles(identityComponent.getID());
//            }
//        } else {
//
//            boolean cornering = false;
//            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
//            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
//            if (cornering) { cameraHandler.glide(gameState, selected); }
//        }
//    }
}
