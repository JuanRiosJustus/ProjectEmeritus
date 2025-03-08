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
            Asset asset = AssetPool.getInstance().getAsset(reticleId);
            Animation animation = asset.getAnimation();

            Image newImage = SwingFXUtils.toFXImage(animation.toImage(), null);
            Point p = calculateWorldPosition(model, camera, tile, newImage);
            graphicsContext.drawImage(newImage, p.x, p.y);
        });

        renderContext.getHoveredTiles().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String reticleId = AssetPool.getInstance().getBlueReticleId(model);
            Asset asset = AssetPool.getInstance().getAsset(reticleId);
            Animation animation = asset.getAnimation();

            Image newImage = SwingFXUtils.toFXImage(animation.toImage(), null);
            Point p = calculateWorldPosition(model, camera, tile, newImage);
            graphicsContext.drawImage(newImage, p.x, p.y);
        });

        String reticleId = AssetPool.getInstance().getYellowReticleId(model);
        Asset asset = AssetPool.getInstance().getAsset(reticleId);
        asset.getAnimation().update();

        reticleId = AssetPool.getInstance().getBlueReticleId(model);
        asset = AssetPool.getInstance().getAsset(reticleId);
        asset.getAnimation().update();
    }

//    public void render(GraphicsContext graphics, GameModel model, RenderContext context) {
//
//        context.getSelectedTiles().forEach(tileEntity -> {
//            Tile tile = tileEntity.get(Tile.class);
//            String reticleId = AssetPool.getInstance().getYellowReticleId(model);
//            Asset asset = AssetPool.getInstance().getAsset(reticleId);
//            Animation animation = asset.getAnimation();
//
//            Image newImage = SwingFXUtils.toFXImage(animation.toImage(), null);
//            Point p = calculateWorldPosition(model, tile, newImage);
//            graphics.drawImage(newImage, p.x, p.y);
//        });
//
//        context.getHoveredTiles().forEach(tileEntity -> {
//            Tile tile = tileEntity.get(Tile.class);
//            String reticleId = AssetPool.getInstance().getBlueReticleId(model);
//            Asset asset = AssetPool.getInstance().getAsset(reticleId);
//            Animation animation = asset.getAnimation();
//
//            Image newImage = SwingFXUtils.toFXImage(animation.toImage(), null);
//            Point p = calculateWorldPosition(model, tile, newImage);
//            graphics.drawImage(newImage, p.x, p.y);
//        });
//
//        String reticleId = AssetPool.getInstance().getYellowReticleId(model);
//        Asset asset = AssetPool.getInstance().getAsset(reticleId);
//        asset.getAnimation().update();
//
//        reticleId = AssetPool.getInstance().getBlueReticleId(model);
//        asset = AssetPool.getInstance().getAsset(reticleId);
//        asset.getAnimation().update();
//    }
}
