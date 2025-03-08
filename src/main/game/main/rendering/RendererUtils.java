package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import main.constants.Direction;
import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RendererUtils {
    public void renderTileSet(GraphicsContext gc, GameModel m, Set<Entity> c, Color bg, Color fg) {
        renderTileSet(gc, m, c, bg, fg, new HashSet<>());
    }
    public void renderTileSet(GraphicsContext gc, GameModel m, Collection<Entity> set, Color bg, Color fg, Set<Entity> exclude) {
        for (Entity entity : set) {
            if (exclude.contains(entity)) { continue; }

            Tile tile = entity.get(Tile.class);
            int spriteWidth = m.getGameState().getSpriteWidth();
            int spriteHeight = m.getGameState().getSpriteHeight();
            Vector3f coordinate = tile.getWorldVector(m);

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
                Entity adjacent = m.tryFetchingEntityAt(row, column);

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

    // Overloaded method without fontSize (uses current font size)
    public void renderTextWithOutline(GraphicsContext gc, int x, int y, String str, Color fg, Color bg) {
        renderTextWithOutline(gc, x, y, str, (float) gc.getFont().getSize(), fg, bg);
    }

//    public void renderTextWithOutline(GraphicsContext g, int x, int y, String str, Color fg, Color bg) {
//        renderTextWithOutline(g, x, y, str, (float) g.getFont().getSize(), fg, bg);
//    }
//
//    public void renderTextWithOutline(GraphicsContext g, int x, int y, String str, float fontSize, Color fg, Color bg) {
//        Color originalColor = g.getColor();
//        Stroke originalStroke = g.getStroke();
//        RenderingHints originalHints = g.getRenderingHints();
//        AffineTransform originalTransform = g.getTransform();
//        Font originalFont = g.getFont();
//
//        // create a glyph vector from your text, then get the shape object
//        GlyphVector glyphVector = originalFont.createGlyphVector(g.getFontRenderContext(), str);
//        Shape textShape = glyphVector.getOutline();
//
//        g.setFont(originalFont.deriveFont(fontSize));
//        g.setColor(bg);
//        g.setStroke(mOutlineStroke);
//        g.translate(x, y);
//        g.draw(textShape); // draw outline
//
//        g.setColor(fg);
//        g.fill(textShape); // fill the shape
//
//        // reset to original settings after painting
//        g.setColor(originalColor);
//        g.setStroke(originalStroke);
//        g.setRenderingHints(originalHints);
//        g.setTransform(originalTransform);
//    }
}

