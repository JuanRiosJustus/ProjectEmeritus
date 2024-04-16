package test;

import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.game.stores.pools.unit.UnitPool;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class AbilityTest {
    @Test
    public void testItemStatsAreLinked() {
        Ability ability = AbilityPool.getInstance().getAbility("Panic");

        Entity entity = EntityFactory.create();
        entity.add(new Statistics(Map.of("Health", 100)));

        int cost = ability.getCost(entity, "Health");
        Assert.assertEquals(cost, 25);
    }

    @Test
    public void testAbility_panic() {
        String uuid = UnitPool.getInstance().getTestUnit(1);
        Entity entity = UnitPool.getInstance().get(uuid);

        Statistics statistics = entity.get(Statistics.class);
        Ability ability = AbilityPool.getInstance().getAbility("Panic");

        int damage = ability.getDamage(entity, "Health");
        Assert.assertEquals(damage, 25);
    }
}
