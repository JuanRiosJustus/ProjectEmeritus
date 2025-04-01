package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.game.main.GameModel;

public class BackgroundRenderer extends Renderer {
    private Image mBlurredImage;
    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        if (mBlurredImage != null) {
            graphicsContext.drawImage(mBlurredImage, 0, 0);
        } else {
            // Poll for background wallpaper
            mBlurredImage = renderContext.getGameModel().getBackgroundWallpaper();
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        }
    }
}
