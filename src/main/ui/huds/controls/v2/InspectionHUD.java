package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.Summary;
import main.game.components.Tile;
import main.game.main.GameModel;
import main.game.stats.node.StatsNode;
import main.graphics.temporary.JKeyLabelOld;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JKeyLabelArray;
import main.ui.huds.controls.HUD;
import main.ui.panels.ImagePanel;
import main.utils.StringFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InspectionHUD extends HUD {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JTextArea description;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabelOld> labelMap = new HashMap<>();
    private final JKeyLabelArray statPane;

    private JPanel tagPanel;
    private JPanel modificationPanel;
    private static final String IS_WALL = "Wall";
    private static final String HAS_STRUCTURE = "Structure";
    private static final String DEBUG_SHADOWS = "DebugShadows";
    private static final String DEBUG_TERRAIN = "DebugTerrain";
    private static final String DEBUG_LIQUID = "DebugLiquid";
    private static final String DEBUG_STRUCTURE = "DebugStructure";

    public InspectionHUD(int width, int height) {
        super(width, height, "Inspection");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
//        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        // Image
        selection = new ImagePanel((int) (height * .2), (int) (height * .2));
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(selection.getPreferredSize());
        panel.add(selection);
        add(panel, constraints);

        constraints.gridy = 1;
        statPane =  new JKeyLabelArray(
                width,
                (int) (height * .65),
                new String[]{
                        Constants.MOVE,
                        Constants.CLIMB,
                        Constants.SPEED,
                        Constants.ELEVATION,
                        Constants.TILE,
                        DEBUG_SHADOWS,
                        DEBUG_TERRAIN,
                        DEBUG_LIQUID,
                        DEBUG_STRUCTURE
                }
        );
        add(statPane, constraints);


        constraints.gridy = 2;
        description = new JTextArea();
        description.setPreferredSize(new Dimension(width, (int) (height * .1)));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(description, constraints);

//        constraints.gridy = 3;
//        JScrollPane pane = createButtonPane(width, (int) (height * .3));
//        add(pane, constraints);
//        setBackground(ColorPalette.BLACK);
//
        constraints.gridy = 3;
        getExitButton().setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(getExitButton(), constraints);
    }

    protected JScrollPane createButtonPane(int width, int height) {
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 2));
        actionPanel.setBackground(ColorPalette.getRandomColor());


        int buttons = 12;
        for (int i = 0; i < buttons; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFocusPainted(false);
            actionPanel.add(button);
        }

        return createScalingPane(width, height, actionPanel);
    }
    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (currentTile != null && currentTile != previousTile) {
            statPane.get(Constants.MOVE).setLabel("");
            statPane.get(Constants.SPEED).setLabel("");
            statPane.get(Constants.CLIMB).setLabel("");
            if (currentUnit == null) { selection.set(currentTile); }
            Tile tile = currentTile.get(Tile.class);
            statPane.get(DEBUG_SHADOWS).setLabel(tile.shadows.size() + "");
            statPane.get(DEBUG_TERRAIN).setLabel(tile.getTerrain() + "");
            statPane.get(DEBUG_LIQUID).setLabel(tile.getLiquid() + "");
            statPane.get(DEBUG_STRUCTURE).setLabel(tile.getStructure() + "");
            statPane.get(Constants.ELEVATION).setLabel(tile.getHeight() + "");
            statPane.get(Constants.TILE).setLabel(StringFormatter.format("Row: {}, Column: {}", tile.row, tile.column));
        }
        if (currentUnit != null) {
            Summary summary = currentUnit.get(Summary.class);
            selection.set(currentUnit);
            statPane.get(Constants.MOVE).setLabel(summary.getStatTotal(Constants.MOVE) + "");
            statPane.get(Constants.CLIMB).setLabel(summary.getStatTotal(Constants.CLIMB) + "");
            statPane.get(Constants.SPEED).setLabel(summary.getStatTotal(Constants.SPEED) + "");
        }
    }
}