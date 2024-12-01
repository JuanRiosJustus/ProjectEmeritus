package main.game.main.rendering;

import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.awt.Graphics;
import java.awt.Point;

public class SelectedAndHoveredTileRenderer extends Renderer {
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {

        context.getSelectedTiles().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String reticleId = AssetPool.getInstance().getYellowReticleId(model);
            Asset asset = AssetPool.getInstance().getAsset(reticleId);
            Animation animation = asset.getAnimation();

            Point p = calculateWorldPosition(model, tile, animation.toImage());
            graphics.drawImage(animation.toImage(), p.x, p.y, null);
        });

        context.getHoveredTiles().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String reticleId = AssetPool.getInstance().getBlueReticleId(model);
            Asset asset = AssetPool.getInstance().getAsset(reticleId);
            Animation animation = asset.getAnimation();

            Point p = calculateWorldPosition(model, tile, animation.toImage());
            graphics.drawImage(animation.toImage(), p.x, p.y, null);
        });

        String reticleId = AssetPool.getInstance().getYellowReticleId(model);
        Asset asset = AssetPool.getInstance().getAsset(reticleId);
        asset.getAnimation().update();

        reticleId = AssetPool.getInstance().getBlueReticleId(model);
        asset = AssetPool.getInstance().getAsset(reticleId);
        asset.getAnimation().update();
    }
}
