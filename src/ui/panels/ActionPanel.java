package ui.panels;

import constants.Constants;
import game.GameController;
import game.components.ActionManager;
import game.entity.Entity;
import graphics.JScene;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ActionPanel extends JScene {


    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox attacked = new JCheckBox("has Attacked for the turn.");
    public final JButton endTurnToggleButton = new JButton("End the turn.");
//    public final JButton endTurnButton = new JButton("End the turn.");
    public final JPanel container = new JPanel();

    public ActionPanel(int width, int height) {
        super(width, height, "Actions");

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

        endTurnToggleButton.addActionListener(e ->
                GameController.instance().model.ui.set(Constants.ACTIONS_UI_ENDTURN, true));
    }

    public void set(Entity unit) {
        ActionManager manager = unit.get(ActionManager.class);
        attacked.setSelected(manager.attacked);
        moved.setSelected(manager.moved);
        update = false;
    }
}
