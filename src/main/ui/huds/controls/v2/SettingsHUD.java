package main.ui.huds.controls.v2;

import main.constants.GameState;
import main.game.components.ActionManager;
import main.game.components.MovementManager;
import main.game.main.GameModel;
import main.ui.huds.controls.HUD;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;

public class SettingsHUD extends HUD {

    private boolean initialized = false;
    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox acted = new JCheckBox("has Acted for the turn.");
    public final JButton endTurnButton = new JButton("End the turn.");

    public SettingsHUD(int width, int height) {
        super(width, height, 0, 0, SettingsHUD.class.getSimpleName());

//        JScrollPane topRightScroller = createTopRightPanel();
//        add(topRightScroller);
//
//        JScrollPane middleScroller = createMiddlePanel();
//        add(middleScroller);
    }


    protected JScrollPane createTopRightPanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(
                (int)reference.getPreferredSize().getWidth(),
                (int)(reference.getPreferredSize().getHeight() * 1)
        ));

        int rows = 0;
        int columns = 1;
        result.setLayout(new GridLayout(rows, columns));

        result.add(moved);
        moved.setEnabled(false);

        result.add(acted);
        acted.setEnabled(false);

        result.add(endTurnButton);

        JScrollPane scrollPane = new JScrollPane(
                result,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    protected JScrollPane createMiddlePanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(
                (int)reference.getPreferredSize().getWidth(),
                (int)(reference.getPreferredSize().getHeight() * 1)
        ));

        int rows = 0;
        int columns = 1;
        result.setLayout(new GridLayout(rows, columns));


//        result.add(saveButton);
//        result.add(settingsButton);
        result.add(mExitButton);
        JScrollPane scrollPane = new JScrollPane(
                result,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (!initialized) {
            endTurnButton.addActionListener(e -> {
                gameModel.gameState.set(GameState.ACTIONS_END_TURN, true);
            });
//            saveButton.addActionListener(e -> {
//                UserSavedData.getInstance().save();
//            });
            initialized = true;
        }

        if (mCurrentUnit == null) { return; }
//        topLeft.set(currentUnit);
        ActionManager actionManager = mCurrentUnit.get(ActionManager.class);
        MovementManager movementManager = mCurrentUnit.get(MovementManager.class);
        acted.setSelected(actionManager.mActed);
        moved.setSelected(movementManager.moved);
    }
}
