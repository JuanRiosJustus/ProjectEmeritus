package main.game.camera;

import main.constants.Vector3f;
import main.game.main.GameState;

public abstract class CameraPositionHandler {

    public abstract void handle(GameState gameState, Vector3f toPosition, boolean isMouseBeingPressed);

    public abstract void handle(GameState gameState, String camera, Vector3f toPosition, boolean isMouseBeingPressed);
    public abstract void update(GameState gameState);
}
