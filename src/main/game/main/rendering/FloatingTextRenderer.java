package main.game.main.rendering;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;

import java.awt.*;

public class FloatingTextRenderer extends Renderer {
    private final Font mFont = FontPool.getInstance().getBoldFont(20);
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {

        graphics.setFont(mFont);
        // Get the current font
        Font font = graphics.getFont();
        // Get the FontMetrics for the current font
        FontMetrics metrics = graphics.getFontMetrics(font);

        context.getFloatingText().forEach(ft -> {

            int width = metrics.stringWidth(ft.getText());
            int height = metrics.getHeight();
            Point p = calculateWorldPosition(model, ft.getX(), ft.getY(), width, height);

//            graphics.setColor(ColorPalette.BEIGE);
//            graphics.fillRect(p.x, p.y, width, height);

            mRendererUtils.renderTextWithOutline(
                    (Graphics2D) graphics,
                    p.x,
                    p.y,
                    ft.getText(),
                    ft.getForeground(),
                    ft.getBackground()
            );
        });
    }
}
