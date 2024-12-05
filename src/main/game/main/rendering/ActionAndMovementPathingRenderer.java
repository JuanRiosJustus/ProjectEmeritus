package main.game.main.rendering;

import main.game.components.ActionComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class ActionAndMovementPathingRenderer extends Renderer {
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Entity unitEntity = model.getSpeedQueue().peek();
        if (unitEntity == null) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        boolean isActionPanelOpen = model.getGameState().isActionPanelOpen();
        boolean isMovementPanelOpen = model.getGameState().isMovementPanelOpen();
        if (behavior.isUserControlled()) {
            if (isActionPanelOpen) { renderUnitActionPathing(graphics, model, unitEntity); }
            if (isMovementPanelOpen) { renderUnitMovementPathing(graphics, model, unitEntity); }
        } else {
            if (isActionPanelOpen) { renderUnitActionPathing(graphics, model, unitEntity); }
            if (isMovementPanelOpen) { renderUnitMovementPathing(graphics, model, unitEntity); }
        }
    }

    private void renderUnitActionPathing(Graphics graphics, GameModel model, Entity unitEntity) {
        ActionComponent actionComponent = unitEntity.get(ActionComponent.class);
        Entity targetTile = actionComponent.getStagedTileTargeted();
        Set<Entity> actionRNG = actionComponent.getTilesInStagedRange();
        Set<Entity> actionLOS = actionComponent.getTilesInStagedLineOfSight();
        Set<Entity> actionAOE = actionComponent.getTilesInStagedAreaOfEffect();

        Set<Entity> aoeAndLos = new HashSet<>();
        aoeAndLos.addAll(actionAOE);
        aoeAndLos.addAll(actionLOS);


        mRendererUtils.renderTileSet(
                graphics,
                model,
                actionRNG,
                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1,
                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3,
                aoeAndLos
        );

        Color background = ColorPalette.TRANSLUCENT_GREEN_LEVEL_1;
        Color foreground = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile != null && !actionRNG.contains(targetTile)) {
            background = ColorPalette.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPalette.TRANSLUCENT_RED_LEVEL_3;
        }

        mRendererUtils.renderTileSet(
                graphics,
                model,
                actionLOS,
                background,
                background,
                actionAOE
        );

        mRendererUtils.renderTileSet(graphics, model, actionAOE, background, foreground);
    }

    private void renderUnitMovementPathing(Graphics graphics, GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Set<Entity> movementRange = movementComponent.getTileInStagedRange();
        Set<Entity> movementPath = movementComponent.getTilesInStagedPath();
        Entity targetTile = movementComponent.getStagedNextTile();

        mRendererUtils.renderTileSet(
                graphics,
                model,
                movementRange,
                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1,
                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3,
                movementPath
        );

        Color background = ColorPalette.TRANSLUCENT_GREEN_LEVEL_1;
        Color foreground = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile == null || !movementRange.contains(targetTile)) {
            background = ColorPalette.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPalette.TRANSLUCENT_RED_LEVEL_3;
        }
        System.out.println(movementRange.contains(targetTile));

        mRendererUtils.renderTileSet(
                graphics,
                model,
                movementPath,
                background,
                foreground,
                movementRange
        );
    }
}
