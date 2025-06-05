package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.AssetPool;


public class SelectedAndHoveredTileRenderer extends Renderer {

    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();

        Entity hoveredTile = renderContext.getHoveredTile();
        if (hoveredTile != null) {
            TileComponent tile = hoveredTile.get(TileComponent.class);
            String reticleId = AssetPool.getInstance().getBlueReticleId(model);

            Image image = AssetPool.getInstance().getImage(reticleId);

            Point p = calculateWorldPosition(model, camera, tile, image);
            graphicsContext.drawImage(image, p.x, p.y);
        }


        renderContext.getSelectedTiles().forEach(tileEntity -> {
            TileComponent tile = tileEntity.get(TileComponent.class);
            String reticleId = AssetPool.getInstance().getYellowReticleId(model);

            Image image = AssetPool.getInstance().getImage(reticleId);

            Point p = calculateWorldPosition(model, camera, tile, image);
            graphicsContext.drawImage(image, p.x, p.y);
        });

        String reticleId = AssetPool.getInstance().getYellowReticleId(model);
        AssetPool.getInstance().update(reticleId);

        reticleId = AssetPool.getInstance().getBlueReticleId(model);
        AssetPool.getInstance().update(reticleId);
    }
}
