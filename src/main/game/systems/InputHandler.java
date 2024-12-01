package main.game.systems;

import main.game.camera.Camera;
import main.game.components.tile.Tile;
import main.game.main.GameState;
import main.game.components.SecondTimer;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.input.InputController;
import main.input.Keyboard;
import main.input.Mouse;
import org.json.JSONObject;

import java.awt.event.KeyEvent;

public class InputHandler {

    private final int speed = 128;
    private final Vector3f selected = new Vector3f();
    private final SecondTimer selectionTimer = new SecondTimer();

    public void input(GameState gameState, Camera camera, InputController controls, GameModel model) {
//        System.out.println("CAMERA POSITION " + Camera.getInstance().getVector());

        JSONObject potentialTile = gameState.getTileToGlideTo();
        if (potentialTile instanceof Tile tile) {
            camera.glide(gameState, tile);
            gameState.setTileToGlideTo(null);
        }


        Entity hoveredTile = model.tryFetchingTileMousedAt();
        if (hoveredTile != null) {
            Tile tile = hoveredTile.get(Tile.class);
            model.getGameState().setHoveredTiles(tile);
        }


        Keyboard keyboard = controls.getKeyboard();
        Mouse mouse = controls.getMouse();
        Vector3f currentMousePosition = mouse.getPosition();
        boolean isMousePressed = mouse.isPressed();


        if (keyboard.isPressed(KeyEvent.VK_9)) {
            int newSpriteWidth = (int) (gameState.getSpriteWidth() * .8);
            int newSpriteHeight = (int) (gameState.getSpriteHeight() * .8);
            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
            System.out.println("GETTING SMALLER");
        }

        if (keyboard.isPressed(KeyEvent.VK_0)) {
            int newSpriteWidth = (int) (gameState.getSpriteWidth() * 1.2);
            int newSpriteHeight = (int) (gameState.getSpriteHeight() * 1.2);
            gameState.setSpriteWidth(newSpriteWidth).setSpriteHeight(newSpriteHeight);
            System.out.println("GETTING BIGGER");
        }

        if (mouse.isHeld()) {
            camera.drag(gameState, currentMousePosition, isMousePressed);

            Entity selected = model.tryFetchingTileMousedAt();
            if (selected == null) { return; }
            // Disable rapid clicks that some mouses have??
            if (selectionTimer.elapsed() >= .2) {
                // Store the previous state
//                Entity currentlySelected = model.getGameState().getCurrentlySelectedTileEntity();
//                model.mGameState.put(GameState.PREVIOUSLY_SELECTED, currentlySelected);
                boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
                if (mouse.isLeftButtonPressed() && !isActionPanelOpen) {
//                    model.getGameState().setSelectedEntity(selected);
                    Tile tile = selected.get(Tile.class);
                    model.getGameState().setSelectedTiles(tile);
                } else if (mouse.isRightButtonPressed()) {
//                    model.getGameState().setSelectedEntity(null);
//                    model.setSelectedTile(null);
//                    model.getGameState().setSelectedTile(null);
                }
                selectionTimer.reset();
            }
        } else {

            boolean cornering = false;
            if (keyboard.isPressed(KeyEvent.VK_A)) { selected.x -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_D)) { selected.x += speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_W)) { selected.y -= speed; cornering = true; }
            if (keyboard.isPressed(KeyEvent.VK_S)) { selected.y += speed; cornering = true; }
            if (cornering) { camera.glide(gameState, selected); }
        }
    }
}
