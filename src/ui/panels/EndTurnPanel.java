package ui.panels;

import constants.GameStateKey;
import game.GameModel;
import graphics.JScene;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class EndTurnPanel extends JScene {


    public final JCheckBox moved = new JCheckBox("has Moved for the turn.");
    public final JCheckBox attacked = new JCheckBox("has Attacked for the turn.");
    public final JButton endTurnButton = new JButton("End the turn.");
    public final JPanel container = new JPanel();
    private boolean initialized = false;

    public EndTurnPanel(int width, int height) {
        super(width, height, "End");

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
        add(endTurnButton, gbc);
        add(getExitButton(), gbc);
    }

    public void update(GameModel model) {
        if (!initialized) {
            endTurnButton.addActionListener(e -> {
                model.state.set(GameStateKey.ACTIONS_UI_ENDTURN, true);
                System.out.println("Ending turn");
            });
            initialized = true;
        }
//        ActionManager manager = unit.get(ActionManager.class);
//        MovementManager movement = unit.get(MovementManager.class);
//        attacked.setSelected(manager.attacked);
//        moved.setSelected(movement.moved);
//        update = false;
    }
}
