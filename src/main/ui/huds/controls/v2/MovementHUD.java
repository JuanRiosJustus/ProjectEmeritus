package main.ui.huds.controls.v2;

import main.constants.Constants;
import main.constants.GameState;
import main.game.components.Summary;
import main.game.components.tile.Tile;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JKeyValueMap;
import main.ui.huds.controls.HUD;
import main.ui.custom.ImagePanel;
import main.utils.StringFormatter;

import javax.swing.*;
import java.awt.*;

public class MovementHUD extends HUD {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JTextArea description;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final JKeyValueMap mStatsKeyValueMap;
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
        mImagePanel = new ImagePanel(width, (int) (height * .25));
        add(mImagePanel, constraints);

        constraints.gridy = 1;
        mStatsKeyValueMap =  new JKeyValueMap(
                width,
                (int) (height * .7),
                new Object[][]{
                        new Object[] { Constants.MOVE, new JLabel() },
                        new Object[] { Constants.CLIMB, new JLabel() },
                        new Object[] { Constants.SPEED, new JLabel() },
                        new Object[] { Constants.ELEVATION, new JLabel() },
                        new Object[] { Constants.TILE, new JLabel() },
                }
        );
        add(mStatsKeyValueMap, constraints);

        constraints.gridy = 2;
        undoButton.setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(undoButton, constraints);
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (mCurrentTile == null) { return; }

        if (undoButton.getActionListeners().length == 0) {
            undoButton.addActionListener(e -> {
//                Entity current =
                gameModel.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, true);
            });
        }

        if (mCurrentUnit == null) { return; }
        Summary summary = mCurrentUnit.get(Summary.class);
        mImagePanel.set(mCurrentUnit);

        JLabel label = (JLabel) mStatsKeyValueMap.get(Constants.MOVE).getValueComponent();
        label.setText(summary.getStatCurrent(Constants.MOVE) + "");

        label = (JLabel) mStatsKeyValueMap.get(Constants.SPEED).getValueComponent();
        label.setText(summary.getStatCurrent(Constants.SPEED) + "");

        label = (JLabel) mStatsKeyValueMap.get(Constants.CLIMB).getValueComponent();
        label.setText(summary.getStatCurrent(Constants.CLIMB) + "");

        Tile tile = mCurrentTile.get(Tile.class);

        label = (JLabel) mStatsKeyValueMap.get(Constants.ELEVATION).getValueComponent();
        label.setText(tile.getHeight() + "");

        label = (JLabel) mStatsKeyValueMap.get(Constants.TILE).getValueComponent();
        label.setText(StringFormatter.format("Row: {}, Column: {}", tile.row, tile.column));
    }
}