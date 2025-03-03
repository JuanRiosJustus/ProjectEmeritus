package main.game.main.rendering;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.AssetComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;


public class StructureRendererV2 extends RendererV2 {
    @Override
    public void render(GraphicsContext graphics, GameModel model, RenderContext context) {
        context.getTilesWithStructures().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);

            String structureID = tile.getStructureID();
            Entity structureEntity = EntityStore.getInstance().get(structureID);
            AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
            String id = assetComponent.getMainID();
            Asset asset = AssetPool.getInstance().getAsset(id);
            if (asset == null) { return; }

            Animation animation = asset.getAnimation();

            // Retrieve the image dimensions
            Image animationImage = SwingFXUtils.toFXImage(animation.toImage(), null);

            Point position = calculateWorldPosition(model, tile, animationImage);

            int x = position.x;
            int y = position.y;


            y = (int) (y - (model.getGameState().getSpriteHeight() * .1));

            // Draw the image
            graphics.drawImage(animationImage, x, y);
        });
    }
}
