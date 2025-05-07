package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.constants.Point;
import main.game.components.AssetComponent;
import main.game.components.tile.TileComponent;
import main.game.main.GameModel;
import main.game.stores.ColorPalette;
import main.game.stores.FontPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class TileRenderer extends Renderer {

    private static final Map<Integer, Double> mFontSizeCache = new HashMap<>();


    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        renderContext.getAllVisibleTiles().forEach(tileEntity -> {
            AssetComponent assetComponent = tileEntity.get(AssetComponent.class);
            TileComponent tile = tileEntity.get(TileComponent.class);

            // Draw tile asset
            String id = assetComponent.getMainID();
            if (id == null) { return; }
            Image image = getImageWithID(id);

            Point p = calculateWorldPosition(model, camera, tile, image);
            graphicsContext.drawImage(image, p.x, p.y);

            id = assetComponent.getShadowID();
            image = getImageWithID(id);
            if (image == null) { return; }
            graphicsContext.drawImage(image, p.x, p.y);


            // Center within tile
            String spawnRegion = tile.getSpawnRegion();
            if (spawnRegion == null || spawnRegion.isEmpty()) { return; }
            drawCenteredText(graphicsContext, renderContext.getGameModel(), spawnRegion, p);
        });
    }


    public void drawCenteredText(GraphicsContext gc, GameModel model, String textStr, Point topLeft) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();

        double fontSize = getFontSize(textStr, spriteHeight, spriteWidth);

        Font font = FontPool.getInstance().getFont(fontSize);
        gc.setFont(font);

        // 3. Measure actual layout dimensions
        Text text = new Text(textStr);
        text.setFont(font);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        double centeredX = topLeft.x + (spriteWidth - textWidth) / 2.0;
        double centeredY = topLeft.y + (spriteHeight + textHeight) / 2.0;

        // 4. Draw outline
        double outlineWidth = Math.max(1.0, fontSize * 0.08);
        gc.setFill(ColorPalette.BLACK_LEVEL_1);
        for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth) {
            for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth) {
                if (dx != 0 || dy != 0) {
                    gc.fillText(textStr, centeredX + dx, centeredY + dy);
                }
            }
        }

        // 5. Draw main text
        gc.setFill(ColorPalette.WHITE_LEVEL_2);
        gc.fillText(textStr, centeredX, centeredY);
    }

    private double getFontSize(String textStr, int spriteHeight, int spriteWidth) {
        int cacheKey = Objects.hash(textStr, spriteWidth);
        if (mFontSizeCache.containsKey(cacheKey)) {
            return mFontSizeCache.get(cacheKey);
        }

        double maxFontSize = spriteHeight * 0.6;
        Text text = mTextMetrics;
        text.setText(textStr);
        double bestFitSize = 8;

        for (double fontSize = maxFontSize; fontSize >= 8; fontSize = fontSize - 2) {
            Font font = FontPool.getInstance().getFont(fontSize);
            text.setFont(font);
            double textWidth = text.getLayoutBounds().getWidth();
            if (textWidth <= spriteWidth) {
                bestFitSize = fontSize;
                break;
            }
        }

        mFontSizeCache.put(cacheKey, bestFitSize);
        return bestFitSize;
    }

//    public static void drawCenteredText(GraphicsContext gc, GameModel model, String textStr, Point topLeft) {
//
//        // 1. Set font size proportional to tile height
//        int spriteWidth = model.getGameState().getSpriteWidth();
//        int spriteHeight = model.getGameState().getSpriteHeight();
//        double fontSize = spriteHeight * 0.6; // 60% of tile height
//        Font font = FontPool.getInstance().getFont(fontSize);
//        gc.setFont(font);
//
//        // 2. Measure text size
//        Text text = new Text(textStr);
//        text.setFont(font);
//        double textWidth = text.getLayoutBounds().getWidth();
//        double textHeight = text.getLayoutBounds().getHeight();
//
//        // 3. Calculate centered position
//        double centeredX = topLeft.x + (spriteWidth - textWidth) / 2.0;
//        double centeredY = topLeft.y + (spriteHeight + textHeight) / 2.0;
//
//        // 4. Draw outline
//        double outlineWidth = Math.max(1.0, fontSize * 0.08);
//        gc.setFill(ColorPalette.BLACK_LEVEL_1); // outline color
//        for (double dx = -outlineWidth; dx <= outlineWidth; dx += outlineWidth) {
//            for (double dy = -outlineWidth; dy <= outlineWidth; dy += outlineWidth) {
//                if (dx != 0 || dy != 0) {
//                    gc.fillText(textStr, centeredX + dx, centeredY + dy);
//                }
//            }
//        }
//
//        // 5. Draw main text
//        gc.setFill(ColorPalette.WHITE_LEVEL_2);
//        gc.fillText(textStr, centeredX, centeredY);
//    }
}
