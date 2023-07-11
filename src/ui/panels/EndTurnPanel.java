package ui.panels;

import constants.GameStateKey;
import game.components.ActionManager;
import game.components.MovementManager;
import game.components.Tile;
import game.entity.Entity;
import game.main.GameModel;
import graphics.JScene;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

public class EndTurnPanel extends ControlPanelInnerTemplate {


    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox acted = new JCheckBox("has Acted for the turn.");
    public final JButton endTurnButton = new JButton("End the turn.");
    public final JPanel container = new JPanel();
    private boolean initialized = false;

    public EndTurnPanel(int width, int height) {
        super(width, (int) (height * .9), EndTurnPanel.class.getSimpleName());

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
    }

    public void set(GameModel model, Entity tileEntity) {
        if (tileEntity == null) { return; }

        if (!initialized) {
            endTurnButton.addActionListener(e -> {
                model.state.set(GameStateKey.ACTIONS_END_TURN, true);
                System.out.println("Ending turn");
            });
            initialized = true;
        }


        Entity unit = tileEntity.get(Tile.class).unit;
        if (unit == null) { return; }
        topLeft.set(unit);
        ActionManager action = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);
        acted.setSelected(action.acted);
        moved.setSelected(movement.moved);
    //    update = false;
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
}
