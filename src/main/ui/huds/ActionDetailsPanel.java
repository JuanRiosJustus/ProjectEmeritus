package main.ui.huds;

import main.constants.Tuple;
import main.constants.csv.CsvRow;
import main.game.components.ActionComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.action.ActionPool;
import main.graphics.GameUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.outline.OutlineLabel;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.OutlineMapPanel;

import javax.swing.BoxLayout;
import java.util.List;

public class ActionDetailsPanel extends GameUI {
    private Entity mCurrentUnit;
    private String mCurrentAction;

    private final OutlineMapPanel mMainContent;
    private ELogger mLogger = ELoggerFactory.getInstance().getELogger(ActionDetailsPanel.class);
    public ActionDetailsPanel(int width, int height, int x, int y) {
        super(width, height);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        mMainContent = new OutlineMapPanel(width, height, 5);

        add(mMainContent);
    }

    @Override
    public void gameUpdate(GameModel model) {
        Entity currentSelected = model.getSelectedTiles().stream().findFirst().orElse(null);
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            update(model, unit);
        }

    }

    public void update(GameModel model, Entity entity) {
        if (entity == null) { return; }
        mCurrentUnit = entity;
        ActionComponent actionComponent = mCurrentUnit.get(ActionComponent.class);
        String selectedAction = actionComponent.getAction();
        if (selectedAction == null) { return; }
        mCurrentAction = selectedAction;

//        forceUpdate(mCurrentAction);
//        int currentHashState = Objects.hash(entityActionsList.toString());
//        if (currentHashState == mPeviousHashState) { return; }
//        mPeviousHashState = currentHashState;
        // TODO do something if there are more actions then free buttons


        List<String> columns = ActionPool.getInstance()
                .getColumns()
                .stream()
                .filter(e1 -> e1.length() < 10)
                .toList();
        CsvRow csvRow = ActionPool.getInstance().getAction(mCurrentAction);

        for (String column : columns) {
            Tuple<String, OutlineLabel, OutlineLabel> row = mMainContent.putKeyValue(column);
            OutlineMapPanel.updateKeyValueLabel(
                    row,
                    column.replace('_', ' '),
                    csvRow.get(column)
            );
        }

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
    }

    public void forceUpdate(String action) {
        if (action == null || mCurrentAction == null || mCurrentAction.equalsIgnoreCase(action)) { return; }
        mCurrentAction = action;
        List<String> columns = ActionPool.getInstance()
                .getColumns()
                .stream()
                .filter(e1 -> e1.length() < 10)
                .toList();
        CsvRow csvRow = ActionPool.getInstance().getAction(mCurrentAction);
        for (String column : columns) {
            Tuple<String, OutlineLabel, OutlineLabel> row = null;
            row = mMainContent.putKeyValue(column);
            OutlineMapPanel.updateKeyValueLabel(
                    row,
                    column.replace('_', ' '),
                    csvRow.get(column).replace('_', ' ')
            );
        }

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
    }
    public boolean hasContent() { return mMainContent.getRowCount() > 0; }
}
