package main.ui.huds;


import main.constants.GameState;
import main.constants.Settings;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;
import main.ui.custom.DatasheetPanel;
import main.ui.custom.SwingUiUtils;
import main.utils.StringFormatter;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class SettingsPanel extends JScene {

    protected final DatasheetPanel mDatasheetPanel;
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;

    private final String DEBUG_MODE_CHECK_BOX = "DeBuG MoDE";
    private JCheckBox mDebugMode = null;

    public SettingsPanel(int width, int height, int x, int y) {
        super(width, height, x, y, SettingsPanel.class.getSimpleName());

        Color color = ColorPalette.getRandomColor();
        setLayout(new GridBagLayout());

        int spreadSheetWidth = width;
        int spreadSheetHeight = (int) (height * .7);
        mDatasheetPanel = new DatasheetPanel(spreadSheetWidth, spreadSheetHeight, 7);
        mDatasheetPanel.setBackground(color);

        mDebugMode = new JCheckBox();
        mDebugMode.setBorderPaintedFlat(true);
        mDatasheetPanel.addRow(DEBUG_MODE_CHECK_BOX, mDebugMode);

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
        SwingUiUtils.automaticallyStyleButton(exitButton);
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



//            mDatasheetPanel.addRow("Name", identity.getName());
//            mDatasheetPanel.addRow("Type", statistics.getType().toString());
//            mDatasheetPanel.addRow(Statistics.HEALTH,
//                    statistics.getStatCurrent(Statistics.HEALTH) + "/" + statistics.getStatTotal(Statistics.HEALTH));
//            mDatasheetPanel.addRow(Statistics.MANA,
//                    statistics.getStatCurrent(Statistics.MANA) + "/" + statistics.getStatTotal(Statistics.MANA));
//            mDatasheetPanel.addRow(Statistics.LEVEL, String.valueOf(statistics.getStatBase(Statistics.LEVEL)));
//            mDatasheetPanel.addRow(Statistics.EXPERIENCE,
//                    statistics.getStatModified(Statistics.LEVEL) + "/"
//                            + Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));

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
        currentSelected = (Entity) model.mGameState.getObject(GameState.CURRENTLY_SELECTED);
//        if (currentSelected != null) {
//            Tile tile = currentSelected.get(Tile.class);
//            Entity unit = tile.getUnit();
//            set(unit);
//        }
        model.getSettings().put(Settings.GAMEPLAY_DEBUG_MODE, mDebugMode.isSelected());
    }
}
