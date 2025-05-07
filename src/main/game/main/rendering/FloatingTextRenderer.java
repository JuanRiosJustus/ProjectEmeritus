package main.game.main.rendering;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.constants.Point;
import main.game.main.GameModel;
import main.game.stores.FontPool;
import main.game.systems.texts.FloatingText;
import main.game.systems.texts.PopUpFloatingText;

import java.util.ArrayList;
import java.util.List;

public class FloatingTextRenderer extends Renderer {

    private final Text mTextMetrics = new Text();

    @Override
    public void render(GraphicsContext gc, RenderContext rc) {
        List<FloatingText> floatingTexts = new ArrayList<>(rc.getFloatingText());

        for (FloatingText ft : floatingTexts) {
            gc.save();

            // Font setup
            float fontSize = ft.getFontSize();
            Font fontToUse = FontPool.getInstance().getFont(fontSize);
            gc.setFont(fontToUse);
            gc.setTextAlign(TextAlignment.LEFT);

            // Measure text
            mTextMetrics.setText(ft.getText());
            mTextMetrics.setFont(fontToUse);
            int textWidth = (int) mTextMetrics.getLayoutBounds().getWidth();
            int textHeight = (int) mTextMetrics.getLayoutBounds().getHeight();

            // World-to-screen position
            GameModel model = rc.getGameModel();
            String camera = rc.getCamera();
            Point p = calculateWorldPosition(model, camera, ft.getX(), ft.getY(), textWidth, textHeight);
            int x = p.x;
            int y = p.y;

            String str = ft.getText();
            Color bg = ft.getBackground();
            Color fg = ft.getForeground();

            double outlineWidth = Math.max(2, fontSize * 0.1);
            gc.setFill(bg);
            for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth / 2) {
                for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth / 2) {
                    if (dx != 0 || dy != 0) {
                        gc.fillText(str, x + dx, y + dy);
                    }
                }
            }
            gc.setFill(fg);
            gc.fillText(str, x, y);

            gc.restore();
        }
    }
}