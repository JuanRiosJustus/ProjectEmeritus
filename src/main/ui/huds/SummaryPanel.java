package main.ui.huds;


import main.game.main.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.ControllerUI;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.OutlineMapPanel;

import java.awt.image.BufferedImage;

import javax.swing.*;

public class SummaryPanel extends ControllerUI {

    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected OutlineMapPanel mMainContent;
    protected Entity observing = null;
    protected JPanel container;

    public SummaryPanel(int width, int height, int x, int y, JButton enter, JButton exit) {
        super(width, height, x, y, enter, exit);

        mMainContent = new OutlineMapPanel(mMainContentWidth, mMainContentHeight, 5);

        add(mMainContent);
        add(getExitButton());
    }

    public void gameUpdate(GameModel model, Entity entity) {
        if (entity == null) { return; }

        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);


        var row = mMainContent.putKeyValue("Name");
        row.third.setText(identityComponent.getName());

        row = mMainContent.putKeyValue("Type");
        row.third.setText(statisticsComponent.getType().toString());

        row = mMainContent.putKeyValue(StatisticsComponent.HEALTH);
        row.third.setText(statisticsComponent.getCurrent(StatisticsComponent.HEALTH) + "/" +
                statisticsComponent.getTotal(StatisticsComponent.HEALTH));

        row = mMainContent.putKeyValue(StatisticsComponent.MANA);
        row.third.setText(statisticsComponent.getCurrent(StatisticsComponent.MANA) + "/" +
                statisticsComponent.getTotal(StatisticsComponent.MANA));

        row = mMainContent.putKeyValue(StatisticsComponent.LEVEL);
        row.third.setText(String.valueOf(statisticsComponent.getBase(StatisticsComponent.LEVEL)));

        row = mMainContent.putKeyValue(StatisticsComponent.EXPERIENCE);
        row.third.setText(statisticsComponent.getModified(StatisticsComponent.LEVEL) + "/" +
                StatisticsComponent.getExperienceNeeded(statisticsComponent.getBase(StatisticsComponent.LEVEL)));

        // Setup ui coloring
        mMainContent.getContents().forEach(component -> {
            SwingUiUtils.setBackgroundFor(getBackground(), component);
            component.setFont(FontPool.getInstance().getFontForHeight(mMainContent.getComponentHeight()));
        });
        observing = entity;
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void gameUpdate(GameModel model) {
        super.gameUpdate(model);
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
//        currentSelected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED_TILES);
//        currentSelected = model.getSelectedTile();
//        if (currentSelected != null) {
//            Tile tile = currentSelected.get(Tile.class);
//            Entity unit = tile.getUnit();
//            gameUpdate(model, unit);
//        }
    }
}
