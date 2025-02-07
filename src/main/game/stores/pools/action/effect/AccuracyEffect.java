package main.game.stores.pools.action.effect;

import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import org.json.JSONObject;

import java.awt.Color;
import java.util.Set;

public class AccuracyEffect extends Effect {
    private float mAccuracy = 0;
    public AccuracyEffect(JSONObject effect) {
        super(effect);
        mAccuracy = effect.getFloat("chance");

    }


    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
        float successChance = mAccuracy;
        boolean success = passesChanceOutOf100(successChance);
        if (!success) {
            for (Entity target : targets) {

                Tile tile = target.get(Tile.class);

                String targetUnitID = tile.getUnitID();
                Entity targetUnit = EntityStore.getInstance().get(targetUnitID);
//                Entity targetUnit = tile.getUnit();

                if (targetUnit == null) { continue; }

                announceWithFloatingTextCentered(model, "Missed!", targetUnit, Color.RED);
            }
        }

        return false;
    }
}
