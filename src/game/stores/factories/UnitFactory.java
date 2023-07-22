package game.stores.factories;

import constants.Constants;
import game.components.*;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Level;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.stats.node.StatsNode;
import game.stats.node.StatsNode;
import game.stores.pools.AssetPool;
import game.stores.pools.unit.UnitPool;
import ouput.JsonWriter;
import utils.RandomUtils;
import utils.StringUtils;
import game.stores.pools.unit.Unit;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.cliftonlabs.json_simple.JsonObject;

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

        int id = AssetPool.instance().getUnitAnimation(simplified);
        unit.add(AssetPool.instance().getAnimation(id));

        Unit template = UnitPool.instance().getUnit(name.toLowerCase());

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
