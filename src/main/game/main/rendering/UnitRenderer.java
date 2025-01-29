package main.game.main.rendering;

import main.constants.Direction;
import main.game.components.AssetComponent;
import main.game.components.DirectionComponent;
import main.game.components.MovementComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class UnitRenderer extends Renderer {

    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { return; } // Maybe this is because of things happening from seperate thread?
            AssetComponent unitAssetComponent = unitEntity.get(AssetComponent.class);
            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            String id = unitAssetComponent.getMainID();
            BufferedImage image = AssetPool.getInstance().getImage(id);
            if (image == null) { return; } // TODO why is this null sometimes??

            // Default origin with not animation consideration
            int x = movementComponent.getX();
            int y = movementComponent.getY();

            Point p = calculateWorldPosition(model, x, y, image);
            graphics.drawImage(image, p.x, p.y, null);

            DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
            graphics.setFont(FontPool.getInstance().getDefaultFont());
            String str = directionComponent.getFacingDirection().name();
            if (str.equalsIgnoreCase(Direction.North.name())) {
                str = "North ↑";
            } else if (str.equalsIgnoreCase(Direction.East.name())) {
                str = "East →";
            } else if (str.equalsIgnoreCase(Direction.South.name())) {
                str = "South ↓";
            } else if (str.equalsIgnoreCase(Direction.West.name())) {
                str = "West ←";
            }
            graphics.setColor(ColorPalette.WHITE);
//            graphics.setFont(FontPool.getInstance().getFont(12).deriveFont(Font.BOLD));
//            graphics.setFont(FontPool.getInstance().getFontForHeight((int) (configuredSpriteHeight * .25)));
        });
    }
//    @Override
//    public void render(Graphics graphics, GameModel model, RenderContext context) {
//        context.getTilesWithUnits().forEach(tileEntity -> {
//            Tile tile = tileEntity.get(Tile.class);
//            Entity unitEntity = tile.getUnit();
//            if (unitEntity == null) { return; } // Maybe this is because of things happening from seperate thread?
//            AssetComponent unitAssetComponent = unitEntity.get(AssetComponent.class);
//            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
////            String id = unitAssetComponent.getId(AssetComponent.UNIT_ASSET);
//            String id = unitAssetComponent.getMainID();
//            Asset asset = AssetPool.getInstance().getAsset(id);
//            if (asset == null) { return; } // TODO why is this null sometimes??
//            Animation animation = asset.getAnimation();
//
//            // Default origin with not animation consideration
//            int x = movementComponent.getX();
//            int y = movementComponent.getY();
//
//
//            Point p = calculateWorldPosition(model, x, y, animation.toImage());
//            graphics.drawImage(animation.toImage(), p.x, p.y, null);
//
//
//            DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);
//            graphics.setFont(FontPool.getInstance().getDefaultFont());
//            String str = directionComponent.getFacingDirection().name();
//            if (str.equalsIgnoreCase(Direction.North.name())) {
//                str = "North ↑";
//            } else if (str.equalsIgnoreCase(Direction.East.name())) {
//                str = "East →";
//            } else if (str.equalsIgnoreCase(Direction.South.name())) {
//                str = "South ↓";
//            } else if (str.equalsIgnoreCase(Direction.West.name())) {
//                str = "West ←";
//            }
//            graphics.setColor(ColorPalette.WHITE);
////            graphics.setFont(FontPool.getInstance().getFont(12).deriveFont(Font.BOLD));
////            graphics.setFont(FontPool.getInstance().getFontForHeight((int) (configuredSpriteHeight * .25)));
//        });
//    }
}
