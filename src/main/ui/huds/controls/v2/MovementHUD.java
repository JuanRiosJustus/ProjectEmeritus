package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.constants.GameState;
import main.game.components.Summary;
import main.game.components.Tile;
import main.game.main.GameModel;
import main.game.stats.node.StatsNode;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JKeyValueArray;
import main.ui.huds.controls.HUD;
import main.ui.custom.ImagePanel;
import main.utils.StringFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MovementHUD extends HUD {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JTextArea description;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final JKeyValueArray statPane;
    private final JButton undoButton = new JButton("Undo Move");

    private JPanel tagPanel;
    private JPanel modificationPanel;

    public MovementHUD(int width, int height) {
        super(width, height, "Movement");

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
        statPane =  new JKeyValueArray(
                width,
                (int) (height * .5),
                new String[]{
                        Constants.MOVE,
                        Constants.CLIMB,
                        Constants.SPEED,
                        Constants.ELEVATION,
                        Constants.TILE
                }
        );
        add(statPane, constraints);


        constraints.gridy = 2;
        description = new JTextArea();
        description.setPreferredSize(new Dimension(width, (int) (height * .2)));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(description, constraints);



        constraints.gridy = 3;
        undoButton.setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(undoButton, constraints);
//        constraints.gridy = 3;
//        JScrollPane pane = createButtonPane(width, (int) (height * .3));
//        add(pane, constraints);
//        setBackground(ColorPalette.BLACK);
//
        constraints.gridy = 4;
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
        if (currentTile == null) { return; }

        if (undoButton.getActionListeners().length == 0) {
            undoButton.addActionListener(e -> {
//                Entity current =
                gameModel.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, true);
            });
        }

        if (currentUnit == null) { return; }
        Summary summary = currentUnit.get(Summary.class);
        selection.set(currentUnit);
        StatsNode node = summary.getStatsNode(Constants.MOVE);
        statPane.get(node.getName()).setValue(node.getTotal() + "");
        node = summary.getStatsNode(Constants.SPEED);
        statPane.get(node.getName()).setValue(node.getTotal() + "");
        node = summary.getStatsNode(Constants.CLIMB);
        statPane.get(node.getName()).setValue(node.getTotal() + "");

        Tile tile = currentTile.get(Tile.class);
        statPane.get(Constants.ELEVATION).setValue(tile.getHeight() + "");
        statPane.get(Constants.TILE).setValue(StringFormatter.format("Row: {}, Column: {}", tile.row, tile.column));

    }
}