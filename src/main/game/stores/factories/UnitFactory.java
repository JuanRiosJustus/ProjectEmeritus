package main.game.stores.factories;

import main.game.components.*;
import main.game.components.behaviors.AiBehavior;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.unit.UnitPool;
import main.game.stores.pools.unit.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitFactory {

    public static final List<Entity> list = new ArrayList<>();

    public static Entity create(String name) {
        return create(name, false);
    }

    public static Entity load(String path) {
        // try {
        //     FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
        //     OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        //     BufferedWriter bw = new BufferedWriter(osw, 512);
        //     outputStream = new PrintWriter(bw);
        // } catch (Exception ex) {
        //     System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
        // }
        return null;
    }
    
    public static Entity create(String name, boolean controlled) {

        Entity unit = new Entity();

        if (controlled) {
            unit.add(new UserBehavior());
        } else {
            unit.add(new AiBehavior());
        }

        unit.add(new ActionManager());
        unit.add(new MovementTrack());
        unit.add(new MovementManager());
        unit.add(new OverlayAnimation());

        unit.add(new StatusEffects());
        unit.add(new Inventory());
        // unit.add(new Level());

        String simplified = name.toLowerCase()
                .replaceAll(" ", "")
                .replaceAll("_", "");

        int id = AssetPool.getInstance().getUnitAnimation(simplified);
        unit.add(AssetPool.getInstance().getAnimation(id));

        Unit template = UnitPool.getInstance().getUnit(name.toLowerCase());

        unit.add(new Statistics(template));
        unit.add(new Abilities(template));
        unit.add(new Type(template));
        unit.add(new NameTag(template.name));

        // JsonWriter.saveUnit(".", unit);
        list.add(unit);
        return unit;
    }

    // public static Entity create2(String name, boolean controlled) {

    //     Entity unit = Entity.newBuilder()
    //                         .add(controlled ? new UserBehavior() : new AiBehavior())
    //                         .add(new ActionManager())
    //                         .add(new MovementTrack())
    //                         .add(new MovementManager())
    //                         .add(new OverlayAnimation())
    //                         .add(new StatusEffects())
    //                         .add(new Inventory())
    //                         .add(() -> { })

    //     if (controlled) {
    //         unit.add(new UserBehavior());
    //     } else {
    //         unit.add(new AiBehavior());
    //     }

    //     unit.add(new ActionManager());
    //     unit.add(new MovementTrack());
    //     unit.add(new MovementManager());
    //     unit.add(new OverlayAnimation());

    //     unit.add(new StatusEffects());
    //     unit.add(new Inventory());
    //     // unit.add(new Level());

    //     String simplified = name.toLowerCase()
    //             .replaceAll(" ", "")
    //             .replaceAll("_", "");

    //     int id = AssetPool.instance().getUnitAnimation(simplified);
    //     unit.add(AssetPool.instance().getAnimation(id));

    //     Unit template = UnitPool.instance().getUnit(name.toLowerCase());

    //     unit.add(new Statistics(template));
    //     unit.add(new Abilities(template));
    //     unit.add(new Type(template));
    //     unit.add(new NameTag(template.name));

    //     // JsonWriter.saveUnit(".", unit);
    //     list.add(unit);
    //     return unit;
    // }
}
