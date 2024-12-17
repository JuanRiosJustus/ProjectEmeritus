package test.gamesystems;

import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stats.StatNode;
import main.game.stores.pools.UnitDatabase;
import main.game.stores.pools.action.ActionDatabase;
import main.game.systems.ActionSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionSystemTest {


    private ActionSystem actionSystem;
    private Entity unitEntity;
    private String action;

    @BeforeEach
    void setUp() {
        actionSystem = new ActionSystem();
        // Based on the units.json
        String unitID = UnitDatabase.getInstance().create("Dragon", "Testo", false);
        unitEntity = UnitDatabase.getInstance().get(unitID);

    }

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
