package main.ui.panels;

import main.constants.GameStateKey;
import main.game.components.ActionManager;
import main.game.components.MovementManager;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.JScene;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class EndTurnPanel extends ControlPanelInnerTemplate {

    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox acted = new JCheckBox("has Acted for the turn.");
    public final JButton endTurnButton = new JButton("End the turn.");
    public final JPanel container = new JPanel();
    private GameModel gameModel = null;
    private boolean initialized = false;

    public EndTurnPanel(int width, int height) {
        super(width, height, EndTurnPanel.class.getSimpleName());

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
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
        result.add(new JButton("Items"));
        result.add(new JButton("Settings"));
        result.add(new JButton("Exit"));
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

    public void set(GameModel model, Entity entity) {
        if (entity != null) {
            Tile tile = entity.get(Tile.class);
            observing = tile.unit;
        }
        gameModel = model;
        update();
    }

    public void update() {
        if (initialized == false) {
            endTurnButton.addActionListener(e -> {
                gameModel.state.set(GameStateKey.ACTIONS_END_TURN, true);
            });
            initialized = true;
        }

        if (observing == null) { return; }
        topLeft.set(observing);
        ActionManager action = observing.get(ActionManager.class);
        MovementManager movement = observing.get(MovementManager.class);
        acted.setSelected(action.acted);
        moved.setSelected(movement.moved);
    }

    @Override
    public void update(GameModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
