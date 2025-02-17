package main.game.main.rendering;

import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.ColorPalette;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HealthBarRenderer extends Renderer {

    private final Map<Entity, Double> previousHealthMap = new HashMap<>();
    private final Map<Entity, Double> previousManaMap = new HashMap<>();
    private final Map<Entity, Double> previousStaminaMap = new HashMap<>();

    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Graphics2D g2 = (Graphics2D) graphics;

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Iterate through each tile with a unit
        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String unitID = tile.getUnitID();
            Entity unit = getEntityWithID(unitID);
//            Entity unit = tile.getUnit();
            if (unit == null) { return; } // This can happen when a unit dies
            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
            MovementComponent movementComponent = unit.get(MovementComponent.class);

            int width = model.getGameState().getSpriteWidth();
            int height = model.getGameState().getSpriteHeight();
            int x = movementComponent.getX();
            int y = movementComponent.getY();
            Point position = calculateWorldPosition(model, x, y, width, height);

            int tileX = position.x;
            int tileY = position.y;

            // Health bar dimensions
            int healthBarWidth = (int) (width * 0.8); // 80% of the tile width
            int healthBarHeight = (int) (height * 0.075); // 8% of the tile height
            int healthBarX = tileX + (width - healthBarWidth) / 2; // Centered horizontally
            int healthBarY = tileY - healthBarHeight - (int) (height * 0.1); // Just above the tile

            // Render health bar
            Color color = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
            renderResourceBar(g2, unit, statisticsComponent.getCurrentHealth(), statisticsComponent.getTotalHealth(),
                    healthBarX, healthBarY, healthBarWidth, healthBarHeight, color, currentTime, previousHealthMap);

            // **NEW: Draw transparent border around health bar**
            Color borderColor = ColorPalette.TRANSLUCENT_WHITE_LEVEL_1;
            drawTransparentHealthBarBorder(g2, healthBarX, healthBarY, healthBarWidth, healthBarHeight, borderColor);

//            // Mana and stamina bar dimensions
//            int resourceBarWidth = (int) (healthBarWidth * 0.75); // 75% of health bar width
//            int resourceBarHeight = (int) (healthBarHeight * 0.75); // Half the height of health bar
//            int resourceBarX = healthBarX + (healthBarWidth - resourceBarWidth) / 2; // Centered under the health bar
//
//            // Render mana bar below health bar
//            int manaBarY = healthBarY + healthBarHeight + (int) (height * 0.01); // Slight offset below health barColor
//            color = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;
//            renderResourceBar(g2, unit, statisticsComponent.getCurrentMana(), statisticsComponent.getTotalMana(),
//                    resourceBarX, manaBarY, resourceBarWidth, resourceBarHeight, color, currentTime, previousManaMap);
//
//            // Render stamina bar below mana bar
//            int staminaBarY = manaBarY + resourceBarHeight + (int) (height * 0.005); // Slight offset below mana bar
//            color = ColorPalette.TRANSLUCENT_AMBER_LEVEL_3;
//            renderResourceBar(g2, unit, statisticsComponent.getCurrentStamina(), statisticsComponent.getTotalStamina(),
//                    resourceBarX, staminaBarY, resourceBarWidth, resourceBarHeight, color, currentTime, previousStaminaMap);
        });
    }


    /**
     * Draws a transparent rectangle around the health bar.
     *
     * @param g2 Graphics2D object
     * @param x X coordinate of the health bar
     * @param y Y coordinate of the health bar
     * @param width Width of the health bar
     * @param height Height of the health bar
     */
    private void drawTransparentHealthBarBorder(Graphics2D g2, int x, int y, int width, int height, Color color) {
        Stroke previousStroke = g2.getStroke();

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2)); // Set thickness
        g2.drawRect(x - 2, y - 2, width + 4, height + 4); // Slightly larger than the health bar

        g2.setStroke(previousStroke); // Restore previous stroke settings
    }

    public void renderV1(Graphics graphics, GameModel model, RenderContext context) {
        Graphics2D g2 = (Graphics2D) graphics;

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Iterate through each tile with a unit
        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            String unitID = tile.getUnitID();
            Entity unit = EntityStore.getInstance().get(unitID);
//            Entity unit = tile.getUnit();
            if (unit == null) { return; } // This can happen when a unit dies
            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
            MovementComponent movementComponent = unit.get(MovementComponent.class);

            int width = model.getGameState().getSpriteWidth();
            int height = model.getGameState().getSpriteHeight();
            int x = movementComponent.getX();
            int y = movementComponent.getY();
            Point position = calculateWorldPosition(model, x, y, width, height);

            int tileX = position.x;
            int tileY = position.y;

            // Health bar dimensions
            int healthBarWidth = (int) (width * 0.8); // 80% of the tile width
            int healthBarHeight = (int) (height * 0.075); // 8% of the tile height
            int healthBarX = tileX + (width - healthBarWidth) / 2; // Centered horizontally
            int healthBarY = tileY - healthBarHeight - (int) (height * 0.1); // Just above the tile

            // Render health bar
            Color color = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
            renderResourceBar(g2, unit, statisticsComponent.getCurrentHealth(), statisticsComponent.getTotalHealth(),
                    healthBarX, healthBarY, healthBarWidth, healthBarHeight, color, currentTime, previousHealthMap);

            // Mana and stamina bar dimensions
            int resourceBarWidth = (int) (healthBarWidth * 0.75); // 75% of health bar width
            int resourceBarHeight = (int) (healthBarHeight * 0.75); // Half the height of health bar
            int resourceBarX = healthBarX + (healthBarWidth - resourceBarWidth) / 2; // Centered under the health bar

            // Render mana bar below health bar
            int manaBarY = healthBarY + healthBarHeight + (int) (height * 0.01); // Slight offset below health bar
            color = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;
            renderResourceBar(g2, unit, statisticsComponent.getCurrentMana(), statisticsComponent.getTotalMana(),
                    resourceBarX, manaBarY, resourceBarWidth, resourceBarHeight, color, currentTime, previousManaMap);

            // Render stamina bar below mana bar
            int staminaBarY = manaBarY + resourceBarHeight + (int) (height * 0.005); // Slight offset below mana bar
            color = ColorPalette.TRANSLUCENT_AMBER_LEVEL_3;
            renderResourceBar(g2, unit, statisticsComponent.getCurrentStamina(), statisticsComponent.getTotalStamina(),
                    resourceBarX, staminaBarY, resourceBarWidth, resourceBarHeight, color, currentTime, previousStaminaMap);
        });
    }

    private void renderResourceBar(Graphics2D g2, Entity unit, int current, int max, int x, int y, int width, int height,
                                   Color baseColor, long currentTime, Map<Entity, Double> previousValueMap) {
        if (current >= max) {
//            return; // Skip full resources
        }

        // Get or initialize the previous value
        double previousValue = previousValueMap.getOrDefault(unit, (double) current);

        // Gradually interpolate the value
        previousValue = interpolateValue(previousValue, current);
        previousValueMap.put(unit, previousValue);

        // Calculate the fill width
        int fillWidth = (int) ((previousValue / max) * width);

        // Determine the bar color and apply blinking effect
        Color barColor = getBarColor(baseColor, previousValue / max, currentTime);

        // Draw background bar
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, width, height);


        // Draw resource fill
        g2.setColor(barColor);
        g2.fillRect(x, y, fillWidth, height);

        // Draw border
        g2.setStroke(new BasicStroke(2)); // Adjust thickness if needed
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);
        g2.setStroke(new BasicStroke(1)); // Reset stroke to default
    }

    private double interpolateValue(double previousValue, double currentValue) {
        double easingSpeed = 0.1; // Adjust this value to control the easing speed
        double delta = currentValue - previousValue;
        return previousValue + delta * easingSpeed;
    }

    private Color getBarColor(Color baseColor, double percentage, long currentTime) {
        if (percentage > 0.66) {
            return baseColor; // No blinking for high values
        } else if (percentage > 0.33) {
            // Medium-speed blinking
            int blinkSpeed = 1000; // Medium cycle
            float phase = (float) ((Math.sin((currentTime % blinkSpeed) / (double) blinkSpeed * Math.PI * 2) + 1) / 2);
            return adjustBrightness(baseColor, 0.7f + phase * 0.3f);
        } else {
            // Faster blinking for low values
            int blinkSpeed = 400; // Faster cycle
            float phase = (float) ((Math.sin((currentTime % blinkSpeed) / (double) blinkSpeed * Math.PI * 2) + 1) / 2);
            return adjustBrightness(baseColor, 0.6f + phase * 0.4f);
        }
    }

    private Color adjustBrightness(Color color, float factor) {
        int red = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
        int green = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
        int blue = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
        return new Color(red, green, blue);
    }

    public void cleanup() {
        previousHealthMap.clear();
        previousManaMap.clear();
        previousStaminaMap.clear();
    }
}