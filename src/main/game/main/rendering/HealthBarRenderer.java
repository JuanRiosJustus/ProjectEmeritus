package main.game.main.rendering;

import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HealthBarRenderer extends Renderer {

    private final Map<Entity, Double> previousHealthMap = new HashMap<>();

    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Graphics2D g2 = (Graphics2D) graphics;

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Iterate through each tile with a unit
        context.getTilesWithUnits().forEach(tileEntity -> {
            Tile tile = tileEntity.get(Tile.class);
            Entity unit = tile.getUnit();
            StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
            MovementComponent movementComponent = unit.get(MovementComponent.class);

            // Extract current health and max health
            int currentHealth = statisticsComponent.getCurrentHealth();
            int maxHealth = statisticsComponent.getTotalHealth();
            if (currentHealth >= maxHealth) {
//                return;
            }

            // Get or initialize the previous health value
            double previousHealth = previousHealthMap.getOrDefault(unit, (double) currentHealth);

            // Gradually interpolate health
            previousHealth = interpolateHealth(previousHealth, currentHealth);

            // Update the previous health value in the map
            previousHealthMap.put(unit, previousHealth);

            int width = model.getGameState().getSpriteWidth();
            int height = model.getGameState().getSpriteHeight();
            int x = movementComponent.getX();
            int y = movementComponent.getY();
            Point position = calculateWorldPosition(model, x, y, width, height);

            int tileX = position.x;
            int tileY = position.y;

            // Calculate health bar dimensions
            int barWidth = (int) (width * 0.8); // 80% of the tile width
            int barHeight = (int) (height * 0.1); // 10% of the tile height
            int barX = tileX + (width - barWidth) / 2; // Centered horizontally
            int justAboveTileOffset = (int) (height * .01);
            int barY = tileY - barHeight - justAboveTileOffset; // Just above the tile

            // Calculate the width of the health fill with easing
            int healthFillWidth = (int) ((previousHealth / maxHealth) * barWidth);

            // Determine the health bar color and blinking behavior
            double healthPercentage = previousHealth / maxHealth;
            Color healthBarColor;

            if (healthPercentage > 0.66) {
                // Green: No blinking
                healthBarColor = Color.GREEN;
            } else if (healthPercentage > 0.33) {
                // Yellow: Medium-speed blinking
                int blinkSpeed = 1000; // Medium cycle
                float phase = (float) ((Math.sin((currentTime % blinkSpeed) / (double) blinkSpeed * Math.PI * 2) + 1) / 2);
                healthBarColor = adjustBrightness(Color.YELLOW, 0.7f + phase * 0.3f);
            } else {
                // Red: Faster blinking
                int blinkSpeed = 400; // Faster cycle
                float phase = (float) ((Math.sin((currentTime % blinkSpeed) / (double) blinkSpeed * Math.PI * 2) + 1) / 2);
                healthBarColor = adjustBrightness(Color.RED, 0.6f + phase * 0.4f);
            }

            // Draw background bar
            g2.setColor(Color.GRAY);
            g2.fillRect(barX, barY, barWidth, barHeight);

            // Draw health fill
            g2.setColor(healthBarColor);
            g2.fillRect(barX, barY, healthFillWidth, barHeight);

            // Draw thicker border
            int borderThickness = 2; // Adjust as needed
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
            g2.setStroke(new BasicStroke(1)); // Reset stroke to default
        });
    }

    private double interpolateHealth(double previousHealth, double currentHealth) {
        double easingSpeed = 0.1; // Adjust this value to control the easing speed
        double delta = currentHealth - previousHealth;
        return previousHealth + delta * easingSpeed;
    }

    /**
     * Adjust the brightness of a color.
     *
     * @param color The original color.
     * @param factor The brightness factor (1.0 = no change, <1 = darker, >1 = brighter).
     * @return A new color with adjusted brightness.
     */
    private Color adjustBrightness(Color color, float factor) {
        int red = Math.min(255, Math.max(0, (int) (color.getRed() * factor)));
        int green = Math.min(255, Math.max(0, (int) (color.getGreen() * factor)));
        int blue = Math.min(255, Math.max(0, (int) (color.getBlue() * factor)));
        return new Color(red, green, blue);
    }

    public void cleanup() {
        // Clear the previous health map when the renderer is cleaned up
        previousHealthMap.clear();
    }
}