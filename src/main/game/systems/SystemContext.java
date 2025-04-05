package main.game.systems;

import main.game.components.AbilityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;

import java.util.ArrayList;
import java.util.List;

public class SystemContext {

    private GameModel mGameModel = null;
    public SystemContext(GameModel gameModel) { mGameModel = gameModel; }

    private String mCurrentUnit = null;
    private final List<String> mAllUnitIDs = new ArrayList<>();
    private final List<String> mNonPlayerUnitIDs = new ArrayList<>();
    private final List<String> mPlayerUnitIDs = new ArrayList<>();
    private final List<String> mUnitsNotActedThisRoundIDs = new ArrayList<>();
    private final List<String> mUnitsNotMovedThisRoundIDs = new ArrayList<>();
    private final List<String> mNonPlayerUnitsNotActedThisRoundIDs = new ArrayList<>();
    private final List<String> mNonPlayerUnitsNotMovedThisRoundIDs = new ArrayList<>();
    private final List<String> mAllTileEntityIDs = new ArrayList<>();
    public static SystemContext create(GameModel gameModel) {
        SystemContext systemContext = new SystemContext(gameModel);

        List<String> unitIDs = gameModel.getSpeedQueue().getAllUnitIDs();

        systemContext.mCurrentUnit = gameModel.getSpeedQueue().peek();

        for (String unitID : unitIDs) {
            if (unitID == null || unitID.isEmpty()) { continue; }
            Entity unitEntity = EntityStore.getInstance().get(unitID);
            if (unitEntity == null) { continue; }

            Behavior behavior = unitEntity.get(Behavior.class);

            boolean isPlayerUnit = behavior.isUserControlled();
            if (isPlayerUnit) {
                systemContext.mPlayerUnitIDs.add(unitID);
            } else {
                systemContext.mNonPlayerUnitIDs.add(unitID);
            }
            systemContext.mAllUnitIDs.add(unitID);

            MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
            boolean hasNotMoved = !movementComponent.hasMoved();
            if (hasNotMoved) {
                systemContext.mUnitsNotMovedThisRoundIDs.add(unitID);
                if (!isPlayerUnit) {
                    systemContext.mNonPlayerUnitsNotMovedThisRoundIDs.add(unitID);
                }
            }

            AbilityComponent abilityComponent = unitEntity.get(AbilityComponent.class);
            boolean hasNotActed = !abilityComponent.hasActed();
            if (hasNotActed) {
                systemContext.mUnitsNotActedThisRoundIDs.add(unitID);
                if (!isPlayerUnit) {
                    systemContext.mNonPlayerUnitsNotActedThisRoundIDs.add(unitID);
                }
            }
        }


        systemContext.mAllTileEntityIDs.addAll(gameModel.getTileMap().getAllTileEntityIDs());

        return systemContext;
    }


    public List<String> getAllUnitEntityIDs() { return mAllUnitIDs; }
    public List<String> getAllTileEntityIDs() { return mAllTileEntityIDs; }
    public String getCurrentUnitID() { return mCurrentUnit; }
    public List<String> getUnitsNotMovedThisRound() { return mUnitsNotMovedThisRoundIDs; }
    public List<String> getNoPlayerUnitsNotMovedThisRound() { return mUnitsNotMovedThisRoundIDs; }
    public List<String> getUnitsNotActedThisRoundIDs() { return mUnitsNotActedThisRoundIDs; }
    public List<String> getNonPlayerUnitsNotActedThisRound() { return mUnitsNotActedThisRoundIDs; }

}
