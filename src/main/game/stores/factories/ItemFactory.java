package main.game.stores.factories;

import main.constants.Constants;
import main.game.components.Identity;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.utils.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SplittableRandom;

public class ItemFactory {

    public static final List<Entity> list = new ArrayList<>();
    private static final SplittableRandom random = new SplittableRandom();

    public static Entity create2(int mins, int maxs) {

        String type = RandomUtils.getRandomFrom(new String[] {
                "Head", "Torso", "Arms", "Legs", "Jewelry1", "Jewelry2"
        });

        String name = RandomUtils.createRandomName(3, 6);
//        item.add(new Identity(type + " of " + name));

        Summary stats = new Summary();

//        if (random.nextBoolean()) { stats.putStatsNode(Constants.HEALTH, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.ENERGY, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.MAGICAL_ATTACK, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.MAGICAL_DEFENSE, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.PHYSICAL_ATTACK, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.PHYSICAL_DEFENSE, random.nextInt(mins, maxs)); }

//        item.add(stats);

//        list.add(item);
//
//        return item;
        return null;
    }

    public static Entity create(int mins, int maxs) {
        String[][] types = new String[][]{
                new String[]{ "Weapon", "Sword", "Staff", "Wand", "Knife", "Dagger", "Mace" },
                new String[]{ "Head", "Helmet" },
                new String[]{ "Torso", "Chainmail", "Platemail", "Coat", "Jacket" },
                new String[]{ "Arms", "Gloves" },
                new String[]{ "Legs", "Pants" },

                new String[]{ "Jewelry", "Ring" },
                new String[]{ "Item", "Test Item" }
        };

        int index = random.nextInt(types.length);
        String[] categoryAndNames = types[index];
        String name = categoryAndNames[random.nextInt(1, categoryAndNames.length)];
        String category = categoryAndNames[0];
        String randomName = RandomUtils.createRandomName(3, 6);

//        String name = RandomUtils.createRandomName(3, 6);
//        Entity entity = EntityFactory.create(type + " of " + name);

        HashMap<String, Integer> map = new HashMap<>();
//        map.put(Constants.)

        for (String stat : Summary.getStatKeys()) {
            if (random.nextBoolean()) { map.put(stat, random.nextInt(mins, maxs)); }
        }

        Entity entity = EntityFactory.create(name + " of " + randomName);
        entity.add(new Summary(map));

//        if (random.nextBoolean()) { stats.putStatsNode(Constants.HEALTH, random.nextInt(mins, maxs)); }
//        if (random.nextBoolean()) { map.put(Constants.HEALTH, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.ENERGY, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.MAGICAL_ATTACK, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.MAGICAL_DEFENSE, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.PHYSICAL_ATTACK, random.nextInt(mins, maxs)); }
//
//        if (random.nextBoolean()) { stats.putStatsNode(Constants.PHYSICAL_DEFENSE, random.nextInt(mins, maxs)); }

//        item.add(stats);
//
//        list.add(item);

        return entity;
    }
}
