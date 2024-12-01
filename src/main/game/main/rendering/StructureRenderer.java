package main.game.main.rendering;

import main.game.components.AssetComponent;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class StructureRenderer extends Renderer {
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        context.getTilesWithStructures().forEach(tileEntity -> {
            AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
            String id = assetComponent.getId(AssetComponent.STRUCTURE_ASSET);
            Asset asset = AssetPool.getInstance().getAsset(id);
            if (asset == null) { return; }

            Animation animation = asset.getAnimation();
            Tile tile = tileEntity.get(Tile.class);

            // Retrieve the image dimensions
            BufferedImage animationImage = animation.toImage();

            Point position = calculateWorldPosition(model, tile, animationImage);

            // Draw the image
            graphics.drawImage(animationImage, position.x, position.y, null);
        });
    }
}
