package main.ui.panels;

import main.constants.Vector3f;
import main.engine.Engine;
import main.game.components.statistics.StatisticsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.utils.MathUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class HealthBarDrawer {
    private static class HealthBarNode {
        public final Entity unit;
        public final int x;
        public final int y;
        HealthBarNode(Entity unitEntity, int unitX, int unitY) {
            unit = unitEntity;
            x = unitX;
            y = unitY;
        }
    }
    private static class Lerp {
        public float start;
        public float end;
        public float progress;
        public Lerp(float startVal, float endVal) {
            start = startVal;
            end = endVal;
        }
    }
    private final Queue<HealthBarNode> healthBarNodes = new LinkedList<>();
    private final Map<Entity, Lerp> mStartedLerps = new HashMap<>();
    private final Map<Entity, Float>  mCurrentHealthBarWidths = new HashMap<>();
    public void renderHealthBars(Graphics graphics, GameModel model) {
        while (!healthBarNodes.isEmpty()) {
            HealthBarNode healthBarNode = healthBarNodes.poll();
            drawHealthBar(graphics, model, healthBarNode.unit, healthBarNode.x, healthBarNode.y);
        }
    }

    public void stageUnits(Entity unitEntity, int unitX, int unitY) {
        healthBarNodes.add(new HealthBarNode(unitEntity, unitX, unitY));
    }

    private void drawHealthBar(Graphics graphics, GameModel model, Entity unitEntity, int x, int y) {
        int configuredSpriteHeight = model.getGameState().getSpriteHeight();
        int configuredSpriteWidth = model.getGameState().getSpriteWidth();

        // Draw the backdrop (Black-drop) for the health-bar
        graphics.setColor(Color.BLACK);
        int outerHealthBarHeight = (int) (configuredSpriteHeight * .1);
        int outerHealthBarWidth = (int) (configuredSpriteWidth);
        int outerHealthBarX = x;
        int outerHealthBarY = y - outerHealthBarHeight;
        graphics.fillRect(outerHealthBarX, outerHealthBarY, outerHealthBarWidth, outerHealthBarHeight);

        // Draw the colored backdrop for the health-bar
        int innerHealthBarHeight = (int) (outerHealthBarHeight * .75);
        int innerHealthBarWidth = (int) (outerHealthBarWidth * .95);
        Vector3f center = Vector3f.getCenteredVector(
                outerHealthBarX, outerHealthBarY,
                outerHealthBarWidth, outerHealthBarHeight,
                innerHealthBarWidth, innerHealthBarHeight
        );
        int innerHealthBarX = (int) center.x;
        int innerHealthBarY = (int) center.y;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(innerHealthBarX, innerHealthBarY, innerHealthBarWidth , innerHealthBarHeight);

        // Draw the health bar
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int current = statisticsComponent.getCurrentHealth();
        int max = statisticsComponent.getTotalHealth();
        float currentHealthBarWidth = MathUtils.map(current, 0, max, 0, innerHealthBarWidth);
        float oldHealthBarWidth = mCurrentHealthBarWidths.getOrDefault(unitEntity, currentHealthBarWidth);
        // if the current and old health bar widths differ, they have changed
        if (currentHealthBarWidth != oldHealthBarWidth) {
            mStartedLerps.put(unitEntity, new Lerp(oldHealthBarWidth, currentHealthBarWidth));
        }
        mCurrentHealthBarWidths.put(unitEntity, currentHealthBarWidth);
        // If lerping, get that value instead
        currentHealthBarWidth = getHandledLerpValue(unitEntity, currentHealthBarWidth);

        graphics.setColor(Color.RED);
        graphics.fillRect(innerHealthBarX, innerHealthBarY, (int) currentHealthBarWidth, innerHealthBarHeight);
    }
    private float getHandledLerpValue(Entity unitEntity, float defaultValue) {
        Lerp lerp = mStartedLerps.get(unitEntity);
        if (lerp == null) {
            return defaultValue;
        }
        float start = lerp.start;
        float end = lerp.end;
        float currentProgress = lerp.progress;

        // Get Lerped Value
        float lerpValue = Vector3f.lerp(start, end, currentProgress);

        // Speed of lerp and progression
        double pixelsBetweenStartPositionAndEndPosition = Math.abs(start - end);
        double pixelsTraveledThisTick = Engine.getInstance().getDeltaTime() * 50;
        float additionalProgress = (float) (pixelsTraveledThisTick / pixelsBetweenStartPositionAndEndPosition);
        lerp.progress += additionalProgress;

        // remove when weve reached 100 eercent
        if (currentProgress >= 1) {
            mStartedLerps.remove(unitEntity);
        }

        return lerpValue;
    }
}
