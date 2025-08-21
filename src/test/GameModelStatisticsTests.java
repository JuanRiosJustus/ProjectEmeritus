package test;

import com.alibaba.fastjson2.JSONObject;
import main.game.main.GameController;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameModelStatisticsTests extends GameTests {

    @Test
    public void ensureNewUnitsAreCreated() {
        GameController gameController =  createGameWithDefaults(5, 5, true);
        String npc1 = gameController.createCpuUnit().getString("id");
        String npc2 = gameController.createCpuUnit().getString("id");
        assertNotNull(npc1);
        assertNotNull(npc2);
    }

    @Test
    public void testApplyNegativeModification() {
        String resource = "mana";
        GameController game = createGameWithDefaults(5, 5, true);

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        JSONObject before = setUnitAttributeToValue(game, unitID, resource, 200);


        JSONObject request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("bonus", "-10% total " + resource + " to " + resource);
        JSONObject after = game.addStatisticBonus(request);

        assertTrue(before.getFloatValue("total") > after.getFloatValue("total"));
//        assertEquals(before.getFloatValue("base"), after.getFloatValue("base"));
//        assertTrue(before.getFloatValue("bonus") > after.getFloatValue("bonus"));
//        assertTrue(before.getFloatValue("total") > after.getFloatValue("total"));
//        assertNotEquals(before.getFloatValue("current"), after.getFloatValue("current"));
//        assertEquals(before.getFloatValue("missing"),  after.getFloatValue("missing"));
    }


    @Test
    public void testSetUnitAttributeWithFlatValue() {
        GameController game = createGameWithDefaults(5, 5, true);
        game.start();

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");

        // Set the attribute to 100
        int value = 100;
        for (String resource : List.of("health", "stamina", "mana")) {
            JSONObject after = setUnitAttributeToValue(game, unitID, resource, value);
        }

        // Set the attribute to 10
        value = 10;
        for (String resource : List.of("speed")) {
            JSONObject after = setUnitAttributeToValue(game, unitID, resource, value);
        }
    }

    @Test
    public void testFillUnitResourceAttribute() {
        GameController game = createGameWithDefaults(5, 5, true);
        game.start();

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");

        for (String resource : List.of("health", "stamina", "mana")) {
            JSONObject before = setUnitAttributeToValue(game, unitID, resource, 100, true);
        }
    }


    @Test
    public void testResourceMultipliedCorrectly() {
        GameController game = createGameWithDefaults(5, 5, true);
        game.start();

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");

        for (String resource : List.of("health", "stamina", "mana")) {
            JSONObject before = setUnitAttributeToValue(game, unitID, resource, 100, true);
        }

        String resource = "health";
        JSONObject request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("attribute", "health")
                .fluentPut("scaling", "100 to " + resource)
                .fluentPut("source", "setUnitAttributeToValue");
        JSONObject result = game.addStatisticBonus(request);

        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", resource);
        JSONObject after = game.getStatisticsFromUnit(request);
    }

    @Test
    public void testTwoBuffsOverlapCorrectlyFlat() {
        String attribute = "stamina";
        GameController game = createGameWithDefaults(5, 5, true);
        game.start();
//
//        simulateUserPause(game, 2000);

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");

        JSONObject request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject before = game.getStatisticsFromUnit(request);
        JSONObject after = setUnitAttributeToValue(game, unitID, attribute, 100, true);


        // First buff with this name
        request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("name", "test_buff")
                .fluentPut("bonus", "-25% total " + attribute + " to " + attribute)
                .fluentPut("source", "testTwoBuffsOverlapCorrectlyFlat");
        JSONObject result = game.addStatisticBonus(request);
        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject after2 = game.getStatisticsFromUnit(request);
        assertEquals(75, after2.getIntValue("total"));


        // Second buff with the same name
        request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("name", "test_buff")
                .fluentPut("bonus", "25 to " + attribute)
                .fluentPut("source", "testTwoBuffsOverlapCorrectlyFlat");
        result = game.addStatisticBonus(request);

        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        after2 = game.getStatisticsFromUnit(request);
        assertNotEquals(75, after2.getIntValue("total"));
    }


    @Test
    public void doesNotSetResourceValue() {
        int value = Integer.MAX_VALUE;
        String attribute = "health";
        GameController game = GameController.create(5, 5, 400, 400);

        // Setup unit
        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        setUnitStatistic(game, unitID, attribute, value);
    }

    @Test
    public void doesNotSetResourceValueBelow0() {
        int value = Integer.MIN_VALUE;
        String attribute = "health";
        GameController game = createAndStartGameWithDefaults();

        // Setup unit
        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        JSONObject result = setUnitStatistic(game, unitID, attribute, value);

        JSONObject request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject after = game.getStatisticsFromUnit(request);

        assertTrue(after.getIntValue("total") >= 0);
    }

    @Test
    public void doesNotSetResourceValueAbove() {
        int value = Integer.MAX_VALUE;
        String attribute = "health";
        GameController game = GameController.create(5, 5, 400, 400);

        // Setup unit
        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        setUnitStatistic(game, unitID, attribute, value);
    }

    @Test
    public void canSetResourceValueToMaxPossible() {
        int value = 1_000_000_000;
        String attribute = "health";
        GameController game = GameController.create(5, 5, 400, 400);

        // Setup unit
        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        setUnitAttributeToValue(game, unitID, attribute, value);
        setUnitStatistic(game, unitID, attribute, value);
    }

//    @Test
//    public void canSetResourceValue() {
//        int value = -300;
//        String attribute = "health";
//        GameController game = GameController.create(5, 5, 400, 400);
//
//        // Setup unit
//        JSONObject unitData = game.createUnit();
//        String unitID = unitData.getString("unit_id");
//        setUnitAttributeTotalValue(game, unitID, attribute, value);
//        setUnitResourceToToValue(game, unitID, attribute, value);
//    }

    @Test
    public void canPayResourceRequirementsForPotentialDefense() {
        String attribute = "mana";
        GameController game = createGameWithDefaults(10, 10, true);

        // Setup unit
        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");
        JSONObject before = setUnitAttributeToValue(game, unitID, attribute, 100, true);

        // Test function
        JSONObject request = new JSONObject()
                .fluentPut("id", unitID)
                .fluentPut("ability", "potential_defense") // This ability requires at least 5 flat of resource ATM
                .fluentPut("commit", true);
        JSONObject response = game.payAbilityCost(request);

        // Validate behavior, that resources hav declined
        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
        JSONObject after = game.getStatisticsFromUnit(request);
        assertTrue(before.getFloatValue("current") > after.getFloatValue("current"));
        assertTrue(before.getFloatValue("missing") < after.getFloatValue("missing"));
    }

//    @Test
//    public void canPayResourceRequirementsForPotentialOffense() {
//        String attribute = "mana";
//        GameController game = createGameWithDefaults(10, 10, true);
//
//        // Setup unit
//        JSONObject unitData = game.createCpuUnit();
//        String unitID = unitData.getString("id");
//        JSONObject before = setUnitAttributeToValue(game, unitID, attribute, 100, true);
//
//        // Test function
//        JSONObject request = new JSONObject()
//                .fluentPut("id", unitID)
//                .fluentPut("ability", "potential_offense") // This ability requires at least 5 flat of resource ATM
//                .fluentPut("commit", true);
//        JSONObject response = game.payAbilityCost(request);
//
//        // Validate behavior, that resources hav declined
//        request = new JSONObject().fluentPut("id", unitID).fluentPut("attribute", attribute);
//        JSONObject after = game.getStatisticsFromUnit(request);
//        assertTrue(before.getFloatValue("current") > after.getFloatValue("current"));
//        assertTrue(before.getFloatValue("missing") < after.getFloatValue("missing"));
//    }

    @Test
    public void ensureAttachingEquipmentUpdatesStats() {
        GameController game = createGameWithDefaults(10, 10, false);
        game.start();

        JSONObject unitData = game.createCpuUnit();
        String unitID = unitData.getString("id");

        JSONObject request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("statistic", "health");
        JSONObject before = game.getStatisticsFromUnit(request);

        request = new JSONObject()
                .fluentPut("unit_id", unitID)
                .fluentPut("equipment_name", "armor_of_testing_health")
                .fluentPut("equip_slot", "torso");
        JSONObject response = game.attachEquipment(request);

        request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("statistic", "health");
        JSONObject after = game.getStatisticsFromUnit(request);

        assertTrue(before.getIntValue("total") < after.getFloatValue("total"));
        assertTrue(before.getFloatValue("bonus") < after.getFloatValue("bonus"));

        request = new JSONObject().fluentPut("unit_id", unitID);
        response = game.getEquipment(request);
        assertTrue(response.containsValue("armor_of_testing_health"));
    }


//    @Test
//    public void ensureAttachingEquipmentUpdatesStats() {
//        GameController game = createGameWithDefaults(10, 10, false);
//        game.start();
//
//        JSONObject unitData = game.createCpuUnit();
//        String unitID = unitData.getString("id");
//
//        JSONObject request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("statistic", "health");
//        JSONObject before = game.getStatisticsFromUnit(request);
//
//        request = new JSONObject()
//                .fluentPut("unit_id", unitID)
//                .fluentPut("equipment_name", "armor_of_testing_health")
//                .fluentPut("equip_slot", "torso");
//        JSONObject response = game.attachEquipment(request);
//
//        request = new JSONObject().fluentPut("unit_id", unitID).fluentPut("statistic", "health");
//        JSONObject after = game.getStatisticsFromUnit(request);
//
//        assertTrue(before.getIntValue("total") < after.getFloatValue("total"));
//        assertTrue(before.getFloatValue("bonus") < after.getFloatValue("bonus"));
//
//        request = new JSONObject().fluentPut("unit_id", unitID);
//        response = game.getEquipment(request);
//        assertTrue(response.containsValue("armor_of_testing_health"));
//    }


    @Test
    public void canUseAbility() {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        // Setup unit1
        JSONObject response = game.createCpuUnit();
        String unit1 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit1, 100);
        getAndSetUnitOnTile(game, unit1, 4, 4);

        // Setup unit2
        response = game.createCpuUnit();
        String unit2 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit2, 100);
        getAndSetUnitOnTile(game, unit2, 4, 5);

        // Pause game to wait for ui to show
        simulateUserInactivity(game, 2000);
        game.triggerTurnOrderQueue();

        // Test unit 1 damage on unit 2
        JSONObject request = new JSONObject()
                .fluentPut("unit_id", unit1)
                .fluentPut("ability", "magic_pulse")
                .fluentPut("row", 4)
                .fluentPut("column", 5)
                .fluentPut("commit", true);
        game.useAbility(request);

        request = new JSONObject()
                .fluentPut("id", unit2)
                .fluentPut("attribute", "health");
        JSONObject unitStatsAfterDamage = game.getStatisticsFromUnit(request);

        stopAndEndGame(game, 3000);
    }

    @Test
    public void canUseAbilityWithHealthDamageAndTags() {
        GameController game = createAndStartGameWithDefaults();
        game.disableAutoBehavior();

        String statToMonitor = "health";
        // Setup unit1
        JSONObject response = game.createCpuUnit();
        String unit1 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit1, 100);
        getAndSetUnitOnTile(game, unit1, 4, 4);

        // Setup unit2
        response = game.createCpuUnit();
        String unit2 = response.getString("id");
        fillUnitBaseResourcesTo100Percent(game, unit2, 100);
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
                .fluentPut("ability", "magic_pulse_ii")
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
        assertFalse(unit2tags.isEmpty());

        stopAndEndGame(game, 3000);
    }
}
