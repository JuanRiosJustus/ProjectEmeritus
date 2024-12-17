package main.game.main.rendering;

import main.game.components.AssetComponent;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

public class TileRenderer extends Renderer {
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        context.getAllVisibleTiles().forEach(tileEntity -> {
            AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
            Tile tile = tileEntity.get(Tile.class);

            // Draw tile asset
            String id = assetComponent.getMainID();
            Animation animation = AssetPool.getInstance().getAnimation(id);
            if (animation == null) { return; } // The view might have rendered before assets are generated

            Point p = calculateWorldPosition(model, tile, animation.toImage());
            graphics.drawImage(animation.toImage(), p.x, p.y, null);
//            if (tile.getTopLayerType().equalsIgnoreCase(Tile.LAYER_TYPE_LIQUID_TERRAIN)) {
//                String id = assetComponent.getId(AssetComponent.LIQUID_ASSET);
//                Asset asset = AssetPool.getInstance().getAsset(id);
//                if (asset != null) {
//                    Animation animation = asset.getAnimation();
//                    Point p = calculateWorldPosition(model, tile, animation.toImage());
//                    graphics.drawImage(animation.toImage(), p.x, p.y, null);
//                }
//            } else {
//                String id = assetComponent.getId(AssetComponent.TERRAIN_ASSET);
//                Asset asset = AssetPool.getInstance().getAsset(id);
//                if (asset != null) {
//                    Animation animation = asset.getAnimation();
//                    Point p = calculateWorldPosition(model, tile, animation.toImage());
//                    graphics.drawImage(animation.toImage(), p.x, p.y, null);
//                }
//            }

            // Draw the directional shadows
            List<String> directionalShadowIds = assetComponent.getIds(AssetComponent.DIRECTIONAL_SHADOWS_ASSET);
            if (!directionalShadowIds.isEmpty()) {
                for (String directionalShadowId : directionalShadowIds) {
                    animation = AssetPool.getInstance().getAnimation(directionalShadowId);
                    p = calculateWorldPosition(model, tile, animation.toImage());
                    graphics.drawImage(animation.toImage(), p.x, p.y, null);
                }
            }

            // Draw the depth shadows
            if (!tile.isWall()) {
                id = assetComponent.getID(AssetComponent.DEPTH_SHADOWS_ASSET);
                animation =  AssetPool.getInstance().getAnimation(id);
                p = calculateWorldPosition(model, tile, animation.toImage());
                graphics.drawImage(animation.toImage(), p.x, p.y, null);
            }
        });
    }
}
