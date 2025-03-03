package main.game.main.rendering;

import com.sun.javafx.scene.paint.GradientUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

public abstract class RendererV2 {
    protected RendererUtilsV2 mRendererUtils = new RendererUtilsV2();

    private final Point mEphemeralPoint = new Point();
    public abstract void render(GraphicsContext gc, GameModel gm, RenderContext rc);

    public Entity getEntityWithID(String entityID) { return EntityStore.getInstance().get(entityID); }
    /**
     * Calculates the drawing position for an image to align its bottom with the tile's bottom
     * and center it horizontally on the tile.
     *
     * @param model the game model containing global positioning details
     * @param tile the tile to align the image with
     * @param image the image to be drawn
     * @return the calculated drawing position as a Point
     */
    public Point calculateWorldPosition(GameModel model, Tile tile, Image image) {
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        int localX = tile.getColumn() * configuredSpriteWidth;
        int localY = tile.getRow() * configuredSpriteHeight;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        return calculateWorldPosition(model, localX, localY, width, height);
    }

    public Point calculateWorldPosition(GameModel model, Tile tile) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        int x = tile.getColumn() * width;
        int y = tile.getRow() * height;
        return calculateWorldPosition(model, x, y, width, height);
    }
    public Point calculateWorldPosition(GameModel model, int localX, int localY, Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        return calculateWorldPosition(model, localX, localY, width, height);
    }

    public Point calculateWorldPosition(GameModel model, int localX, int localY, int width, int height) {
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        // Calculate tile's center X in global coordinates
        int tileCenterX = model.getGameState().getGlobalX(localX) + (configuredSpriteWidth / 2);

        // Calculate tile's bottom Y in global coordinates
        int tileBottomY = model.getGameState().getGlobalY(localY) + configuredSpriteHeight;

        // Calculate drawX (center image horizontally)
        int drawX = tileCenterX - (width / 2);

        // Calculate drawY (align image bottom with tile bottom)
        int drawY = tileBottomY - height;

        // Use the reusable Point object (mEphemeralPoint)
        mEphemeralPoint.x = drawX;
        mEphemeralPoint.y = drawY;
        return mEphemeralPoint;
    }

    public Point calculateWorldPositionRaw(GameModel model, int localX, int localY, int width, int height) {
        // Convert localX and localY to global coordinates
        int worldX = model.getGameState().getGlobalX(localX);
        int worldY = model.getGameState().getGlobalY(localY);

        // Directly assign worldX and worldY without centering adjustments
        mEphemeralPoint.x = worldX;
        mEphemeralPoint.y = worldY;
        return mEphemeralPoint;
    }
}
