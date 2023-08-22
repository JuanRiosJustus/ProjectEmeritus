package main.ui.huds.controls.v1;

import main.constants.GameState;
import main.game.components.ActionManager;
import main.game.components.MovementManager;
import main.game.main.GameModel;
import main.game.state.UserSavedData;
import main.ui.panels.ControlPanelPane;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridLayout;

public class MiniOtherHUD extends ControlPanelPane {

    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox acted = new JCheckBox("has Acted for the turn.");
    public final JButton endTurnButton = new JButton("End the turn.");

    public final JButton saveButton = new JButton("Save");
    public final JButton settingsButton = new JButton("Settings");
    public final JButton exitButton = new JButton("Exit");

    public final JPanel container = new JPanel();
    private GameModel gameModel = null;
    private boolean initialized = false;

    public MiniOtherHUD(int width, int height) {
        super(width, height, "Mini Other");

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middle);
        middle.add(middleScroller);
    }

    @Override
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

    @Override
    protected JScrollPane createMiddlePanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(
            (int)reference.getPreferredSize().getWidth(), 
            (int)(reference.getPreferredSize().getHeight() * 1)
        ));

        int rows = 0;
        int columns = 1;
        result.setLayout(new GridLayout(rows, columns));

        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'createMiddlePanel'");
        result.add(saveButton);
        result.add(settingsButton);
        result.add(exitButton);
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
            saveButton.addActionListener(e -> {
                UserSavedData.getInstance().save();
            });
            initialized = true;
        }

        if (currentUnit == null) { return; }
        topLeft.set(currentUnit);
        ActionManager action = currentUnit.get(ActionManager.class);
        MovementManager movement = currentUnit.get(MovementManager.class);
        acted.setSelected(action.acted);
        moved.setSelected(movement.moved);
    }
}
