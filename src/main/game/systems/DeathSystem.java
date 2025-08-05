package main.game.systems;

import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.texts.FloatingTextSystem;

public class DeathSystem extends GameSystem {
    public DeathSystem(GameModel gameModel) {
        super(gameModel);
    }

    public void update(GameModel model, SystemContext systemContext) {
        systemContext.getAllUnitEntityIDs().forEach(unitID -> {

            Entity unitEntity = getEntityWithID(unitID);

            StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);

            int unitHealth = statisticsComponent.getCurrentHealth();
            if (unitHealth > 0) { return; }

            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            String tileEntityID = movementComponent.getCurrentTileID();
            Entity entity = getEntityWithID(tileEntityID);
            if (entity != null) {
                TileComponent tileComponent = entity.get(TileComponent.class);
                tileComponent.removeUnit();
                tileComponent.removetructure();
            }

            model.getSpeedQueue().remove(unitID);
            getEventBus().publish(FloatingTextSystem.createFloatingTextEvent("Dead!", unitID));
        });
    }
}
