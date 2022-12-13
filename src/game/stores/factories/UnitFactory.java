package game.stores.factories;

import constants.Constants;
import game.components.*;
import game.components.behaviors.AIBehavior;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Level;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stores.pools.AssetPool;
import game.stores.pools.UnitPool;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            unit.add(new AIBehavior());
        }

        unit.add(new ActionManager());
        unit.add(new Movement());
        unit.add(new CombatAnimations());

        unit.add(new Health());
        unit.add(new Energy());
        unit.add(new Level());

        unit.add(new MoveSet());
        unit.add(new StatusEffects());

        unit.add(new Inventory());

        BufferedImage[] spriteImages = AssetPool.instance().getSpriteAnimation(name.replaceAll(" ", ""));
        unit.add(new SpriteAnimation(spriteImages));

        Map<String, String> template = UnitPool.instance().getStatisticsTemplate(name);
        unit.add(new Statistics(template));

        String value = template.get(Constants.NAME);
        unit.add(new Name(value));

        unit.get(Health.class).subscribe(unit.get(Statistics.class).getScalarNode(Constants.HEALTH));
        unit.get(Energy.class).subscribe(unit.get(Statistics.class).getScalarNode(Constants.ENERGY));
        unit.get(MoveSet.class).subscribe(unit.get(Statistics.class).getStringNode(Constants.ABILITIES));

        list.add(unit);
//        store(unit, creatures);
        return unit;
    }
}
