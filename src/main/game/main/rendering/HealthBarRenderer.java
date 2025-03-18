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

    // Store previous values for delayed health drop animation
    private final Map<Entity, Double> mPreviousHealthMap = new HashMap<>();
    private final Map<Entity, Double> mDelayedHealthMap = new HashMap<>();
    private final Map<Color, Color> mDarkerColorMap = new HashMap<>();
    private final Map<Color, Color> mBrighterColorMap = new HashMap<>();

    @Override
    public void render(GraphicsContext gc, RenderContext context) {
        GameModel model = context.getGameModel();
        String camera = context.getCamera();

        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String unitID = tile.getUnitID();
            Entity unit = getEntityWithID(unitID);

            if (unit == null) { return; }

            MovementComponent movement = unit.get(MovementComponent.class);

            // Sprite dimensions
            int width = model.getGameState().getSpriteWidth();
            int height = model.getGameState().getSpriteHeight();
            int x = movement.getX();
            int y = movement.getY();
            Point position = calculateWorldPosition(model, camera, x, y, width, height);

            int tileX = position.x;
            int tileY = position.y;

            // Health bar dimensions
            int healthBarBorderWidth = (int) (width * 0.8);
            int healthBarHeight = (int) (height * 0.08);
            int healthBarBorderX = tileX + (width - healthBarBorderWidth) / 2;
            int healthBarBorderY = tileY - healthBarHeight - (int) (height * 0.1);
            int healthBarX = (int) (healthBarBorderX + 2);
            int healthBarWidth = (int) (healthBarBorderWidth - 4);
            Color borderColor = ColorPalette.WHITE_LEVEL_1;

            drawHealthBarBorder(gc, healthBarBorderX, healthBarBorderY, healthBarBorderWidth, healthBarHeight, borderColor);

            drawPokemonStyleHealthBar(gc, unit, healthBarX, healthBarBorderY, healthBarWidth, healthBarHeight);
        });
    }

    /**
     * Pokémon-style health bar rendering with delayed damage animation and flickering effect.
     */
    private void drawPokemonStyleHealthBar(GraphicsContext gc, Entity unit, int x, int y, int width, int height) {

        long currentTime = System.currentTimeMillis();
        StatisticsComponent stats = unit.get(StatisticsComponent.class);
        int current = stats.getCurrentHealth();
        int max = stats.getTotalHealth();
        // If no previous record, set both values to current health
        double previousHealth = mPreviousHealthMap.getOrDefault(unit, (double) current);
        double delayedHealth = mDelayedHealthMap.getOrDefault(unit, (double) current);

        // Update current health immediately
        previousHealth = interpolateValue(previousHealth, current);
        mPreviousHealthMap.put(unit, previousHealth);

        // Delayed health bar shrinks more slowly
        delayedHealth = interpolateValue(delayedHealth, (int) previousHealth, 0.05); // Slower shrink rate
        mDelayedHealthMap.put(unit, delayedHealth);

        // Compute health bar widths
        double currentFillWidth = (previousHealth / max) * width;
        double delayedFillWidth = (delayedHealth / max) * width;

        // Get dynamic flickering color
        Color mainBarColor = getFlickeringColor(previousHealth / max, currentTime);
        Color delayedBarColor = Color.DIMGRAY; // Delay effect is gray

        // Draw background bar (gray)
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x, y, width, height);

        // Draw delayed damage bar (shrinking slowly)
        gc.setFill(delayedBarColor);
        gc.fillRect(x, y, delayedFillWidth, height);

        // Draw immediate health bar
        gc.setFill(mainBarColor);
        gc.fillRect(x, y, currentFillWidth, height);

        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(HEALTH_BAR_BLACK_BORDER_WIDTH_MULTIPLIER);
        gc.strokeRect(x, y, width, height);
    }

    /**
     * Smooth interpolation for gradual updates (fast).
     */
    private double interpolateValue(double previous, int current) {
        return interpolateValue(previous, current, 0.2); // Default speed
    }

    /**
     * Smooth interpolation for gradual updates with custom speed.
     */
    private double interpolateValue(double previous, int current, double speed) {
        return previous + (current - previous) * speed;
    }

    /**
     * Determines the health bar color based on percentage and applies flickering.
     */
    private Color getFlickeringColor(double percentage, long currentTime) {
        Color baseColor;
        long flickerSpeed = 0;
        boolean flickering = false;

        if (percentage > 0.66) {
            baseColor = Color.GREEN; // No flickering when health is above 66%
        } else if (percentage > 0.33) {
            baseColor = Color.ORANGE;
            flickerSpeed = 350; // Flickers every 500ms
            flickering = (currentTime / flickerSpeed) % 2 == 0;
        } else {
            baseColor = Color.RED;
            flickerSpeed = 150; // Flickers faster every 250ms
            flickering = (currentTime / flickerSpeed) % 2 == 0;
        }

        Color brighterColor = mBrighterColorMap.getOrDefault(baseColor, null);
        if (brighterColor == null) { mBrighterColorMap.put(baseColor, baseColor.brighter()); }

        Color darkerColor = mDarkerColorMap.getOrDefault(baseColor, null);
        if (darkerColor == null) { mDarkerColorMap.put(baseColor, baseColor.darker()); }

        Color flickeredColor = flickering ? brighterColor : darkerColor;
        baseColor = flickeredColor;

        return baseColor;
    }

    /**
     * Draws a transparent health bar border.
     */
    private void drawHealthBarBorder(GraphicsContext gc, double x, double y, double w, double h, Color color) {
        double originalLineWidth = gc.getLineWidth();
        Color originalStroke = (Color) gc.getStroke();

        gc.setStroke(color);
        gc.setLineWidth(4.0);

        int verticalMultiplier = (int) (h * .01);
        int horizontalMultiplier = (int) (w * .02);
        gc.strokeRect(
                x - horizontalMultiplier,
                y - verticalMultiplier,
                w + (horizontalMultiplier * 2),
                h + (verticalMultiplier * 2)
        );

        gc.setLineWidth(originalLineWidth);
        gc.setStroke(originalStroke);
    }
}