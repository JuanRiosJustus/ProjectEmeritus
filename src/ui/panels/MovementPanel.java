package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import game.components.MoveSet;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import graphics.JScene;
import utils.ComponentUtils;
import graphics.temporary.JKeyValueLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


public class MovementPanel extends JScene {

    private final JKeyValueLabel speedFL;
    private final JKeyValueLabel distanceFL;
    private final JKeyValueLabel jumpFL;
//    private final JFieldLabel abilityRangeFL;
    private final JLabel toolTipLabel;
    private final JPanel abilityPanel;
    private final StringBuilder monitor = new StringBuilder();
    private final List<Ability> abilitiesToObserve = new ArrayList<>();
    private Entity unitToObserve;

    public MovementPanel(int width, int height, String name) {
        super(width, height, name);

        speedFL = ComponentUtils.createFieldLabel("Speed", "-");
        speedFL.key.setFont(speedFL.key.getFont().deriveFont(Font.BOLD));
        speedFL.setBorder(new EmptyBorder(10, 10, 10, 10));

        distanceFL = ComponentUtils.createFieldLabel("Distance", "-");
        distanceFL.key.setFont(distanceFL.key.getFont().deriveFont(Font.BOLD));
        distanceFL.setBorder(new EmptyBorder(10, 10, 10, 10));

        jumpFL = ComponentUtils.createFieldLabel("Jump", "-");
        jumpFL.key.setFont(jumpFL.key.getFont().deriveFont(Font.BOLD));
        jumpFL.setBorder(new EmptyBorder(10, 10, 10, 10));

//        abilityRangeFL = ComponentUtils.createFieldLabel("Ability Range", "Longest Range");
        toolTipLabel = new JLabel("Select a block to move to.");

//        setLayout(new GridBagLayout());

        GridBagConstraints gbc = ComponentUtils.verticalGBC();
        abilityPanel = new JPanel();
        abilityPanel.setLayout(new GridBagLayout());

//        add(abilityPanel, gbc);

//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        add(speedFL, gbc);
//        gbc.gridy = 1;
//        add(distanceFL, gbc);
//        gbc.gridy = 2;
//        add(jumpFL, gbc);
//        gbc.gridy = 3;

        JPanel topHalf = ComponentUtils.createTransparentPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        topHalf.add(speedFL, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topHalf.add(distanceFL, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        topHalf.add(jumpFL, gbc);
//        topHalf.add(selection, BorderLayout.LINE_START);
        topHalf.setBackground(ColorPalette.TRANSPARENT);
        topHalf.setOpaque(false);
        topHalf.setBorder(new EmptyBorder(10, 10, 10, 10));
        ComponentUtils.setSize(topHalf, width, (int) (height * .8));
        add(topHalf);




        JPanel bottomHalf = ComponentUtils.createTransparentPanel(new FlowLayout());
        bottomHalf.add(getExitButton());
//        topHalf.add(selection, BorderLayout.LINE_START);
        bottomHalf.setBackground(ColorPalette.TRANSPARENT);
        bottomHalf.setOpaque(false);
        bottomHalf.setBorder(new EmptyBorder(10, 10, 10, 10));
        ComponentUtils.setSize(bottomHalf, width, (int) (height * .2));
        add(bottomHalf);

//        add(getExitButton(), gbc);
    }

    public void set(Entity entity) {
        if (entity == null) { return; }

        // show distance and speed of selected unit
        Statistics stats = entity.get(Statistics.class);
        speedFL.setLabel(String.valueOf(stats.getScalarNode(Constants.SPEED).getTotal()));
        distanceFL.setLabel(String.valueOf(stats.getScalarNode(Constants.MOVE).getTotal()));
        jumpFL.value.setText(String.valueOf(stats.getScalarNode(Constants.JUMP).getTotal()));

//        abilityRangeFL.setLabel(monitor.toString());

        // set the list of abilities to reflect the unit if not already
//        if (unitToObserve == entity) { return; }
//        unitToObserve = entity;
//        abilitiesToObserve.clear();
//        abilitiesToObserve.addAll(unitToObserve.get(MoveSet.class).getCopy());

        // update the ui manually
        revalidate();
        repaint();
    }

//    public void set(Entity unit) {
//        if (unit == null) { return; }
//
//        // show distance and speed of selected unit
//        Statistics stats = unit.get(Statistics.class);
//        speedFL.setLabel(String.valueOf(stats.getScalarNode(Constants.SPEED).getTotal()));
//        distanceFL.setLabel(String.valueOf(stats.getScalarNode(Constants.MOVE).getTotal()));
////        abilityRangeFL.setLabel(monitor.toString());
//
//        // set the list of abilities to reflect the unit if not already
//        if (unitToObserve == unit) { return; }
//        unitToObserve = unit;
//        abilitiesToObserve.clear();
//        abilitiesToObserve.addAll(unitToObserve.get(MoveSet.class).getCopy());
//
//        // update the ui manually
//        revalidate();
//        repaint();
//    }
}
