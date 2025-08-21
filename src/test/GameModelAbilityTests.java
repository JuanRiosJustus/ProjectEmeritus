package test;

import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameController;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelAbilityTests extends GameTests {

    @ParameterizedTest
    @ValueSource(strings = {"oblation", "energy_wave", "slash"})
    public void canUseDamagingRangedAbility(String ability) {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        String statToMonitor = "health";

        // Setup unit1
        int row = 4;
        int column = 1;
        JSONObject response = game.createRandomForgettableUnit(true, row, column);
        String unit1 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit1, 100);

        // Setup unit2
        JSONObject abilityData = game.getAbility(new JSONObject().fluentPut("ability", ability));
        int abilityRange = abilityData.getIntValue("range");

        response = game.createRandomForgettableUnit(true, row, column + abilityRange);
        String unit2 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit2, 100);

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
                .fluentPut("row", row)
                .fluentPut("column", column + abilityRange)
                .fluentPut("commit", true);
        game.useAbility(request);

        simulateUserInactivity(game, 1000);

        request = new JSONObject()
                .fluentPut("unit_id", unit2)
                .fluentPut("statistic", statToMonitor);
        JSONObject unit2statsAfterDamage = game.getStatisticsFromUnit(request);

        // Assert that the health has been actually lowered (damaged)
        assertTrue(unit2statsBeforeDamage.getIntValue("current") > unit2statsAfterDamage.getIntValue("current"));

        stopAndEndGame(game, 1000);
    }

    @ParameterizedTest
    @ValueSource(strings = {"oblation", "energy_wave"})
    public void cantUseDamagingRangedAbility(String ability) {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        String statToMonitor = "health";
        // Setup unit1
        JSONObject response = game.createRandomForgettableUnit(true, 4, 2);
        String unit1 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit1, 100);

        // Setup unit2
        response = game.createRandomForgettableUnit(true, 4, 5);
        String unit2 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit2, 100);

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

        // Assert that the health has been actually lowered (damaged)
        assertEquals(unit2statsBeforeDamage.getIntValue("current"), unit2statsAfterDamage.getIntValue("current"));

        stopAndEndGame(game, 3000);
    }






//    @ParameterizedTest
//    @ValueSource(strings = {"oblation"})
//    public void canUseEnergyWaveAbility(String ability) {
//        GameController game = createAndStartGameWithDefaults();
//        game.disableAutoBehavior();
//
//        String statToMonitor = "health";
//        // Setup unit1
//        JSONObject response = game.createRandomForgettableUnit(true, 4, 3);
//        String unit1 = response.getString("id");
//        fillUnitBaseResourcesTo100Percent(game, unit1, 100);
//
//        // Setup unit2
//        response = game.createRandomForgettableUnit(true, 4, 5);
//        String unit2 = response.getString("id");
//        fillUnitBaseResourcesTo100Percent(game, unit2, 100);
//
//        // Pause game to wait for ui to show
//        simulateUserInactivity(game, 2000);
//        game.triggerTurnOrderQueue();
//
//        JSONObject request = new JSONObject()
//                .fluentPut("unit_id", unit2)
//                .fluentPut("statistic", statToMonitor);
//        JSONObject unit2statsBeforeDamage = game.getStatisticsFromUnit(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unit1)
//                .fluentPut("ability", ability)
//                .fluentPut("row", 4)
//                .fluentPut("column", 5)
//                .fluentPut("commit", true);
//        game.useAbility(request);
//
//        simulateUserInactivity(game, 2000);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unit2)
//                .fluentPut("statistic", statToMonitor);
//        JSONObject unit2statsAfterDamage = game.getStatisticsFromUnit(request);
//
//        // Assert that the health has been actually lowered (damaged)
//        assertTrue(unit2statsBeforeDamage.getIntValue("current") > unit2statsAfterDamage.getIntValue("current"));
//
//        stopAndEndGame(game, 3000);
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"energy", "tktokto"})
//    public void cantUseEnergyWaveAbility(String ability) {
//        GameController game = createAndStartGameWithDefaults();
//        game.disableAutoBehavior();
//
//        String statToMonitor = "health";
//        // Setup unit1
//        JSONObject response = game.createRandomForgettableUnit(true, 4, 2);
//        String unit1 = response.getString("id");
//        fillUnitBaseResourcesTo100Percent(game, unit1, 100);
//
//        // Setup unit2
//        response = game.createRandomForgettableUnit(true, 4, 5);
//        String unit2 = response.getString("id");
//        fillUnitBaseResourcesTo100Percent(game, unit2, 100);
//
//        // Pause game to wait for ui to show
//        simulateUserInactivity(game, 2000);
//        game.triggerTurnOrderQueue();
//
//        JSONObject request = new JSONObject()
//                .fluentPut("unit_id", unit2)
//                .fluentPut("statistic", statToMonitor);
//        JSONObject unit2statsBeforeDamage = game.getStatisticsFromUnit(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unit1)
//                .fluentPut("ability", ability)
//                .fluentPut("row", 4)
//                .fluentPut("column", 5)
//                .fluentPut("commit", true);
//        game.useAbility(request);
//
//        simulateUserInactivity(game, 2000);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unit2)
//                .fluentPut("statistic", statToMonitor);
//        JSONObject unit2statsAfterDamage = game.getStatisticsFromUnit(request);
//
//        // Assert that the health has been actually lowered (damaged)
//        assertEquals(unit2statsBeforeDamage.getIntValue("current"), unit2statsAfterDamage.getIntValue("current"));
//
//        stopAndEndGame(game, 3000);
//    }
}
