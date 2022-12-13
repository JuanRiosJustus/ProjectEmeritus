package ui.panels;

import constants.Constants;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stats.node.ScalarNode;
import graphics.JScene;
import graphics.temporary.JFieldLabel;
import utils.MathUtils;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;

import static utils.ComponentUtils.embolden;

public class SummaryPanel2 extends JScene {

    private final JLabel[] health = new JLabel[]{
            new JLabel("Health"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] energy = new JLabel[]{
            new JLabel("Energy"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] phyAtk = new JLabel[]{
            new JLabel("Phy Atk"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] phyDef = new JLabel[]{
            new JLabel("Phy Def"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] mgcAtk = new JLabel[]{
            new JLabel("Mgc Atk"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] mgcDef = new JLabel[]{
            new JLabel("Mgc Def"), new JLabel("0"), new JLabel("0")
    };

    private final JLabel[] speed = new JLabel[]{
            new JLabel("Speed"), new JLabel("0"), new JLabel("0")
    };
    private final JLabel[] distance = new JLabel[]{
            new JLabel("Distance"), new JLabel("0"), new JLabel("0")
    };

    private JLabel healthProgressBarLabel = new JLabel("100");
    private JProgressBar healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);

    private JLabel energyProgressBarLabel = new JLabel("10");
    private JProgressBar energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);



    private JFieldLabel distanceFL;
    private JFieldLabel speedFL;
//    private JFieldLabel speedFL;
    private JFieldLabel distanceFieldLabel;
    private Entity observing;
    private static final String defaultStr = "";

    public SummaryPanel2() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT + 100, "Summary");

        setLayout(new GridBagLayout());

        JLabel nameLabel = new JLabel("NAME");
        JLabel valueLabel = new JLabel("VALUE");
        JLabel bonusLabel = new JLabel("BONUS");
        int rightPad = 5;
        Insets startingInset = new Insets(0, 0, 10, rightPad);
        initLabels(0, embolden(nameLabel), embolden(valueLabel), embolden(bonusLabel), startingInset);

        initLabels(1, embolden(health[0]), health[1], health[2], new Insets(0, 0, 5, rightPad));
        initProgress(2, healthProgressBarLabel, healthProgressBar, new Insets(0, 0, 5, rightPad));
        healthProgressBar.setValue(76);
        initLabels(3, embolden(energy[0]), energy[1], energy[2], new Insets(0, 0, 5, rightPad));
        energyProgressBar.setValue(89);
        initProgress(4, energyProgressBarLabel, energyProgressBar, new Insets(0, 0, 10, rightPad));

        initLabels(5, embolden(phyAtk[0]), phyAtk[1], phyAtk[2], new Insets(0, 0, 5, rightPad));
        initLabels(6, embolden(phyDef[0]), phyDef[1], phyDef[2], new Insets(0, 0, 5, rightPad));
        initLabels(7, embolden(mgcAtk[0]), mgcAtk[1], mgcAtk[2], new Insets(0, 0, 5, rightPad));
        initLabels(8, embolden(mgcDef[0]), mgcDef[1], mgcDef[2], new Insets(0, 0, 10, rightPad));

        initLabels(9, embolden(speed[0]), speed[1], speed[2], new Insets(0, 0, 5, rightPad));
        initLabels(10, embolden(distance[0]), distance[1], distance[2], new Insets(0, 0, 10, rightPad));

//        setBackground(Color.BLUE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
//        gbc.weightx = 1;
        add(getExitButton(), gbc);
    }

    public void set(Entity unit) {
        observing = unit;
        if (unit == null) { return; }


        System.out.println("Updating to view " + unit);

        Statistics unitStats = unit.get(Statistics.class);
//        nameFieldLabel.setLabel(stats.getStringNode(Constants.NAME).value);
//        typeFieldLabel.setLabel("(" + stats.getStringNode(Constants.TYPE).value + ")");
//        statusFieldLabel.setLabel("Normal");
//
        Health unitHealth = unit.get(Health.class);
        set(health[1], health[2], unitHealth.node);

        int percentage = (int) MathUtils.mapToRange(unitHealth.percentage(), 0, 1, 0, 100);
        healthProgressBar.setValue(percentage);
        healthProgressBarLabel.setText(String.valueOf(unitHealth.current));
//
        Energy unitEnergy = unit.get(Energy.class);
        set(energy[1], energy[2], unitEnergy.node);

        percentage = (int) MathUtils.mapToRange(unitEnergy.percentage(), 0, 1, 0, 100);
        energyProgressBar.setValue(percentage);
        energyProgressBarLabel.setText(String.valueOf(unitEnergy.current));

        set(phyAtk[1], phyAtk[2], unitStats.getScalarNode(Constants.PHYSICAL_ATTACK));
        set(phyDef[1], phyDef[2], unitStats.getScalarNode(Constants.PHYSICAL_DEFENSE));
        set(mgcAtk[1], mgcAtk[2], unitStats.getScalarNode(Constants.MAGICAL_ATTACK));
        set(mgcDef[1], mgcDef[2], unitStats.getScalarNode(Constants.MAGICAL_DEFENSE));

        set(speed[1], speed[2], unitStats.getScalarNode(Constants.SPEED));
        set(distance[1], distance[2], unitStats.getScalarNode(Constants.DISTANCE));


//        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
//        energyProgressBar.setValue(percentage);
//        energyFieldLabel.setLabel(String.valueOf(energy.current));
//
//        distanceFL.setLabel(show(stats.getScalarNode(Constants.DISTANCE)));
//        magicalAttackFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_ATTACK)));
//        magicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_DEFENSE)));
//        physicalAttackFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_ATTACK)));
//        physicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_DEFENSE)));
//        speedFL.setLabel(show(stats.getScalarNode(Constants.SPEED)));
//        update = false;
    }

    private void set(JLabel value, JLabel bonus, ScalarNode node) {
        value.setText(String.valueOf(node.getTotal()));
        if (node.getMods() == 0) {
            bonus.setText("");
        } else {
            bonus.setText((node.getMods() > 0 ? "+" : "") + node.getMods());
        }
    }
    private void initLabels(int row, JLabel name, JLabel value, JLabel bonus, Insets padding) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
//        gbc.weightx = .2;
        gbc.insets = padding;

        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setVerticalAlignment(SwingConstants.CENTER);
        add(name, gbc);

        gbc.gridx = 1;
        gbc.weightx = .9;
        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setVerticalAlignment(SwingConstants.CENTER);
        add(value, gbc);

        gbc.gridx = 2;
//        gbc.weightx = .2;
        bonus.setHorizontalAlignment(SwingConstants.CENTER);
        bonus.setVerticalAlignment(SwingConstants.CENTER);
        add(bonus, gbc);
    }

    private void initProgress(int row, JLabel name, JProgressBar progress, Insets padding) {
        int[] v = new int[]{5, 6};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = .3;
        gbc.insets = padding;

        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setVerticalAlignment(SwingConstants.CENTER);
        add(name, gbc);

        gbc.gridx = 1;
        gbc.weightx = .7;
        gbc.gridwidth = 2;
        add(progress, gbc);
    }

    private static String show(ScalarNode node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getMods();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods); //total + " (Base " + (mods > 0 ? "+" : "") + mods + " )";
    }
}
