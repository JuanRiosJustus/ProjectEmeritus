package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.AssetComponent;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.asset.AssetPool;


public class TileRenderer extends Renderer {

    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        renderContext.getAllVisibleTiles().forEach(tileEntity -> {
            AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
            Tile tile = tileEntity.get(Tile.class);

            // Draw tile asset
            String id = assetComponent.getMainID();
            Image newImage = AssetPool.getInstance().getImage(id);
            if (newImage == null) { return; }

            Point p = calculateWorldPosition(model, camera, tile, newImage);
            graphicsContext.drawImage(newImage, p.x, p.y);
        });
    }
}
