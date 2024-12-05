package main.game.main.rendering;

import main.constants.Direction;
import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RendererUtils {
    private final BasicStroke mOutlineStroke = new BasicStroke(5f);
    public void renderTileSet(Graphics g, GameModel m, Set<Entity> c, Color bg, Color fg) {
        renderTileSet(g, m, c, bg, fg, new HashSet<>());
    }
    public void renderTileSet(Graphics g, GameModel m, Collection<Entity> set, Color bg, Color fg, Set<Entity> exclude) {
        for (Entity entity : set) {
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
            g.setColor(bg);
            g.fillRect(tileX, tileY, spriteWidth, spriteHeight);

            // Handle tile edges
            g.setColor(fg);
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

                g.fillRect(x, y, width, height);
            }
        }
    }

    public void renderTextWithOutline(Graphics2D g, int x, int y, String str, Color fg, Color bg) {
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();
        RenderingHints originalHints = g.getRenderingHints();
        AffineTransform originalTransform = g.getTransform();

        // create a glyph vector from your text, then get the shape object
        GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), str);
        Shape textShape = glyphVector.getOutline();

        g.setColor(bg);
        g.setStroke(mOutlineStroke);
        g.translate(x, y);
        g.draw(textShape); // draw outline

        g.setColor(fg);
        g.fill(textShape); // fill the shape

        // reset to original settings after painting
        g.setColor(originalColor);
        g.setStroke(originalStroke);
        g.setRenderingHints(originalHints);
        g.setTransform(originalTransform);
    }


}
