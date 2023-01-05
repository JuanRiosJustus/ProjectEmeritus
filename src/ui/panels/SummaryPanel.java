package ui.panels;

import constants.Constants;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stats.node.ScalarNode;
import graphics.JScene;
import utils.ComponentUtils;
import graphics.temporary.JFieldLabel;
import utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

public class SummaryPanel extends JScene {

    private JFieldLabel nameFieldLabel;
    private JFieldLabel statusFieldLabel;
    private JFieldLabel typeFieldLabel;
    private JFieldLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JFieldLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private JFieldLabel magicalAttackFL;
    private JFieldLabel magicalDefenseFL;
    private JFieldLabel physicalAttackFL;
    private JFieldLabel physicalDefenseFL;
    private JFieldLabel distanceFL;
    private JFieldLabel speedFL;
//    private JFieldLabel speedFL;
    private JFieldLabel distanceFieldLabel;
    private Entity observing;
    private static final String defaultStr = "";

    public SummaryPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT + 100, "Summary");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = ComponentUtils.verticalGBC();

        JPanel panel = new JPanel();

        nameFieldLabel = ComponentUtils.createFieldLabel("", "???");
        typeFieldLabel = ComponentUtils.createFieldLabel("", "???");
        panel.add(nameFieldLabel);
        panel.add(typeFieldLabel);
        add(panel, gbc);

        statusFieldLabel = ComponentUtils.createFieldLabel("Status", defaultStr);
        add(statusFieldLabel, gbc);

        JPanel healthPanel = new JPanel();
        healthPanel.setLayout(new GridBagLayout());

        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        add(healthFieldLabel, gbc);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        add(healthProgressBar, gbc);

        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        add(energyFieldLabel, gbc);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        add(energyProgressBar, gbc);


        physicalAttackFL = ComponentUtils.createFieldLabel("Physical Attack", defaultStr);
        add(physicalAttackFL, gbc);
        physicalDefenseFL = ComponentUtils.createFieldLabel("Physical Defense", defaultStr);
        add(physicalDefenseFL, gbc);
        magicalAttackFL = ComponentUtils.createFieldLabel("Magical Attack", defaultStr);
        add(magicalAttackFL, gbc);
        magicalDefenseFL = ComponentUtils.createFieldLabel("Magical Defense", defaultStr);
        add(magicalDefenseFL, gbc);
        speedFL = ComponentUtils.createFieldLabel("Speed", defaultStr);
        add(speedFL, gbc);
        distanceFL = ComponentUtils.createFieldLabel("Distance", defaultStr);
        add(distanceFL, gbc);

//        setBackground(Color.BLUE);
        add(getExitButton(), gbc);
    }

    public void set(Entity unit) {
        observing = unit;
//        if (!update) { return; }

        Statistics stats = unit.get(Statistics.class);
        nameFieldLabel.setLabel(stats.getStringNode(Constants.NAME).value);
        typeFieldLabel.setLabel("(" + stats.getStringNode(Constants.TYPE).value + ")");
        statusFieldLabel.setLabel("Normal");

        Health health = unit.get(Health.class);
        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
        healthProgressBar.setValue(percentage);
        healthFieldLabel.setLabel(String.valueOf(health.current));

        Energy energy = unit.get(Energy.class);
        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
        energyProgressBar.setValue(percentage);
        energyFieldLabel.setLabel(String.valueOf(energy.current));

        distanceFL.setLabel(show(stats.getScalarNode(Constants.MOVE)));
        magicalAttackFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_ATTACK)));
        magicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_DEFENSE)));
        physicalAttackFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_ATTACK)));
        physicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_DEFENSE)));
        speedFL.setLabel(show(stats.getScalarNode(Constants.SPEED)));
        update = false;
    }

    private static String show(ScalarNode node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getMods();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods); //total + " (Base " + (mods > 0 ? "+" : "") + mods + " )";
    }
}
