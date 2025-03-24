package main.game.systems;

import javafx.application.Platform;
import main.game.camera.CameraHandler;
import main.game.components.IdentityComponent;
import main.game.components.tile.Tile;
import main.game.main.GameState;
import main.game.components.SecondTimer;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.input.*;
import main.logging.EmeritusLogger;
import main.ui.game.JavaFxUtils;
import org.json.JSONObject;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int speed = 128;
    private final Vector3f selected = new Vector3f();
    private final SecondTimer selectionTimer = new SecondTimer();
    private final EmeritusLogger mLogger = EmeritusLogger.create(InputHandler.class);

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

    private void handleCamera(GameState gameState, CameraHandler cameraHandler, InputController controls) {
        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();
        Vector3f currentMousePosition = mouse.getPosition();
        boolean mouseButtonIsBeingHeldDown = mouse.isButtonBeingHeldDown();


//        boolean hasTileToGlideTo = gameState.hasTileToGlideTo();
//        if (hasTileToGlideTo) {
//            String tileToGlideToID = gameState.getTileToGlideToID();
//            String cameraToGlideWith = gameState.getTileToGlideToCamera();
//            Entity tileEntity = EntityStore.getInstance().get(tileToGlideToID);
//            Tile tile = tileEntity.get(Tile.class);
//            cameraHandler.glide(gameState, cameraToGlideWith, tile);
//            gameState.clearTileToGlideTo();
//            mLogger.info("Gliding {} camera to new tile {}", cameraToGlideWith, tileToGlideToID);
//        }

        JSONObject tilesToGlideTo = gameState.consumeTilesToGlideTo();
        if (tilesToGlideTo != null) {
            for (String key : tilesToGlideTo.keySet()) {
                JSONObject tileToGlideToData = tilesToGlideTo.getJSONObject(key);
                String tileToGlideToID = tileToGlideToData.getString("id");
                String cameraToGlideWith = tileToGlideToData.getString("camera");
                Entity tileEntity = EntityStore.getInstance().get(tileToGlideToID);
                Tile tile = tileEntity.get(Tile.class);
                cameraHandler.glide(gameState, cameraToGlideWith, tile);
                mLogger.info("Gliding {} camera to new tile {}", cameraToGlideWith, tileToGlideToID);
            }
        }

        cameraHandler.drag(gameState, currentMousePosition, mouseButtonIsBeingHeldDown);

        if (keyboard.isPressed()){
            boolean cornering = false;
            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
            if (cornering) { cameraHandler.glide(gameState, selected); }
        }
    }

    public void input(GameState gameState, CameraHandler cameraHandler, InputController controls, GameModel model) {

        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();

        handleCamera(gameState, cameraHandler, controls);

        Entity hoveredTile = model.tryFetchingMousedAtTileEntity();
        if (hoveredTile != null) {
            Tile tile = hoveredTile.get(Tile.class);
            IdentityComponent identityComponent = hoveredTile.get(IdentityComponent.class);
            model.getGameState().setHoveredTiles(identityComponent.getID());
        }


        if (keyboard.isPressed(KeyEvent.VK_9)) {
            int newSpriteWidth = (int) (gameState.getSpriteWidth() * .8);
            int newSpriteHeight = (int) (gameState.getSpriteHeight() * .8);
            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
//            System.out.println(gameState.getSpriteWidth() + ", " + gameState.getSpriteHeight() + " GETTING SMALLER " + Platform.isFxApplicationThread());
            return;
        }

        if (keyboard.isPressed(KeyEvent.VK_0)) {
            int newSpriteWidth = (int) (gameState.getSpriteWidth() * 1.2);
            int newSpriteHeight = (int) (gameState.getSpriteHeight() * 1.2);
            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
//            System.out.println(gameState.getSpriteWidth() + ", " + gameState.getSpriteHeight() + " GETTING BIGGER " + Platform.isFxApplicationThread());
            return;
        }

//        System.out.println(gameState.getSpriteWidth() + ", " + gameState.getSpriteHeight() + " = " + gameState.getConfigurableStateGameplayHudIsVisible() + " " + gameState.hashCode());


        if (mouse.isButtonBeingHeldDown()) {
//            camera.drag(gameState, currentMousePosition, true);
//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());

//            camera.drag(gameState, currentMousePosition, mouse.isButtonBeingHeld());
//            camera.drag(gameState, currentMousePosition, mouse.isHeld());

            Entity selected = model.tryFetchingMousedAtTileEntity();
            if (selected == null) { return; }

            boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
            if (mouse.isLeftButtonPressed() && !isActionPanelOpen) {
                Tile tile = selected.get(Tile.class);
//                model.getGameState().setSelectedTiles(tile);
                IdentityComponent identityComponent = selected.get(IdentityComponent.class);
                model.getGameState().setSelectedTileIDs(identityComponent.getID());
            }
        } else {

            boolean cornering = false;
            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
            if (cornering) { cameraHandler.glide(gameState, selected); }
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
