package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.constants.Direction;
import main.constants.Point;
import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Renderer {

    private final Point mEphemeralPoint = new Point();
    public abstract void render(GraphicsContext gc, RenderContext rc);

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
    public Point calculateWorldPosition(GameModel model, String camera, Tile tile, Image image) {
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        int localX = tile.getColumn() * configuredSpriteWidth;
        int localY = tile.getRow() * configuredSpriteHeight;
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        return calculateWorldPosition(model, camera, localX, localY, width, height);
    }

    public Point calculateWorldPosition(GameModel model, String camera, int localX, int localY, int width, int height) {
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        // Calculate tile's center X in global coordinates
        int tileCenterX = model.getGameState().getGlobalX(camera, localX) + (configuredSpriteWidth / 2);

        // Calculate tile's bottom Y in global coordinates
        int tileBottomY = model.getGameState().getGlobalY(camera, localY) + configuredSpriteHeight;

        // Calculate drawX (center image horizontally)
        int drawX = tileCenterX - (width / 2);

        // Calculate drawY (align image bottom with tile bottom)
        int drawY = tileBottomY - height;

        // Use the reusable Point object (mEphemeralPoint)
        mEphemeralPoint.x = drawX;
        mEphemeralPoint.y = drawY;
        return mEphemeralPoint;
    }


    public Point calculateWorldPosition(GameModel model, String camera, int localX, int localY, Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        return calculateWorldPosition(model, camera, localX, localY, width, height);
    }

    public void renderTileSet(GraphicsContext gc, RenderContext rc, Set<Entity> c, Color bg, Color fg) {
        renderTileSet(gc, rc, c, bg, fg, new HashSet<>());
    }
    public void renderTileSet(GraphicsContext gc, RenderContext rc, Collection<Entity> set, Color bg, Color fg, Set<Entity> exclude) {
        GameModel model = rc.getGameModel();
        String camera = rc.getCamera();

        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        for (Entity entity : set) {
            if (exclude.contains(entity)) { continue; }

            Tile tile = entity.get(Tile.class);
            int localX = tile.getColumn() * configuredSpriteWidth;
            int localY = tile.getRow() * configuredSpriteHeight;
            int spriteWidth = model.getGameState().getSpriteWidth();
            int spriteHeight = model.getGameState().getSpriteHeight();

            Point coordinate = calculateWorldPosition(model, camera, localX, localY, spriteWidth, spriteHeight);

            int tileX = (int) coordinate.x;
            int tileY = (int) coordinate.y;
            float multiplier = 0.1f;
            int spriteSubWidth = (int) (spriteWidth * multiplier);
            int spriteSubHeight = (int) (spriteHeight * multiplier);

            // Fill the entire tile
            gc.setFill(bg);
            gc.fillRect(tileX, tileY, spriteWidth, spriteHeight);

            // Handle tile edges
            gc.setFill(fg);
            for (Direction direction : Direction.cardinal) {
                int row = tile.getRow() + direction.y;
                int column = tile.getColumn() + direction.x;
                Entity adjacent = model.tryFetchingEntityAt(row, column);

                if (adjacent == null) continue;
                if (set.contains(adjacent)) { continue; }
                if (exclude.contains(adjacent)) { continue; }

                int x = tileX + (direction == Direction.East ? spriteWidth - spriteSubWidth : 0);
                int y = tileY + (direction == Direction.South ? spriteHeight - spriteSubHeight : 0);
                int width = (direction == Direction.East || direction == Direction.West) ? spriteSubWidth : spriteWidth;
                int height = (direction == Direction.North || direction == Direction.South) ? spriteSubHeight : spriteHeight;

                gc.fillRect(x, y, width, height);
            }
        }
    }

    public void renderTextWithOutline(GraphicsContext gc, int x, int y, String str, float fontSize, Color fg, Color bg) {
        // Save original state
        Font originalFont = gc.getFont();
        Paint originalFill = gc.getFill();
        Paint originalStroke = gc.getStroke();
        double originalLineWidth = gc.getLineWidth();
        TextAlignment originalTextAlign = gc.getTextAlign();

        // Set font
        Font textFont = new Font(gc.getFont().getName(), fontSize);
        gc.setFont(textFont);
        gc.setTextAlign(TextAlignment.LEFT);

        // Set outline width
        double outlineWidth = Math.max(2, fontSize * 0.1); // Dynamic thickness

        // Draw outline by drawing the text in different directions
        gc.setFill(bg);
        for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth / 2) {
            for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth / 2) {
                if (dx != 0 || dy != 0) {
                    gc.fillText(str, x + dx, y + dy);
                }
            }
        }

        // Draw the foreground text
        gc.setFill(fg);
        gc.fillText(str, x, y);

        // Restore original settings
        gc.setFont(originalFont);
        gc.setFill(originalFill);
        gc.setStroke(originalStroke);
        gc.setLineWidth(originalLineWidth);
        gc.setTextAlign(originalTextAlign);
    }

//    public Point calculateWorldPositionRaw(GameModel model, int localX, int localY, int width, int height) {
//        // Convert localX and localY to global coordinates
//        int worldX = model.getGameState().getGlobalX(localX);
//        int worldY = model.getGameState().getGlobalY(localY);
//
//        // Directly assign worldX and worldY without centering adjustments
//        mEphemeralPoint.x = worldX;
//        mEphemeralPoint.y = worldY;
//        return mEphemeralPoint;
//    }

}
