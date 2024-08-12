package main.ui.huds;

import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;
import main.ui.custom.DatasheetPanel;

import java.awt.*;

import javax.swing.*;

public class MovementPanel extends JScene {

    protected final DatasheetPanel mDatasheetPanel;
    private int mHistoryState = 0;
    protected Entity observing = null;
    protected JPanel container;
    private static final String ELEVATION = "Elevation";
    private static final String TILE = "Tile";
    private static final String DIRECTION = "Direction";

    public MovementPanel(int width, int height, int x, int y) {
        super(width, height, x, y,  MovementPanel.class.getSimpleName());

        setLayout(new GridBagLayout());
        setOpaque(true);
        Color color = ColorPalette.getRandomColor();

        int spreadSheetWidth = width;
        int spreadSheetHeight = (int) (height * .7);
        mDatasheetPanel = new DatasheetPanel(spreadSheetWidth, spreadSheetHeight);
//        mDatasheetPanel = new DatasheetV2();

//        mDatasheetPanel.setPreferredSize(new Dimension(spreadSheetWidth, spreadSheetHeight));
//        mDatasheetPanel.setMinimumSize(new Dimension(spreadSheetWidth, spreadSheetHeight));
//        mDatasheetPanel.setMaximumSize(new Dimension(spreadSheetWidth, spreadSheetHeight));
        mDatasheetPanel.setBackground(color);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(mDatasheetPanel, gbc);

        gbc.gridy = 1;
        JButton exitButton = getExitButton();

        add(exitButton, gbc);
        setBackground(color);
    }

    public void set(GameModel model, Entity entity) {
        if (entity == null) { return; }

        MovementManager movementManager = entity.get(MovementManager.class);
        Tile tile = movementManager.currentTile.get(Tile.class);
        Statistics statistics = entity.get(Statistics.class);
        DirectionalFace directionalFace = entity.get(DirectionalFace.class);

        mDatasheetPanel.addRow(TILE, String.valueOf(tile.toString()));
        mDatasheetPanel.addRow(Statistics.MOVE, String.valueOf(statistics.getStatTotal(Statistics.MOVE)));
        mDatasheetPanel.addRow(Statistics.SPEED, String.valueOf(statistics.getStatTotal(Statistics.SPEED)));
        mDatasheetPanel.addRow(Statistics.CLIMB, String.valueOf(statistics.getStatTotal(Statistics.CLIMB)));
        mDatasheetPanel.addRow(ELEVATION, String.valueOf(tile.getHeight()));
        mDatasheetPanel.addRow(DIRECTION, directionalFace.getFacingDirection().toString());

        observing = entity;

        if (model != null) {
            model.setGameState(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, isShowing());
        }
    }


    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void jSceneUpdate(GameModel model) {
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED);
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            set(model, unit);
        }
    }
}
