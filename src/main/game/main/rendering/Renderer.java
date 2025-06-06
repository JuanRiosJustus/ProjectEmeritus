package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.constants.Direction;
import main.constants.Point;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.game.stores.FontPool;
import main.game.systems.texts.FloatingText;
import main.graphics.AssetPool;

import java.util.*;

public abstract class Renderer {
    protected final Text mTextMetrics = new Text();
    private final Point mEphemeralPoint = new Point();
    public abstract void render(GraphicsContext gc, RenderContext rc);

    public Entity getEntityWithID(String entityID) { return EntityStore.getInstance().get(entityID); }
    public Image getImageWithID(String id) { return AssetPool.getInstance().getImage(id); }
    /**
     * Calculates the drawing position for an image to align its bottom with the tile's bottom
     * and center it horizontally on the tile.
     *
     * @param model the game model containing global positioning details
     * @param tile the tile to align the image with
     * @param image the image to be drawn
     * @return the calculated drawing position as a Point
     */
    public Point calculateWorldPosition(GameModel model, String camera, TileComponent tile, Image image) {
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

    public void renderTileSet(GraphicsContext gc, RenderContext rc, List<String> c, Color bg, Color fg) {
        renderTileSet(gc, rc, c, bg, fg, new ArrayList<>());
    }
    public void renderTileSet(GraphicsContext gc, RenderContext rc, Collection<String> set, Color bg, Color fg, List<String> exclude) {
        GameModel model = rc.getGameModel();
        String camera = rc.getCamera();

        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        for (String tileEntityID : set) {
            if (exclude.contains(tileEntityID)) { continue; }

            Entity tileEntity = EntityStore.getInstance().get(tileEntityID);
            TileComponent tile = tileEntity.get(TileComponent.class);
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


    public void renderTileSetV1(GraphicsContext gc, RenderContext rc, Set<Entity> c, Color bg, Color fg) {
        renderTileSetV1(gc, rc, c, bg, fg, new HashSet<>());
    }
    public void renderTileSetV1(GraphicsContext gc, RenderContext rc, Collection<Entity> set, Color bg, Color fg, Set<Entity> exclude) {
        GameModel model = rc.getGameModel();
        String camera = rc.getCamera();

        int configuredSpriteWidth = model.getGameState().getSpriteWidth();
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();

        for (Entity entity : set) {
            if (exclude.contains(entity)) { continue; }

            TileComponent tile = entity.get(TileComponent.class);
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

//    public void renderTextWithOutline(GraphicsContext gc, FloatingText ft) {
//        // Save original state
//        mTextMetrics.setText(ft.getText());
//        mTextMetrics.setFont(fontToUse);
//
//        int textWidth = (int) mTextMetrics.getLayoutBounds().getWidth();
//        int textHeight = (int) mTextMetrics.getLayoutBounds().getHeight();
//
//        // Calculate the world position
//        Point p = calculateWorldPosition(model, camera, ft.getX(), ft.getY(), textWidth, textHeight);
//        int x = p.x;
//        int y = p.y;
//
//        // Set font
//        Font textFont = new Font(gc.getFont().getName(), fontSize);
//        gc.setFont(textFont);
//        gc.setTextAlign(TextAlignment.LEFT);
//
//        // Set outline width
//        double outlineWidth = Math.max(2, fontSize * 0.1); // Dynamic thickness
//
//        // Draw outline by drawing the text in different directions
//        gc.setFill(bg);
//        for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth / 2) {
//            for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth / 2) {
//                if (dx != 0 || dy != 0) {
//                    gc.fillText(str, x + dx, y + dy);
//                }
//            }
//        }
//
//        // Draw the foreground text
//        gc.setFill(fg);
//        gc.fillText(str, x, y);
//
//        // Restore original settings
//        gc.setFont(originalFont);
//        gc.setFill(originalFill);
//        gc.setStroke(originalStroke);
//        gc.setLineWidth(originalLineWidth);
//        gc.setTextAlign(originalTextAlign);
//    }

    public void renderFloatingText(GraphicsContext gc, RenderContext rc, FloatingText ft) {
        gc.save();

        // Set font
        float fontSize = ft.getFontSize();
        Font fontToUse = FontPool.getInstance().getFont(fontSize);
        gc.setFont(fontToUse);
        gc.setTextAlign(TextAlignment.LEFT);
        String str = ft.getText();
        Color bg = ft.getBackground();
        Color fg = ft.getForeground();

        GameModel model = rc.getGameModel();
        String camera = rc.getCamera();
        mTextMetrics.setText(ft.getText());
        mTextMetrics.setFont(fontToUse);
        int textWidth = (int) mTextMetrics.getLayoutBounds().getWidth();
        int textHeight = (int) mTextMetrics.getLayoutBounds().getHeight();
        Point p = calculateWorldPosition(model, camera, ft.getX(), ft.getY(), textWidth, textHeight);
        int x = p.x;
        int y = p.y;


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

        gc.restore();
    }

    public void renderTextWithOutline(GraphicsContext gc, int x, int y, String str, float fontSize, Color fg, Color bg) {
        // Save original state
//        Font originalFont = gc.getFont();
//        Paint originalFill = gc.getFill();
//        Paint originalStroke = gc.getStroke();
//        double originalLineWidth = gc.getLineWidth();
//        TextAlignment originalTextAlign = gc.getTextAlign();
        gc.save();

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

        gc.restore();
        // Restore original settings
//        gc.setFont(originalFont);
//        gc.setFill(originalFill);
//        gc.setStroke(originalStroke);
//        gc.setLineWidth(originalLineWidth);
//        gc.setTextAlign(originalTextAlign);
    }

//    public void renderBoundingTextBox(GraphicsContext gc, RenderContext rc,  FloatingText floatingText) {
//        // ====== ADD THIS FOR DEBUGGING RECTANGLES ======
//        gc.save(); // Save previous GC state
//
//        int textWidth = (int) mTextMetrics.getLayoutBounds().getWidth();
//        int textHeight = (int) mTextMetrics.getLayoutBounds().getHeight();
//
//        // Calculate the world position
//        GameModel model = rc.getGameModel();
//        String camera = rc.getCamera();
//        String str = floatingText.getText();
//        Color bg = floatingText.getBackground();
//        Color fg = floatingText.getForeground();
//
//        Point p = calculateWorldPosition(model, camera, floatingText.getX(), floatingText.getY(), textWidth, textHeight);
//
//        double fontSize = gc.getFont().getSize();
//        gc.setStroke(Color.DARKGRAY);  // Red color for the debug box
//        double outlineWidth = Math.max(2, fontSize * 0.1); // Dynamic thickness
//        // Draw outline by drawing the text in different directions
//        gc.setFill(bg);
//        for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth / 2) {
//            for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth / 2) {
//                if (dx != 0 || dy != 0) {
//                    gc.fillText(str, p.x, + dx, p.y + dy);
//                }
//            }
//        }
////        gc.setLineWidth(outlineWidth);                          // Thin rectangle
//        gc.strokeRect(p.x, p.y - textHeight, textWidth, textHeight);
//        gc.restore(); // Restore previous GC state
//        // ====== END DEBUGGING RECTANGLE ======
//    }

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
