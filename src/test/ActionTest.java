package test;

import main.game.stores.pools.action.ActionDatabase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ActionTest {

    @Test
    public void test_gets_correct_resources_cost() {
        int flatCost = ActionDatabase.getInstance().getBaseCost("Template", "Mana", 100, 100);
        Assert.assertEquals(5, flatCost);

        int totalCost = ActionDatabase.getInstance().getTotalPercentageCost("Template", "Mana", 100, 100);
        Assert.assertEquals(50, totalCost);

        int currentCost = ActionDatabase.getInstance().getCurrentPercentageCost("Template", "Mana", 50, 100);
        Assert.assertEquals(25, currentCost);

        int missingCost = ActionDatabase.getInstance().getMissingPercentageCost("Template", "Mana", 40, 100);
        Assert.assertEquals(30, missingCost);
    }

    @Test
    public void test_gets_all_required_resources() {
        String action = "Template";
        Set<String> toUse = ActionDatabase.getInstance().getResourcesToUse(action);
        Assert.assertTrue(toUse.contains("Mana"));
        Assert.assertTrue(toUse.contains("Health"));
        Assert.assertTrue(toUse.contains("Stamina"));

        Set<String> toDamage = ActionDatabase.getInstance().getResourcesToDamage(action);
        Assert.assertTrue(toDamage.contains("Mana"));
        Assert.assertTrue(toDamage.contains("Health"));
        Assert.assertTrue(toDamage.contains("Stamina"));

        action = "Swipe";
        toDamage = ActionDatabase.getInstance().getResourcesToDamage(action);
        boolean isDamaging = ActionDatabase.getInstance().isDamagingAbility(action);

//        List<Tuple<String, String, Float>> resourceCosts = ActionPool.getInstance().getResourceCosts("Minor Heal");

        Assert.assertTrue(toDamage.contains("Health"));
        Assert.assertTrue(isDamaging);

        action = "Slam";
        toDamage = ActionDatabase.getInstance().getResourcesToDamage(action);
        isDamaging = ActionDatabase.getInstance().isDamagingAbility(action);

        Assert.assertTrue(toDamage.contains("Health"));
        Assert.assertTrue(isDamaging);

        action = "Jab";
        toDamage = ActionDatabase.getInstance().getResourcesToDamage(action);
        isDamaging = ActionDatabase.getInstance().isDamagingAbility(action);

        Assert.assertTrue(toDamage.contains("Health"));
        Assert.assertTrue(isDamaging);
    }
}
