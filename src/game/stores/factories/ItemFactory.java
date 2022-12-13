package game.stores.factories;

import constants.Constants;
import game.components.Name;
import game.components.statistics.Statistics;
import game.entity.Entity;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class ItemFactory {

    public static final List<Entity> list = new ArrayList<>();
    private static final SplittableRandom random = new SplittableRandom();

    public static Entity create(int mins, int maxs) {
        Entity item = new Entity();

        String type = RandomUtils.getRandomFrom(new String[] {
                "Sword", "Staff", "Wand", "Knife", "Dagger", "Mace"
        });

        String name = RandomUtils.createRandomName(3, 6);
        item.add(new Name(type + " of " + name));

        Statistics stats = new Statistics();

        if (random.nextBoolean()) { stats.putScalar(Constants.MAGICAL_ATTACK, random.nextInt(mins, maxs)); }

        if (random.nextBoolean()) { stats.putScalar(Constants.MAGICAL_DEFENSE, random.nextInt(mins, maxs)); }

        if (random.nextBoolean()) { stats.putScalar(Constants.PHYSICAL_ATTACK, random.nextInt(mins, maxs)); }

        if (random.nextBoolean()) { stats.putScalar(Constants.PHYSICAL_DEFENSE, random.nextInt(mins, maxs)); }

        item.add(stats);

        list.add(item);

        return item;
    }
}
