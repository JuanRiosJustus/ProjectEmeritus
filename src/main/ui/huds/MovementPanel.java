package main.ui.huds;

import main.constants.Tuple;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.ControllerUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.outline.OutlineLabel;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.OutlineMapPanel;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

public class MovementPanel extends ControllerUI {
    private final OutlineMapPanel mMainContent;
    private boolean mSetBackgroundColor = false;
    private int mHistoryState = 0;
    protected Entity observing = null;
    protected JPanel container;
    private static final String ELEVATION = "Elevation";
    private static final String TILE = "Tile";
    private static final String DIRECTION = "Direction";
    private Entity mCurrentUnit;
    private int mPreviousHashState;
    private ELogger mLogger = ELoggerFactory.getInstance().getELogger(MovementPanel.class);

    public MovementPanel(int width, int height, int x, int y, JButton enter, JButton exit) {
        super(width, height, x, y, enter, exit);

        mMainContent = new OutlineMapPanel(mMainContentWidth, mMainContentHeight, 4);

        add(mMainContent);
        add(getExitButton());
    }

    public void gameUpdate(GameModel model, Entity unitEntity) {
        if (unitEntity == null) { return; }
        if (unitEntity == mCurrentUnit) { return; }
        mCurrentUnit = unitEntity;

        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Tile tile = movementComponent.mCurrentTile.get(Tile.class);
        StatisticsComponent statisticsComponent = unitEntity.get(StatisticsComponent.class);
        int currentHashState = Objects.hash(tile.getRow(), tile.getColumn(), statisticsComponent);
        if (currentHashState == mPreviousHashState) { return; }
        mPreviousHashState = currentHashState;

        DirectionComponent directionComponent = unitEntity.get(DirectionComponent.class);

        Tuple<String, OutlineLabel, OutlineLabel> row = mMainContent.putKeyValue(TILE);
        OutlineMapPanel.updateKeyValueLabel(row, TILE, "X: " + (tile.getColumn() + 1) + ", Y: " + (tile.getRow() + 1));

        row = mMainContent.putKeyValue(StatisticsComponent.MOVE);
        OutlineMapPanel.updateKeyValueLabel(row, StatisticsComponent.MOVE,
                String.valueOf(statisticsComponent.getTotal(StatisticsComponent.MOVE)));

        row = mMainContent.putKeyValue(StatisticsComponent.SPEED);
        OutlineMapPanel.updateKeyValueLabel(row, StatisticsComponent.SPEED,
                String.valueOf(statisticsComponent.getTotal(StatisticsComponent.SPEED)));

        row = mMainContent.putKeyValue(StatisticsComponent.CLIMB);
        OutlineMapPanel.updateKeyValueLabel(row, StatisticsComponent.CLIMB,
                String.valueOf(statisticsComponent.getTotal(StatisticsComponent.CLIMB)));

        row = mMainContent.putKeyValue(ELEVATION);
        OutlineMapPanel.updateKeyValueLabel(row, ELEVATION, String.valueOf(tile.getHeight()));

        row = mMainContent.putKeyValue(DIRECTION);
        OutlineMapPanel.updateKeyValueLabel(row, DIRECTION, directionComponent.getFacingDirection().toString());

        observing = unitEntity;

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
        mLogger.info("Updated Movement Panel");

    }

    private Entity lastSelected;
    private Entity currentSelected;
    public void gameUpdate(GameModel model) {
        super.gameUpdate(model);
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
//        currentSelected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED_TILES);
        currentSelected = model.getSelectedTile();
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            gameUpdate(model, unit);
        }
        model.getGameState().setMovementPanelIsOpen(isVisible());
        mMainContent.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
    }

    @Override
    public void setBackground(Color color) {
        if (mMainContent == null) { return; }
        super.setBackground(color);
        mMainContent.setBackground(color);
    }

    @Override
    public void onOpenAction() {
        if (mModel == null) { return; }
        Entity entity = mModel.getSpeedQueue().peek();
        MovementComponent movementComponent = entity.get(MovementComponent.class);
        mModel.getGameState().setTileToGlideTo(movementComponent.getCurrentTile());
        mModel.setSelectedTile(movementComponent.getCurrentTile().get(Tile.class));
//        mModel.getGameState().setSelectedEntity(movementComponent.getCurrentTile());
    }
}
