package main.game.main.rendering;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;

import java.awt.*;

public class FloatingTextRenderer extends Renderer {
    private final Font mFont = FontPool.getInstance().getBoldFont(20);

    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setFont(mFont);

        // Get the FontMetrics for the current font
        FontMetrics metrics = graphics.getFontMetrics(mFont);

        context.getFloatingText().forEach(ft -> {
            // Measure the width and height of the text
            int textWidth = metrics.stringWidth(ft.getText());
            int textHeight = metrics.getHeight();

            // Calculate the world position
            Point p = calculateWorldPosition(model, ft.getX(), ft.getY(), textWidth, textHeight);

            int x = p.x;
            int y = p.y;

            if (ft.isCentered()) {
                // Adjust X and Y to center the text
//                x = p.x - textWidth / 2;  // Subtract half width to center horizontally
//                y = p.y + metrics.getAscent() - textHeight / 2; // Adjust for baseline and center vertically
            }

            // Render the text with an outline
            mRendererUtils.renderTextWithOutline(g2d, x, y, ft.getText(), ft.getForeground(), ft.getBackground());
//            g2d.setPaint(Color.white);
//            g2d.fillRect(x, y - textHeight, textWidth, textHeight);
//            g2d.drawString(ft.getText(), x, y);
        });
    }
}