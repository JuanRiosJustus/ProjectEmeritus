package ui.panels;

import constants.Constants;
import game.components.Inventory;
import game.components.MoveSet;
import game.components.Statistics;
import game.entity.Entity;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;

public class ItemsPanel extends JScene {

    private JComboBox<String> inventory = new JComboBox<>();
    private Entity observing = null;

    public ItemsPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT, "Items");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = ComponentUtils.horizontalGBC();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(new JButton("This is a test button"), gbc);
        panel.add(inventory, gbc);
//

//        inventory.addActionListener(e -> {
//            System.err.println(e.getActionCommand() + " ?");
//        });
//        add(new JButton("This is a test button"));
//        add(inventory, gbc);

        gbc = ComponentUtils.verticalGBC();
        add(panel, gbc);
        add(getExitButton(), gbc);
    }

    public void set(Entity unit) {
        if (unit == null || (observing == unit)) { return; }
        observing = unit;

        Inventory unitInventory = unit.get(Inventory.class);
        inventory.removeAllItems();
        for (String itemName : unitInventory.itemNames()) {
            inventory.addItem(itemName);
        }

        // update the ui manually
        revalidate();
        repaint();
    }
}
