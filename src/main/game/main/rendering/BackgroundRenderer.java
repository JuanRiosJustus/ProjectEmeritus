package main.game.main.rendering;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import main.engine.EngineController;
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

//            if (EngineController.getInstance().getStage().isFocused() && EngineController.getInstance().getStage().isShowing()) {
//                Robot robot = new Robot();
//                WritableImage imgReturn = robot.getScreenCapture(null, new Rectangle2D(
//                        EngineController.getInstance().getStage().getX(),
//                        EngineController.getInstance().getStage().getY(),
//                        EngineController.getInstance().getStage().getWidth(),
//                        EngineController.getInstance().getStage().getHeight()
//                ));
//                mBlurredImage = imgReturn;
//            }
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        }
    }
}
