package main.ui.custom;


import main.constants.Constants;
import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.asset.AssetPool;
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
    private TargetFrameTag mUnitTargetFrame = null;
    private final Map<String, JButton> mRows = new HashMap<>();
    private static final String ELEVATION = "Elevation";
    private static final String TILE = "Tile";

    public SummaryPanel(int width, int height) {
        super(width, height, ImagePanel.class.getSimpleName());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        container = new JPanel();
        container.setBorder(new EmptyBorder(0, 5, 0, 5));
        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new GridBagLayout());

        int targetFrameWidth = width;
        int targetFrameHeight = (int) (height * .3);
        mUnitTargetFrame = new TargetFrameTag(targetFrameWidth, targetFrameHeight);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        container.add(mUnitTargetFrame, gbc);

        int spreadSheetWidth = (int) (width * .9);
        int spreadSheetHeight = height - targetFrameHeight;
        mDatasheetPanel = new DatasheetPanel(spreadSheetWidth, spreadSheetHeight);

        mDatasheetPanel.setPreferredSize(new Dimension(spreadSheetWidth, spreadSheetHeight));
        mDatasheetPanel.setMinimumSize(new Dimension(spreadSheetWidth, spreadSheetHeight));
        mDatasheetPanel.setMaximumSize(new Dimension(spreadSheetWidth, spreadSheetHeight));

        mDatasheetPanel.setBackground(Color.BLUE);
        gbc.gridy = 1;

        container.add(mDatasheetPanel, gbc);
        container.setOpaque(true);
        container.setBackground(ColorPalette.TRANSPARENT);

        add(createScalingPane(width, height, container));
    }

    private void addOrUpdateRow(String text, String value) {

        JButton component = mRows.getOrDefault(text, new JButton());
        SwingUiUtils.stylizeButtons(component, Color.WHITE);
        component.setHorizontalAlignment(SwingConstants.RIGHT);
//        component.setVerticalAlignment(SwingConstants.TOP);
        component.setFocusPainted(false);
        component.setBorderPainted(false);
        component.setText(value);

        mRows.put(text, component);
        mDatasheetPanel.addRow(text, value);
    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        int setupType = 0;
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            if (tile.getLiquid() != null) {
                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
            } else if (tile.getTerrain() != null) {
                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
            }
//            if (tile.getLiquid() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
//            } else if (tile.getTerrain() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
//            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
            MovementManager movementManager = entity.get(MovementManager.class);
            Tile tile = movementManager.currentTile.get(Tile.class);
            Statistics statistics = entity.get(Statistics.class);
            Identity identity = entity.get(Identity.class);
//            setupType = 3;

            addOrUpdateRow("Name", identity.getName());
            addOrUpdateRow("Type", statistics.getType().toString());
            addOrUpdateRow(Statistics.HEALTH,
                    statistics.getStatCurrent(Statistics.HEALTH) + "/" + statistics.getStatTotal(Statistics.HEALTH));
            addOrUpdateRow(Statistics.MANA,
                    statistics.getStatCurrent(Statistics.MANA) + "/" + statistics.getStatTotal(Statistics.MANA));
            addOrUpdateRow(Statistics.LEVEL, String.valueOf(statistics.getStatBase(Statistics.LEVEL)));
            addOrUpdateRow(Statistics.EXPERIENCE,
                    statistics.getStatModified(Statistics.LEVEL) + "/"
                            + Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));

            String[] stats = new String[]{ Statistics.PHYSICAL_ATTACK, Statistics.PHYSICAL_DEFENSE, Statistics.MAGICAL_ATTACK, Statistics.MAGICAL_DEFENSE };

//            History history = entity.get(History.class);
        }

        if (animation != null && mCurrentImage != animation.getFrame(0)) {
            mCurrentImage = animation.getFrame(0);
            mUnitTargetFrame.setImage(animation, reference);
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
