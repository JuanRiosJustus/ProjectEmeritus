package main.ui.custom;


import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.ui.huds.controls.HUD;
import main.utils.StringFormatter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SummaryPanel extends HUD {

    protected final DatasheetPanel mDatasheetPanel;
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;

    public SummaryPanel(int width, int height, int x, int y) {
        super(width, height, x, y, ImagePanel.class.getSimpleName());

        Color color = ColorPalette.getRandomColor();
        setLayout(new GridBagLayout());

        int spreadSheetWidth = width;
        int spreadSheetHeight = (int) (height * .7);
        mDatasheetPanel = new DatasheetPanel(spreadSheetWidth, spreadSheetHeight, 7);
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
        SwingUiUtils.automaticallyStyleButton(exitButton, ColorPalette.WHITE);
        add(exitButton, gbc);
        setBackground(color);
    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            Assets assets = entity.get(Assets.class);
            if (tile.getLiquid() != null) {
                animation = assets.getAnimation(Assets.LIQUID_ASSET);
            } else if (tile.getTerrain() != null) {
                animation = assets.getAnimation(Assets.TERRAIN_ASSET);
            }
//            if (tile.getLiquid() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
//            } else if (tile.getTerrain() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
//            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
            Statistics statistics = entity.get(Statistics.class);
            Identity identity = entity.get(Identity.class);

            mDatasheetPanel.addRowOutlineLabel("Name", identity.getName());
            mDatasheetPanel.addRowOutlineLabel("Type", statistics.getType().toString());
            mDatasheetPanel.addRowOutlineLabel(Statistics.HEALTH,
                    statistics.getStatCurrent(Statistics.HEALTH) + "/" + statistics.getStatTotal(Statistics.HEALTH));
            mDatasheetPanel.addRowOutlineLabel(Statistics.MANA,
                    statistics.getStatCurrent(Statistics.MANA) + "/" + statistics.getStatTotal(Statistics.MANA));
            mDatasheetPanel.addRowOutlineLabel(Statistics.LEVEL, String.valueOf(statistics.getStatBase(Statistics.LEVEL)));
            mDatasheetPanel.addRowOutlineLabel(Statistics.EXPERIENCE,
                    statistics.getStatModified(Statistics.LEVEL) + "/"
                            + Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));

//            String[] stats = new String[]{ Statistics.PHYSICAL_ATTACK, Statistics.PHYSICAL_DEFENSE, Statistics.MAGICAL_ATTACK, Statistics.MAGICAL_DEFENSE };
//            History history = entity.get(History.class);
        }

        if (animation != null && mCurrentImage != animation.getFrame(0)) {
            mCurrentImage = animation.getFrame(0);
//            mUnitTargetFrame.setImage(animation, reference);
        }

        observing = entity;
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void jSceneUpdate(GameModel model) {
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            set(unit);
        }
    }
}
