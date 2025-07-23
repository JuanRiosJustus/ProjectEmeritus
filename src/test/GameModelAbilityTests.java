package test;

import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameController;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameModelAbilityTests extends GameTests {

    @ParameterizedTest
    @ValueSource(strings = {"oblation"})
    public void canUseOblationAbility(String ability) {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        String statToMonitor = "health";
        // Setup unit1
        JSONObject response = game.createCpuUnit();
        String unit1 = response.getString("id");
        fillUnitResources(game, unit1, 100);
        getAndSetUnitOnTile(game, unit1, 4, 4);

        // Setup unit2
        response = game.createCpuUnit();
        String unit2 = response.getString("id");
        fillUnitResources(game, unit2, 100);
        getAndSetUnitOnTile(game, unit2, 4, 5);

        // Pause game to wait for ui to show
        simulateUserInactivity(game, 2000);
        game.triggerTurnOrderQueue();

        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unit2)
                .fluentPut("statistic", statToMonitor);
        JSONObject unit2statsBeforeDamage = game.getStatisticsFromUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unit1)
                .fluentPut("ability", ability)
                .fluentPut("row", 4)
                .fluentPut("column", 5)
                .fluentPut("commit", true);
        game.useAbility(request);

        simulateUserInactivity(game, 2000);

        request = new JSONObject()
                .fluentPut("unit_id", unit2)
                .fluentPut("statistic", statToMonitor);
        JSONObject unit2statsAfterDamage = game.getStatisticsFromUnit(request);

        // Assert that the health has been actually lowered
        assertTrue(unit2statsBeforeDamage.getIntValue("current") > unit2statsAfterDamage.getIntValue("current"));

        request = new JSONObject()
                .fluentPut("unit_id", unit2);
        JSONObject unit2tags = game.getTagsFromUnit(request);
//        assertFalse(unit2tags.isEmpty());

        stopAndEndGame(game, 3000);
    }
}
