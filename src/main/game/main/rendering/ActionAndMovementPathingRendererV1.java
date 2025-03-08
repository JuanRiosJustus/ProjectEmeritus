package main.game.main.rendering;

import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPaletteV1;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class ActionAndMovementPathingRendererV1 extends RendererV1 {
    @Override
    public void render(Graphics graphics, GameModel model, RenderContext context) {
        Entity unitEntity = model.getSpeedQueue().peek();
        if (unitEntity == null) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
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
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        Entity targetTile = abilityComponent.getStagedTileTargeted();
        Set<Entity> actionRNG = abilityComponent.getStageTiledRange();
        Set<Entity> actionLOS = abilityComponent.getStagedTileLineOfSight();
        Set<Entity> actionAOE = abilityComponent.getStagedTileAreaOfEffect();

        Set<Entity> aoeAndLos = new HashSet<>();
        aoeAndLos.addAll(actionAOE);
        aoeAndLos.addAll(actionLOS);

        Color background = ColorPaletteV1.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1;
        Color foreground = ColorPaletteV1.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;

        mRendererUtilsV1.renderTileSet(graphics, model, actionRNG, background, foreground, aoeAndLos);

        background = ColorPaletteV1.TRANSLUCENT_GREEN_LEVEL_1;
        foreground = ColorPaletteV1.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile != null && !actionRNG.contains(targetTile)) {
            background = ColorPaletteV1.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPaletteV1.TRANSLUCENT_RED_LEVEL_3;
        }

        mRendererUtilsV1.renderTileSet(graphics, model, actionLOS, background, background, actionAOE);
        mRendererUtilsV1.renderTileSet(graphics, model, actionAOE, background, foreground);
    }

    private void renderUnitMovementPathing(Graphics graphics, GameModel model, Entity unitEntity) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Set<Entity> movementRange = movementComponent.getStagedTileRange();
        Set<Entity> movementPath = movementComponent.getStagedTilePath();
        Entity targetTile = movementComponent.getStagedNextTile();

        Color background = ColorPaletteV1.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1;
        Color foreground = ColorPaletteV1.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;

        mRendererUtilsV1.renderTileSet(graphics, model, movementRange, background, foreground, movementPath);

        background = ColorPaletteV1.TRANSLUCENT_GREEN_LEVEL_1;
        foreground = ColorPaletteV1.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile == null || !movementRange.contains(targetTile)) {
            background = ColorPaletteV1.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPaletteV1.TRANSLUCENT_RED_LEVEL_3;
        }

        mRendererUtilsV1.renderTileSet(graphics, model, movementPath, background, foreground);
    }
}
