package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.constants.Point;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;


public class FloatingTextRenderer extends Renderer {

    private final Text mTextMetrics = new Text();
    @Override
    public void render(GraphicsContext gc, RenderContext rc) {

        GameModel model = rc.getGameModel();
        String camera = rc.getCamera();
        // Get the FontMetrics for the current font
        float generalFontSize = model.getGameState().getFloatingTextFontSize();
        Font generalFont = FontPool.getInstance().getFont(generalFontSize);
        gc.setFont(generalFont);

        rc.getFloatingText().forEach(ft -> {
            // Get font to use for this text
            float fontSize = ft.getFontSize();
            Font fontToUse = FontPool.getInstance().getFont(fontSize);
            gc.setFont(fontToUse);


            mTextMetrics.setText(ft.getText());
            mTextMetrics.setFont(fontToUse);

            int textWidth = (int) mTextMetrics.getLayoutBounds().getWidth();
            int textHeight = (int) mTextMetrics.getLayoutBounds().getHeight();

            // Calculate the world position
            Point p = calculateWorldPosition(model, camera, ft.getX(), ft.getY(), textWidth, textHeight);

            int x = p.x;
            int y = p.y;

            // Render the text with an outline
            renderTextWithOutline(gc, x, y, ft.getText(), fontSize, ft.getForeground(), ft.getBackground());
        });
    }
}