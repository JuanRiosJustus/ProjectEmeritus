package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.constants.Point;
import main.game.components.AssetComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.graphics.AssetPool;


public class StructureRenderer extends Renderer {
    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        renderContext.getTilesWithStructures().forEach(tileEntity -> {
            TileComponent tile = tileEntity.get(TileComponent.class);

            String structureID = tile.getStructureID();
            Entity structureEntity = EntityStore.getInstance().get(structureID);
            AssetComponent assetComponent = structureEntity.get(AssetComponent.class);
            String id = assetComponent.getMainID();
//            Asset asset = AssetPool.getInstance().getAsset(id);
//            if (asset == null) { return; }
//            Animation animation = asset.getAnimation();
            // Retrieve the image dimensions
//            Image animationImage = SwingFXUtils.toFXImage(animation.toImage(), null);
            Image animationImage = AssetPool.getInstance().getImage(id);

            if (animationImage == null) { return; }


//            Point position = calculateWorldPosition(model, tile, animationImage);
            Point position = calculateWorldPosition(model, camera, tile, animationImage);

            int x = position.x;
            int y = position.y;


            y = (int) (y - (model.getGameState().getSpriteHeight() * .1));

            // Draw the image
            graphicsContext.drawImage(animationImage, x, y);
        });
    }
}
