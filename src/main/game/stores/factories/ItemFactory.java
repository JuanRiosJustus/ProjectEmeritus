package main.game.stores.factories;

import main.game.entity.Entity;
import main.utils.RandomUtils;

import java.util.ArrayList;
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

}
