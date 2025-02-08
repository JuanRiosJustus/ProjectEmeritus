package test.gamesystems;

import main.game.entity.Entity;
import main.game.systems.AbilitySystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionSystemTest {


    private AbilitySystem abilitySystem;
    private Entity unitEntity;
    private String action;

//    @BeforeEach
//    void setUp() {
//        actionSystem = new ActionSystem();
//        // Based on the units.json
//        String unitID = UnitDatabase.getInstance().create("Dragon", "Testo", false);
//        unitEntity = UnitDatabase.getInstance().get(unitID);
//
//    }

//    @Test
//    void testBaseValueInitialization() {
//        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
//        String actionName = "test_action_swing";
//        int manaCost = 50;
//
//
//        statisticsComponent.addToResource("mana", manaCost);
//        int actionCost = ActionDatabase.getInstance().getBaseResourceCost(actionName, "mana");
//
//        boolean canPay = actionSystem.canPayActionCosts(unitEntity, "mana");
//    }
}
