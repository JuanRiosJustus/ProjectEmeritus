package ui.panels;

import constants.ColorPalette;
import game.components.Name;
import game.components.Types;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stats.node.StringNode;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import utils.ComponentUtils;
import graphics.temporary.JKeyValueLabel;
import utils.MathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ConditionPanel extends JScene {

    private final JKeyValueLabel nameFieldLabel;
    private JKeyValueLabel statusFieldLabel;
    private final JKeyValueLabel typeFieldLabel;
    private final JKeyValueLabel healthFieldLabel;
    private final JProgressBar healthProgressBar;
    private final JKeyValueLabel energyFieldLabel;
    private final JProgressBar energyProgressBar;
    private Entity observing;
    private static final String defaultStr = "";
    private SelectionPanel selection = null;
    private final Map<String, JKeyValueLabel> labelMap = new HashMap<>();
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    public ConditionPanel(int width, int height) {
        super(width, height, "Condition");


        selection = new SelectionPanel(width, height);
        ComponentUtils.setSize(selection, (int) (width * .30), (int) (height * .4));

        JPanel topHalf = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        topHalf.add(selection, BorderLayout.LINE_START);
        topHalf.setBackground(ColorPalette.TRANSPARENT);
        topHalf.setOpaque(false);
        topHalf.setBorder(new EmptyBorder(10, 10, 10, 10));


        nameFieldLabel = ComponentUtils.createFieldLabel("Name","");
        JPanel row0 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row0.add(nameFieldLabel);
        topHalf.add(row0);

        typeFieldLabel = ComponentUtils.createFieldLabel("Types", "");
        JPanel row1 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row1.add(typeFieldLabel);
        topHalf.add(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        healthFieldLabel.setLabel("000,000,000");
        row2.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row2.add(healthProgressBar);
//        ComponentUtils.setSize(row1, (int)(width / .75), (int) (height * .3) / 2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        energyFieldLabel.setLabel("000,000,000");
        row3.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        row3.add(energyProgressBar);


        statusFieldLabel = ComponentUtils.createFieldLabel("Status", "");
        JPanel row4 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row4.add(statusFieldLabel);

        JPanel topRight = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        topRight.setLayout(new GridBagLayout());
        topRight.add(row0, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topRight.add(row1, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        topRight.add(row2, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        topRight.add(row3, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        topRight.add(row4, gbc);

        JScrollPane scrollPane = new JScrollPane(topRight,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, width, (int) (height * .45));

        topHalf.add(scrollPane);

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


        scrollPane = new JScrollPane(bottomHalf,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, width, (int) (height * .45));

        add(scrollPane);
        add(getExitButton());
    }

    private JPanel createJPanelColumn(Map<String, JKeyValueLabel> container, String[] values, int width, int height) {
        JPanel column = ComponentUtils.createTransparentPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row < values.length; row++) {
            gbc.gridy = row;
            gbc.gridx = 0;
            JKeyValueLabel label = ComponentUtils.createFieldLabel(values[row], "000", BoxLayout.X_AXIS);
            ComponentUtils.setSize(label, width, height / values.length);
            ComponentUtils.setTransparent(label);
            ComponentUtils.setTransparent(label.key);
            ComponentUtils.setTransparent(label.value);
            label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
            column.add(label, gbc);
            container.put(values[row], label);

        }
        ComponentUtils.setTransparent(column);
        return column;
    }

    public void set(Entity unit) {
        if (unit == null || observing == unit) { return; }
        observing = unit;
        Statistics stats = unit.get(Statistics.class);
//        nameFieldLabel.value.setText("SUUUUCCKS");
//        nameFieldLabel.field.setText("SUCKKKSKKSKS");
//        nameFieldLabel.revalidate();
//        nameFieldLabel.repaint();
//        System.out.println(getComponents().length);
//        removeAll();

//        nameFieldLabel.value.setText("SUUUUCCKS " + calls);
//        nameFieldLabel.field.setText("SUCKKKSKKSKS ");
//        removeAll();
//        System.out.println("Calls " + EventQueue.isDispatchThread());
//        try {
//            SwingUtilities.invokeAndWait(() -> {
//                nameFieldLabel.value.setText("SUUUUCCKS " + calls);
//                nameFieldLabel.field.setText("SUCKKKSKKSKS ");
//                nameFieldLabel.revalidate();
//                nameFieldLabel.repaint();
//                System.out.println(calls++ +" ?");
//            });
//        } catch (InterruptedException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//        nameFieldLabel.revalidate();
//        typeFieldLabel.setLabel("(" + unit.get(Types.class).value + ")");
////        statusFieldLabel.setLabel("Normal");
//
        selection.set(unit);

        nameFieldLabel.value.setText(unit.get(Name.class).value);
        typeFieldLabel.value.setText(unit.get(Types.class).value.toString());

        Health health = unit.get(Health.class);
        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
        healthProgressBar.setValue(percentage);
        healthFieldLabel.setLabel(String.valueOf(health.current));

        Energy energy = unit.get(Energy.class);
        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
        energyProgressBar.setValue(percentage);
        energyFieldLabel.setLabel(String.valueOf(energy.current));

        for (String key : stats.getNodeNames()) {
            StatsNode stat = stats.getNode(key);
            if (key == null || stat == null || stat instanceof StringNode) { continue; }
            String capitalized = handle(key);
            ScalarNode scalar = (ScalarNode) stat;
            if (labelMap.get(capitalized) != null) {
                labelMap.get(capitalized).key.setText(capitalized + ": ");
                labelMap.get(capitalized).value.setText(scalar.getBase() + " ( " + scalar.getMods() + " )");
            }
        }

        revalidate();
        repaint();
        logger.log("Updated condition panel for " + unit);
    }

    private String handle(String key) {
        StringBuilder sb = new StringBuilder();
        boolean finishedFirstCharacter = false;
        for (char c : key.toCharArray()) {
            if (Character.isUpperCase(c) && finishedFirstCharacter) {
                sb.append(' ');
            }
            sb.append(c);
            finishedFirstCharacter = true;
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private static String show(ScalarNode node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getMods();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods); //total + " (Base " + (mods > 0 ? "+" : "") + mods + " )";
    }
}
