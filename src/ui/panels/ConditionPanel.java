package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import game.components.Types;
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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ConditionPanel extends JScene {

    private final JFieldLabel nameFieldLabel;
    private JFieldLabel statusFieldLabel;
    private final JFieldLabel typeFieldLabel;
    private final JFieldLabel healthFieldLabel;
    private final JProgressBar healthProgressBar;
    private final JFieldLabel energyFieldLabel;
    private final JProgressBar energyProgressBar;
    private Entity observing;
    private static final String defaultStr = "";
    private SelectionPanel selection = null;

    private Map<String, JFieldLabel> labelMap = new HashMap<>();

    public ConditionPanel(int width, int height, String name) {
        super(width, height, name);


        selection = new SelectionPanel(width, height);
        ComponentUtils.setSize(selection, (int) (width * .30), (int) (height * .4));

        JPanel topHalf = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        topHalf.add(selection, BorderLayout.LINE_START);
        topHalf.setBackground(ColorPalette.TRANSPARENT);
        topHalf.setOpaque(false);
        topHalf.setBorder(new EmptyBorder(10, 10, 10, 10));

        selection.setBackground(ColorPalette.RED);





        nameFieldLabel = ComponentUtils.createFieldLabel("Name", "");
        typeFieldLabel = ComponentUtils.createFieldLabel("Types", "");
        JPanel row1 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row1.add(nameFieldLabel);
        row1.add(typeFieldLabel);
        topHalf.add(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        healthFieldLabel.setLabel("000,000,000");
        row2.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(55);
//        healthProgressBar.setString("yoo");
//        healthProgressBar.setBorderPainted(true);
//        healthProgressBar.setStringPainted(true);
        row2.add(healthProgressBar);
        row2.setBackground(ColorPalette.BLUE);
//        ComponentUtils.setSize(row1, (int)(width / .75), (int) (height * .3) / 2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        energyFieldLabel.setLabel("000,000,000");
        row3.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        row3.add(energyProgressBar);

        JPanel topRight = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        topRight.setLayout(new GridBagLayout());
        topRight.add(row1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topRight.add(row2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        topRight.add(row3, gbc);
        topHalf.add(topRight);

        ComponentUtils.setSize(topHalf, width, (int) (height * .45));
        add(topHalf);


        JPanel bottomHalf = ComponentUtils.createTransparentPanel(new FlowLayout());

        int columns = 3;

        JPanel col;
        col = createJPanelColumn(labelMap,
                new String[]{"Level", "Jump", "Move", "Speed"}, width / columns, height / 3);
        ComponentUtils.setTransparent(col);
        bottomHalf.add(col);

        col = createJPanelColumn(labelMap,
                new String[]{"Health", "Energy", "HealthRegen", "ManaRegen"}, width / columns, height / 3);
        ComponentUtils.setTransparent(col);
        bottomHalf.add(col);

        col = createJPanelColumn(labelMap,
                new String[]{"Physical Attack", "Physical Defense", "Magical Attack", "Magical Defense"}, width / columns, height / 3);
        ComponentUtils.setTransparent(col);
        bottomHalf.add(col);


        JScrollPane scrollPane = new JScrollPane(bottomHalf,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, width, (int) (height * .45));

        add(scrollPane);

        add(getExitButton());
    }

    private JPanel createJPanelColumn(Map<String, JFieldLabel> container, String[] values, int width, int height) {
        JPanel column = ComponentUtils.createTransparentPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row < values.length; row++) {
            gbc.gridy = row;
            gbc.gridx = 0;
            JFieldLabel label = ComponentUtils.createFieldLabel(values[row], "000", BoxLayout.X_AXIS);
            ComponentUtils.setSize(label, width, height / values.length);
            ComponentUtils.setTransparent(label);
            ComponentUtils.setTransparent(label.field);
            ComponentUtils.setTransparent(label.value);
            label.field.setFont(label.field.getFont().deriveFont(Font.BOLD));
            column.add(label, gbc);
            container.put(values[row], label);

        }
        ComponentUtils.setTransparent(column);
        return column;
    }

    private int calls = 0;
    public void set(Entity unit) {
        if (unit == null) { return; }

        observing = unit;
//        if (!update) { return; }

        Statistics stats = unit.get(Statistics.class);
//        nameFieldLabel.value.setText("SUUUUCCKS");
//        nameFieldLabel.field.setText("SUCKKKSKKSKS");
//        nameFieldLabel.revalidate();
//        nameFieldLabel.repaint();
//        System.out.println(getComponents().length);
//        removeAll();

        nameFieldLabel.value.setText("SUUUUCCKS " + calls);
        nameFieldLabel.field.setText("SUCKKKSKKSKS ");
        System.out.println(calls +" ?");
//        typeFieldLabel.setLabel("(" + unit.get(Types.class).value + ")");
////        statusFieldLabel.setLabel("Normal");
//
//        Health health = unit.get(Health.class);
//        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
//        healthProgressBar.setValue(0);
//        healthFieldLabel.setLabel(String.valueOf(health.current));
//
//        Energy energy = unit.get(Energy.class);
//        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
//        energyProgressBar.setValue(0);
//        energyFieldLabel.setLabel(String.valueOf(energy.current));


//        labelMap.get("Health").field.setText(String.valueOf(unit.get(Health.class).current));
//        labelMap.get("Energy").field.setText(String.valueOf(unit.get(Energy.class).current));

//        distanceFL.setLabel(show(stats.getScalarNode(Constants.MOVE)));
//        magicalAttackFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_ATTACK)));
//        magicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.MAGICAL_DEFENSE)));
//        physicalAttackFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_ATTACK)));
//        physicalDefenseFL.setLabel(show(stats.getScalarNode(Constants.PHYSICAL_DEFENSE)));
//        speedFL.setLabel(show(stats.getScalarNode(Constants.SPEED)));

        revalidate();
        repaint();
        update = false;
    }

    private static String show(ScalarNode node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getMods();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods); //total + " (Base " + (mods > 0 ? "+" : "") + mods + " )";
    }
}
