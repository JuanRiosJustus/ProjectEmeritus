package main.game.stores.factories;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.pools.asset.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static main.game.stores.pools.asset.AssetPool.STRETCH_Y_ANIMATION;
import static main.game.stores.pools.asset.AssetPool.UNITS_SPRITEMAP;

public class UnitFactory {

    public static final List<Entity> list = new ArrayList<>();

//    public static Entity load(JsonObject unitToLoad) {
//        return load(unitToLoad, false);
//    }

    public static Entity load(JsonObject toLoad, boolean controlled) {
        Entity entity = null;
         try {
             String species = (String) toLoad.get("species");
             String name = (String) toLoad.get("name");
             String uuid = (String) toLoad.get("uuid");
             entity = create(species, name, uuid,  controlled);
         } catch (Exception ex) {
             System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
         }
        return entity;
    }



//    public static Entity crxeate(String unit) {
//        return create(unit, false);
//    }

//    public static Entity create(String unit, boolean controlled) {
//        return create(unit, RandomUtils.createRandomName(3, 6), controlled);
//    }

//    public static Entity create(String unit, String nickname, boolean controlled) {
//        return create(unit, nickname, null,  controlled);
//    }

    public static Entity create(String species, String nickname, String uuid, boolean controlled) {

        Entity entity = EntityFactory.create(nickname, uuid);

        if (controlled) {
            entity.add(new UserBehavior());
        } else {
            entity.add(new AiBehavior());
        }

        entity.add(new AbilityManager());
        entity.add(new MovementManager());
        entity.add(new AnimationMovementTrack());
        entity.add(new Overlay());
        entity.add(new Tags());
        entity.add(new Inventory());
        entity.add(new History());

        String simplified = species
                .replaceAll(" ", "")
                .replaceAll("_", "");

        String id = AssetPool.getInstance().createAsset(UNITS_SPRITEMAP, simplified, 0, STRETCH_Y_ANIMATION);
        entity.add(AssetPool.getInstance().getAnimation(id));

//        UnitTemplate unitTemplate = UnitPool.getInstance().getUnitTemplate(species);
//        entity.add(new Summary(unitTemplate));

        // JsonWriter.saveUnit(".", unit);
        list.add(entity);
        return entity;
    }
}
