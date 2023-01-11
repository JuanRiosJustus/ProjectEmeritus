package ui.panels;

import constants.Constants;
import game.GameController;
import game.components.ActionManager;
import game.components.MovementManager;
import game.entity.Entity;
import graphics.JScene;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class TurnStatusPanel extends JScene {


    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox attacked = new JCheckBox("has Attacked for the turn.");
    public final JButton endTurnToggleButton = new JButton("End the turn.");
//    public final JButton endTurnButton = new JButton("End the turn.");
    public final JPanel container = new JPanel();

    public TurnStatusPanel(int width, int height, String name) {
        super(width, height, name);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        container.setLayout(new GridBagLayout());
        container.add(new JLabel("Right click to end the turn."), gbc);
        container.add(moved, gbc);
        container.add(attacked, gbc);
        moved.setEnabled(false);
        attacked.setEnabled(false);

        setLayout(new GridBagLayout());
        add(container, gbc);
        add(endTurnToggleButton, gbc);
        add(getExitButton(), gbc);
    }

    public void set(Entity unit) {
//        ActionManager manager = unit.get(ActionManager.class);
//        MovementManager movement = unit.get(MovementManager.class);
//        attacked.setSelected(manager.attacked);
//        moved.setSelected(movement.moved);
//        update = false;
    }
}
