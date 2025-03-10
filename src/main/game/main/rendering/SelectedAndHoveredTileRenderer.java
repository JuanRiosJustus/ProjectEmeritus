package main.game.main.rendering;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;


public class SelectedAndHoveredTileRenderer extends Renderer {

    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();

        renderContext.getSelectedTiles().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String reticleId = AssetPool.getInstance().getYellowReticleId(model);

            Image image = AssetPool.getInstance().getImage(reticleId);

            Point p = calculateWorldPosition(model, camera, tile, image);
            graphicsContext.drawImage(image, p.x, p.y);
        });

        renderContext.getHoveredTiles().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String reticleId = AssetPool.getInstance().getBlueReticleId(model);

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
