package game.stores.factories;

import constants.Constants;
import game.components.*;
import game.components.behaviors.AiBehavior;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Level;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stores.pools.AssetPool;
import game.stores.pools.unit.UnitPool;
import game.stores.pools.unit.UnitTemplate;

import java.util.ArrayList;
import java.util.List;

public class UnitFactory {

    public static final List<Entity> list = new ArrayList<>();

    public static Entity create(String name) {
        return create(name, false);
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
        unit.add(new CombatAnimations());

        unit.add(new Health());
        unit.add(new Energy());
        unit.add(new Level());

        unit.add(new StatusEffects());
        unit.add(new Inventory());

        int id = AssetPool.instance().getUnitAnimation(name.replaceAll(" ", ""));
        unit.add(AssetPool.instance().getAnimation(id));

        UnitTemplate template = UnitPool.instance().getUnit(name);

        unit.add(new Statistics(template));
        unit.add(new MoveSet(template));
        unit.add(new Name(template));
        unit.add(new Types(template));

        unit.get(Health.class).subscribe(unit.get(Statistics.class).getScalarNode(Constants.HEALTH));
        unit.get(Energy.class).subscribe(unit.get(Statistics.class).getScalarNode(Constants.ENERGY));

        list.add(unit);
        return unit;
    }
}
