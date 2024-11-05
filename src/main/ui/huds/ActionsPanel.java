package main.ui.huds;

import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.ControllerUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.components.OutlineButton;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.OutlineMapPanel;

import java.util.*;

import javax.swing.*;

public class ActionsPanel extends ControllerUI {
    private final OutlineMapPanel mMainContent;
    private String mSelectedAction;
    private Entity mCurrentUnit = null;
    private boolean mIsDirty = false;
    private final ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionsPanel.class);
    private Set<JComponent> usedComponents = new HashSet<>();
    private int mPreviousHashState = 0;

    public ActionsPanel(int width, int height, int x, int y, JButton enter, JButton exit) {
        super(width, height, x, y, enter, exit);

        // Account for bordering
        mMainContent = new OutlineMapPanel(mMainContentWidth, mMainContentHeight, 4);

        add(mMainContent);
        add(getExitButton());
    }

    public void gameUpdate(GameModel model, Entity unitEntity) {
        if (unitEntity == null) { return; }
        mCurrentUnit = unitEntity;

        StatisticsComponent statisticsComponent = mCurrentUnit.get(StatisticsComponent.class);
        List<String> entityActionsList = statisticsComponent.getActions();
        int currentHashState = Objects.hash(entityActionsList.toString(), mCurrentUnit);
        if (currentHashState == mPreviousHashState) { return; }
        mPreviousHashState = currentHashState;
        // Clear the current button map
        usedComponents.clear();
        String defaultAction = null;
        ActionComponent actionComponent = mCurrentUnit.get(ActionComponent.class);
        for (int index = 0; index < entityActionsList.size(); index++) {
            String action = entityActionsList.get(index);
            defaultAction = index == 0 ? action : defaultAction;
            OutlineButton button = mMainContent.createButton(index);
            SwingUiUtils.removeAllActionListeners(button);
            usedComponents.add(button);
            button.setText(action.replace('_', ' '));
            button.addActionListener(e -> {
                actionComponent.stageAction(action);
                mSelectedAction = action;
                mIsDirty = true;
                mLogger.info("Pressed " + action + " from " + unitEntity);
            });
        }

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            if (!usedComponents.contains(component)) {
                component.setVisible(false);
                return;
            } else {
                component.setVisible(true);
            }
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            SwingUiUtils.setHoverEffect(component, false);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
        mIsDirty = true;
        mSelectedAction = defaultAction;
        mLogger.info("Updating Actions Panel");
    }

    public void gameUpdate(GameModel model) {
        if (!model.shouldShowGameplayUI()) { return; }
        super.gameUpdate(model);
//        Entity tileEntity = model.getGameState().getCurrentlySelectedTileEntity();
        Entity tileEntity = model.getSelectedTiles().stream().findFirst().orElse(null);
        if (tileEntity != null) {
            Tile tile = tileEntity.get(Tile.class);
            Entity unit = tile.getUnit();
//            Entity unit = model.getGameState().getLastNonNullSelectedUnitEntity();
            gameUpdate(model, unit);
        }
        model.getGameState().setActionPanelIsOpen(isVisible());
    }

    public boolean isDirty() { return mIsDirty; }
    public String getSelectedAction() { return mSelectedAction; }
    public void clean() { mIsDirty = false; }
}
