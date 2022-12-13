package ui.panels;

import constants.Constants;
import game.components.MoveSet;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import graphics.JScene;
import utils.ComponentUtils;
import graphics.temporary.JFieldLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;


public class MovementPanel extends JScene {

    private final JFieldLabel speedFL;
    private final JFieldLabel distanceFL;
    private final JFieldLabel abilityRangeFL;
    private final JLabel toolTipLabel;
    private final JPanel abilityPanel;
    private final StringBuilder monitor = new StringBuilder();
    private final List<Ability> abilitiesToObserve = new ArrayList<>();
    private Entity unitToObserve;

    public MovementPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT, "Movement");

        setLayout(new GridBagLayout());

        speedFL = ComponentUtils.createFieldLabel("Speed", "0");
        distanceFL = ComponentUtils.createFieldLabel("Distance", "0");
        abilityRangeFL = ComponentUtils.createFieldLabel("Ability Range", "Longest Range");
        toolTipLabel = new JLabel("Select a block to move to.");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = ComponentUtils.verticalGBC();
        abilityPanel = new JPanel();
        abilityPanel.setLayout(new GridBagLayout());

//        add(abilityPanel, gbc);

        add(toolTipLabel, gbc);
        add(speedFL, gbc);
        add(distanceFL, gbc);
        add(abilityRangeFL, gbc);
        add(getExitButton(), gbc);
    }

    public void set(Entity unit) {
        if (unit == null) { return; }

        // show distance and speed of selected unit
        Statistics stats = unit.get(Statistics.class);
        speedFL.setLabel(String.valueOf(stats.getScalarNode(Constants.SPEED).getTotal()));
        distanceFL.setLabel(String.valueOf(stats.getScalarNode(Constants.DISTANCE).getTotal()));
//        abilityRangeFL.setLabel(monitor.toString());

        // set the list of abilities to reflect the unit if not already
        if (unitToObserve == unit) { return; }
        unitToObserve = unit;
        abilitiesToObserve.clear();
        abilitiesToObserve.addAll(unitToObserve.get(MoveSet.class).getCopy());

        // update the ui manually
        revalidate();
        repaint();
    }
}
