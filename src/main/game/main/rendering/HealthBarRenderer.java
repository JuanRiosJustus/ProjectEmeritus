package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.constants.Point;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;

import java.util.HashMap;
import java.util.Map;

public class HealthBarRenderer extends Renderer {

    private static final float HEALTH_BAR_BLACK_BORDER_WIDTH_MULTIPLIER = 1.5f;
    private final Map<Entity, Double> previousHealthMap = new HashMap<>();

    @Override
    public void render(GraphicsContext gc,  RenderContext context) {
        GameModel model = context.getGameModel();
        String camera = context.getCamera();

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Iterate through each tile with a unit
        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String unitID = tile.getUnitID();
            Entity unit = getEntityWithID(unitID);

            if (unit == null) return; // This can happen when a unit dies

            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
            MovementComponent movementComponent = unit.get(MovementComponent.class);

            // Sprite dimensions
            int width = model.getGameState().getSpriteWidth();
            int height = model.getGameState().getSpriteHeight();
            int x = movementComponent.getX();
            int y = movementComponent.getY();
            Point position = calculateWorldPosition(model, camera, x, y, width, height);

            int tileX = position.x;
            int tileY = position.y;

            // Health bar dimensions
            int healthBarWidth = (int) (width * 0.8); // 80% of the tile width
            int healthBarHeight = (int) (height * 0.08); // 7.5% of the tile height
            int healthBarX = tileX + (width - healthBarWidth) / 2; // Centered horizontally
            int healthBarY = tileY - healthBarHeight - (int) (height * 0.1); // Just above the tile

            drawTransparentHealthBarBorder(
                    gc,
                    healthBarX,
                    healthBarY,
                    healthBarWidth,
                    healthBarHeight,
                    ColorPalette.WHITE_LEVEL_1
            );


            // Render health bar
            Color healthColor = ColorPalette.TRANSLUCENT_GREEN_LEVEL_4;

            renderResourceBar(
                    gc,
                    healthBarX + 2,
                    healthBarY,
                    healthBarWidth - 4,
                    healthBarHeight,
                    healthColor,
                    statisticsComponent.getCurrentHealth(),
                    statisticsComponent.getTotalHealth(),
                    currentTime,
                    previousHealthMap,
                    unit
            );
        });
    }

    /**
     * Renders a resource bar with interpolation.
     */
    private static void renderResourceBar(GraphicsContext gc, int x, int y, int width, int height,
                                          Color baseColor, int current, int max, long currentTime,
                                          Map<Entity, Double> previousValueMap, Entity unit) {
//        if (current >= max) return; // Skip full health bars

        // Smooth value interpolation
        double previousValue = previousValueMap.getOrDefault(unit, (double) current);
        previousValue = interpolateValue(previousValue, current);
        previousValueMap.put(unit, previousValue);

        // Compute filled width
        double fillWidth = (previousValue / max) * width;

        // Get dynamic color
        Color barColor = getBarColor(baseColor, previousValue / max, currentTime);

        // Draw background bar
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x, y, width, height);

        // Draw resource fill
        gc.setFill(barColor);
        gc.fillRect(x, y, fillWidth, height);

        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(HEALTH_BAR_BLACK_BORDER_WIDTH_MULTIPLIER);
        gc.strokeRect(x, y, width, height);
    }

    /**
     * Smooth interpolation for gradual updates.
     */
    private static double interpolateValue(double previous, int current) {
        return previous + (current - previous) * 0.2; // Adjust 0.2 for smoother/faster interpolation
    }

    /**
     * Determines the health bar color based on percentage.
     */
    private static Color getBarColor(Color baseColor, double percentage, long currentTime) {
        if (percentage < 0.2) return Color.RED;
        if (percentage < 0.5) return Color.ORANGE;
        return baseColor;
    }

    /**
     * Draws a transparent health bar border.
     */
    private static void drawTransparentHealthBarBorder(GraphicsContext gc, double x, double y, double width, double height, Color color) {
        double originalLineWidth = gc.getLineWidth();
        Color originalStroke = (Color) gc.getStroke();

        gc.setStroke(color);
        gc.setLineWidth(4.0);

        int verticalMultiplier = (int) (height * .01);
        int horizontalMultiplier = (int) (width * .02);
        gc.strokeRect(
                x - horizontalMultiplier,
                y - verticalMultiplier,
                width + (horizontalMultiplier * 2),
                height + (verticalMultiplier * 2)
        );

        gc.setLineWidth(originalLineWidth);
        gc.setStroke(originalStroke);
    }
}