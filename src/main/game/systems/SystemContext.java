package main.game.systems;

import com.alibaba.fastjson2.JSONArray;
import main.game.components.AIComponent;
import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.ActionsComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;

import java.util.ArrayList;
import java.util.List;

public class SystemContext {

    private GameModel mGameModel = null;
    public SystemContext(GameModel gameModel) { mGameModel = gameModel; }

    private String mCurrentUnitID = null;
    private final List<String> mAllUnitIDs = new ArrayList<>();
    private final List<String> mNonControlledUnitIDs = new ArrayList<>();
    private final List<String> mPlayerUnitIDs = new ArrayList<>();
    private final List<String> mUnitsNotActedThisRoundIDs = new ArrayList<>();
    private final List<String> mUnitsNotMovedThisRoundIDs = new ArrayList<>();
    private final List<String> mNonPlayerUnitsNotActedThisRoundIDs = new ArrayList<>();
    private final List<String> mNonPlayerUnitsNotMovedThisRoundIDs = new ArrayList<>();
    private final List<String> mAllTileEntityIDs = new ArrayList<>();
    public static SystemContext create(GameModel gameModel) {
        SystemContext systemContext = new SystemContext(gameModel);

//        Set<String> unitIDs = new HashSet<>(gameModel.getGameState().getAllUnits().toJavaList(String.class));
        JSONArray unitIDs = gameModel.getSpeedQueue().getUnits();
//        Set<String> unitIDs = new HashSet<>(gameModel.getInitiativeQueue().getTurnOrder());

        systemContext.mCurrentUnitID = gameModel.getSpeedQueue().peek();

        for (int index = 0; index < unitIDs.size(); index++) {
            String unitID = unitIDs.getString(index);
            if (unitID == null || unitID.isEmpty()) { continue; }
            Entity unitEntity = EntityStore.getInstance().get(unitID);
            if (unitEntity == null) { continue; }

            ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
            AIComponent aiComponent = unitEntity.get(AIComponent.class);

            boolean isEntityAI = aiComponent.isAI();
            if (isEntityAI) {
                systemContext.mNonControlledUnitIDs.add(unitID);
            } else {
                systemContext.mPlayerUnitIDs.add(unitID);
            }

            systemContext.mAllUnitIDs.add(unitID);

            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            boolean hasNotMoved = !actionsComponent.hasFinishedMoving();
            if (hasNotMoved) {
                systemContext.mUnitsNotMovedThisRoundIDs.add(unitID);
                if (isEntityAI) {
                    systemContext.mNonPlayerUnitsNotMovedThisRoundIDs.add(unitID);
                }
            }

            AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
            boolean hasNotUsedAbility = !actionsComponent.hasFinishedUsingAbility();
            if (hasNotUsedAbility) {
                systemContext.mUnitsNotActedThisRoundIDs.add(unitID);
                if (isEntityAI) {
                    systemContext.mNonPlayerUnitsNotActedThisRoundIDs.add(unitID);
                }
            }
        }


        systemContext.mAllTileEntityIDs.addAll(gameModel.getTileMap().getAllTileEntityIDs());

        return systemContext;






//        SystemContext systemContext = new SystemContext(gameModel);
//
////        Set<String> unitIDs = new HashSet<>(gameModel.getGameState().getAllUnits().toJavaList(String.class));
//        List<String> unitIDs = gameModel.getSpeedQueue().turnOrder();
////        Set<String> unitIDs = new HashSet<>(gameModel.getInitiativeQueue().getTurnOrder());
//
//        systemContext.mCurrentUnit = gameModel.getSpeedQueue().peek();
//
//        for (String unitID : unitIDs) {
//            if (unitID == null || unitID.isEmpty()) { continue; }
//            Entity unitEntity = EntityStore.getInstance().get(unitID);
//            if (unitEntity == null) { continue; }
//
//            ActionsComponent actionsComponent = unitEntity.get(ActionsComponent.class);
//            AIComponent aiComponent = unitEntity.get(AIComponent.class);
//
//            boolean isEntityAI = aiComponent.isAI();
//            if (isEntityAI) {
//                systemContext.mNonControlledUnitIDs.add(unitID);
//            } else {
//                systemContext.mPlayerUnitIDs.add(unitID);
//            }
//
//            systemContext.mAllUnitIDs.add(unitID);
//
//            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//            boolean hasNotMoved = !actionsComponent.hasFinishedMoving();
//            if (hasNotMoved) {
//                systemContext.mUnitsNotMovedThisRoundIDs.add(unitID);
//                if (isEntityAI) {
//                    systemContext.mNonPlayerUnitsNotMovedThisRoundIDs.add(unitID);
//                }
//            }
//
//            AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
//            boolean hasNotUsedAbility = !actionsComponent.hasFinishedUsingAbility();
//            if (hasNotUsedAbility) {
//                systemContext.mUnitsNotActedThisRoundIDs.add(unitID);
//                if (isEntityAI) {
//                    systemContext.mNonPlayerUnitsNotActedThisRoundIDs.add(unitID);
//                }
//            }
//        }
//
//
//        systemContext.mAllTileEntityIDs.addAll(gameModel.getTileMap().getAllTileEntityIDs());
//
//        return systemContext;
    }


    public List<String> getAllUnitEntityIDs() { return mAllUnitIDs; }
    public List<String> getAllTileEntityIDs() { return mAllTileEntityIDs; }
    public String getCurrentUnitID() { return mCurrentUnitID; }
    public List<String> getUnitsNotMovedThisRound() { return mUnitsNotMovedThisRoundIDs; }
    public List<String> getNoPlayerUnitsNotMovedThisRound() { return mUnitsNotMovedThisRoundIDs; }
    public List<String> getUnitsNotActedThisRoundIDs() { return mUnitsNotActedThisRoundIDs; }
    public List<String> getNonPlayerUnitsNotActedThisRound() { return mUnitsNotActedThisRoundIDs; }
    public List<String> getNonControlledUnitIDs() { return mNonControlledUnitIDs; }

}
