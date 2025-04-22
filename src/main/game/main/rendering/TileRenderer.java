package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.AssetComponent;
import main.game.components.tile.TileComponent;
import main.game.main.GameModel;


public class TileRenderer extends Renderer {

    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        renderContext.getAllVisibleTiles().forEach(tileEntity -> {
            AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
            TileComponent tile = tileEntity.get(TileComponent.class);

            // Draw tile asset
            String id = assetComponent.getMainID();
            if (id == null) { return; }
            Image image = getImageWithID(id);
//            if (image == null) { return; }

            Point p = calculateWorldPosition(model, camera, tile, image);
            graphicsContext.drawImage(image, p.x, p.y);

            id = assetComponent.getShadowID();
            image = getImageWithID(id);
            if (image == null) { return; }
            graphicsContext.drawImage(image, p.x, p.y);
        });
    }
}
