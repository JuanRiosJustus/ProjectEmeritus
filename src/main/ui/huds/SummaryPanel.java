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
import java.awt.image.BufferedImage;

import javax.swing.*;

public class SummaryPanel extends JScene {

    protected final DatasheetPanel mDatasheetPanel;
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;

    public SummaryPanel(int width, int height, int x, int y) {
        super(width, height, x, y, SummaryPanel.class.getSimpleName());

        Color color = ColorPalette.getRandomColor();
        setLayout(new GridBagLayout());

        int spreadSheetWidth = width;
        int spreadSheetHeight = (int) (height * .7);
        mDatasheetPanel = new DatasheetPanel(spreadSheetWidth, spreadSheetHeight, 5);
        mDatasheetPanel.setBackground(color);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;

        add(mDatasheetPanel, gbc);

        gbc.gridy = 1;
        JButton exitButton = getExitButton();

        add(exitButton, gbc);
        setBackground(color);
    }

    public void set(Entity entity) {
        if (entity == null) { return; }

        Statistics statistics = entity.get(Statistics.class);
        Identity identity = entity.get(Identity.class);

        mDatasheetPanel.addRow("Name", identity.getName());
        mDatasheetPanel.addRow("Type", statistics.getType().toString());
        mDatasheetPanel.addRow(Statistics.HEALTH,
                statistics.getStatCurrent(Statistics.HEALTH) + "/" + statistics.getStatTotal(Statistics.HEALTH));
        mDatasheetPanel.addRow(Statistics.MANA,
                statistics.getStatCurrent(Statistics.MANA) + "/" + statistics.getStatTotal(Statistics.MANA));
        mDatasheetPanel.addRow(Statistics.LEVEL, String.valueOf(statistics.getStatBase(Statistics.LEVEL)));
        mDatasheetPanel.addRow(Statistics.EXPERIENCE,
                statistics.getStatModified(Statistics.LEVEL) + "/"
                        + Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));
        observing = entity;
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
            set(unit);
        }
    }
}
