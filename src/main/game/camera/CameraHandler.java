package main.game.camera;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.main.GameState;

public class CameraHandler {
    private final CameraPositionHandler dragHandler = new DragPositionHandler();
    private final CameraPositionHandler glideHandler = new GlidePositionHandler();
    private CameraPositionHandler currentHandler = null;

    public void glide(GameState gameState, Tile tile) {
        Vector3f tilePosition = tile.getLocalVector(gameState);
        glideHandler.handle(gameState, tilePosition, true);
        currentHandler = glideHandler;
    }

    public void glide(GameState gameState, Vector3f toPosition) {
        glideHandler.handle(gameState, toPosition, true);
        currentHandler = glideHandler;
    }

    public void drag(GameState gameState, Vector3f toPosition, boolean isMouseButtonDown) {
        dragHandler.handle(gameState, toPosition, isMouseButtonDown);
        if (currentHandler == glideHandler && !isMouseButtonDown) { return; }
        currentHandler = dragHandler;
    }

    public void update(GameState gameState) {
        if (currentHandler == null) { return; }
        currentHandler.update(gameState);
//        dragHandler.update(gameState);
//        glideHandler.update(gameState);
    }
}