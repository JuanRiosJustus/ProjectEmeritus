package main.game.main.rendering;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;

import java.util.HashSet;
import java.util.Set;

public class ActionAndMovementPathingRenderer extends Renderer {

    @Override
    public void render(GraphicsContext graphicsContext, RenderContext renderContext) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();

        Entity unitEntity = model.getSpeedQueue().peek();
        if (unitEntity == null) { return; }

        Behavior behavior = unitEntity.get(Behavior.class);
        boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
        boolean isMovementPanelOpen = model.getGameState().isMovementPanelOpen();
        if (behavior.isUserControlled()) {
            if (isActionPanelOpen) { renderUnitActionPathing(graphicsContext, renderContext, unitEntity); }
            if (isMovementPanelOpen) { renderUnitMovementPathing(graphicsContext, renderContext, unitEntity); }
        } else {
            if (isActionPanelOpen) { renderUnitActionPathing(graphicsContext, renderContext, unitEntity); }
            if (isMovementPanelOpen) { renderUnitMovementPathing(graphicsContext, renderContext, unitEntity); }
        }
    }


//    public void render(GraphicsContext graphics, GameModel model, RenderContext context) {
//        Entity unitEntity = model.getSpeedQueue().peek();
//        if (unitEntity == null) { return; }
//
//        Behavior behavior = unitEntity.get(Behavior.class);
//        boolean isActionPanelOpen = model.getGameState().isAbilityPanelOpen();
//        boolean isMovementPanelOpen = model.getGameState().isMovementPanelOpen();
//        if (behavior.isUserControlled()) {
//            if (isActionPanelOpen) { renderUnitActionPathing(graphics, model, unitEntity); }
//            if (isMovementPanelOpen) { renderUnitMovementPathing(graphics, model, unitEntity); }
//        } else {
//            if (isActionPanelOpen) { renderUnitActionPathing(graphics, model, unitEntity); }
//            if (isMovementPanelOpen) { renderUnitMovementPathing(graphics, model, unitEntity); }
//        }
//    }

    private void renderUnitActionPathing(GraphicsContext graphics, RenderContext renderContext, Entity unitEntity) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
        Entity targetTile = abilityComponent.getStagedTileTargeted();
        Set<Entity> actionRNG = abilityComponent.getStageTiledRange();
        Set<Entity> actionLOS = abilityComponent.getStagedTileLineOfSight();
        Set<Entity> actionAOE = abilityComponent.getStagedTileAreaOfEffect();

        Set<Entity> aoeAndLos = new HashSet<>();
        aoeAndLos.addAll(actionAOE);
        aoeAndLos.addAll(actionLOS);

        Color background = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1;
        Color foreground = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;

        renderTileSet(graphics, renderContext, actionRNG, background, foreground, aoeAndLos);

        background = ColorPalette.TRANSLUCENT_GREEN_LEVEL_1;
        foreground = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile != null && !actionRNG.contains(targetTile)) {
            background = ColorPalette.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPalette.TRANSLUCENT_RED_LEVEL_3;
        }

        renderTileSet(graphics, renderContext, actionLOS, background, background, actionAOE);
        renderTileSet(graphics, renderContext, actionAOE, background, foreground);
    }

    private void renderUnitMovementPathing(GraphicsContext graphics, RenderContext renderContext, Entity unitEntity) {
        GameModel model = renderContext.getGameModel();
        String camera = renderContext.getCamera();
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Set<Entity> movementRange = movementComponent.getStagedTileRange();
        Set<Entity> movementPath = movementComponent.getStagedTilePath();
        Entity targetTile = movementComponent.getStagedNextTile();

        Color background = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_1;
        Color foreground = ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3;

        renderTileSet(graphics, renderContext, movementRange, background, foreground, movementPath);

        background = ColorPalette.TRANSLUCENT_GREEN_LEVEL_1;
        foreground = ColorPalette.TRANSLUCENT_GREEN_LEVEL_3;
        if (targetTile == null || !movementRange.contains(targetTile)) {
            background = ColorPalette.TRANSLUCENT_RED_LEVEL_1;
            foreground = ColorPalette.TRANSLUCENT_RED_LEVEL_3;
        }

        renderTileSet(graphics, renderContext, movementPath, background, foreground);
    }
}
