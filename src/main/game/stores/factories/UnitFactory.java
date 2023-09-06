package main.game.stores.factories;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.unit.UnitPool;
import main.game.stores.pools.unit.Unit;
import main.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class UnitFactory {

    public static final List<Entity> list = new ArrayList<>();

    public static Entity load(JsonObject unitToLoad) {
        return load(unitToLoad, false);
    }

    public static Entity load(JsonObject toLoad, boolean controlled) {
        Entity entity = null;
         try {
             // This is retrieved from unitToLoad
             String unit = (String) toLoad.get("unit");
             String name = (String) toLoad.get("name");
             String uuid = (String) toLoad.get("uuid");
             entity = create(unit, name, uuid,  controlled);
         } catch (Exception ex) {
             System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
         }
        return entity;
    }



    public static Entity create(String unit) {
        return create(unit, false);
    }
    public static Entity create(String unit, boolean controlled) {
        return create(unit, RandomUtils.createRandomName(3, 6), controlled);
    }

    public static Entity create(String unit, String nickname, boolean controlled) {
        return create(unit, nickname, null,  controlled);
    }

    public static Entity create(String species, String nickname, String uuid, boolean controlled) {

        Entity entity = EntityFactory.create(nickname, uuid);

        if (controlled) {
            entity.add(new UserBehavior());
        } else {
            entity.add(new AiBehavior());
        }

        entity.add(new ActionManager());
        entity.add(new Track());
        entity.add(new MovementManager());
        entity.add(new OverlayAnimation());

        entity.add(new Tags());
        entity.add(new Inventory());

        String simplified = species.toLowerCase()
                .replaceAll(" ", "")
                .replaceAll("_", "");

        int id = AssetPool.getInstance().getUnitAnimation(simplified);
        entity.add(AssetPool.getInstance().getAsset(id));

        Unit unit = UnitPool.getInstance().getUnit(species);

        entity.add(new Summary(unit));
        entity.add(new Actions(unit));
        entity.add(new Types(unit));

        // JsonWriter.saveUnit(".", unit);
        list.add(entity);
        return entity;
    }
}
