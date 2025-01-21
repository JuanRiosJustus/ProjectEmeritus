package main.game.main.rendering;

import main.game.main.GameModel;
import main.game.stores.pools.FontPool;

import java.awt.*;

public class FloatingTextRenderer extends Renderer {

    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Graphics2D g2d = (Graphics2D) graphics;

        // Get the FontMetrics for the current font
        float generalFontSize = model.getGameState().getFloatingTextFontSize();
        Font generalFont = FontPool.getInstance().getFont(generalFontSize);
        graphics.setFont(generalFont);


        context.getFloatingText().forEach(ft -> {

            // Get font to use for this text
            float fontSize = ft.getFontSize();
            Font fontToUse = FontPool.getInstance().getFont(fontSize);
            graphics.setFont(fontToUse);
            FontMetrics metrics = graphics.getFontMetrics(fontToUse);

            // Measure the width and height of the text
            int textWidth = metrics.stringWidth(ft.getText());
            int textHeight = metrics.getHeight();

            // Calculate the world position
            Point p = calculateWorldPosition(model, ft.getX(), ft.getY(), textWidth, textHeight);

            int x = p.x;
            int y = p.y;

            // Render the text with an outline
            mRendererUtils.renderTextWithOutline(g2d, x, y, ft.getText(), fontSize, ft.getForeground(), ft.getBackground());
        });
    }
}