package ui.panels;

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
import graphics.temporary.JKeyValueLabel;
import utils.ComponentUtils;
import utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SummaryPanel extends JScene {

    private JKeyValueLabel nameFieldLabel;
    private JKeyValueLabel statusFieldLabel;
    private JKeyValueLabel typeFieldLabel;
    private JKeyValueLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyValueLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private Entity observing;
    private static final String defaultStr = "";
    private SelectionPanel selection = null;
    private final Map<String, JKeyValueLabel> labelMap = new HashMap<>();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    private final ControlPanelSceneTemplate template;

    public SummaryPanel(int width, int height) {
        super(width, height, "Summary");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        template = new ControlPanelSceneTemplate(width, (int) (height * .9), "SummaryPanelTemplate");
        add(template);

        createTopRightPanel(template.topRight);

        createBottomHalfPanel(template.bottomHalf);

        add(getExitButton());
    }

    private JScrollPane createBottomHalfPanel(JPanel reference) {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        int columnWidth = (int) (reference.getWidth() * .5);
        int height = reference.getHeight();
        JPanel col;

        col = createJPanelColumn(labelMap,
                new String[]{"Level", "Health", "Physical Attack", "Physical Defense",
                        "Magical Attack", "Magical Defense"}, columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);

        gbc.gridx = 1;
        col = createJPanelColumn(labelMap,
                new String[]{"Energy", "Jump", "Move", "Speed"}, columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);

        JScrollPane scrollPane = new JScrollPane(result,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, template.bottomHalf.getWidth(), template.bottomHalf.getHeight());

        reference.add(scrollPane);
        return scrollPane;
    }

    private JPanel createJPanelColumn(Map<String, JKeyValueLabel> container, String[] values, int width, int height) {

        JPanel column = new JPanel();
        column.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row < values.length; row++) {
            gbc.gridy = row;
            gbc.gridx = 0;
            JKeyValueLabel label = ComponentUtils.createFieldLabel(values[row], "", BoxLayout.X_AXIS);
            ComponentUtils.setSize( label, width, (int) (height * .2));
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

    private JPanel createTopRightPanel(JPanel reference) {
        JPanel firstGlancePanel = ComponentUtils.createTransparentPanel(new GridBagLayout());
        ComponentUtils.setTransparent(firstGlancePanel);

        nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        JPanel row0 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);
        ComponentUtils.setSize(row0, reference.getWidth(), reference.getHeight() / 4);
        ComponentUtils.setTransparent(row0);

        JPanel row1 = ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        ComponentUtils.setTransparent(healthFieldLabel);
        healthFieldLabel.setLabel("100%");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        ComponentUtils.setSize(row1, reference.getWidth(), reference.getHeight() / 4);
        ComponentUtils.setTransparent(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setLabel("100%");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        ComponentUtils.setTransparent(energyProgressBar);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        ComponentUtils.setSize(row2, reference.getWidth(), reference.getHeight() / 4);
        ComponentUtils.setTransparent(row2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        ComponentUtils.setTransparent(statusFieldLabel);
        row3.add(statusFieldLabel);
        ComponentUtils.setSize(row3, reference.getWidth(), reference.getHeight() / 4);
        ComponentUtils.setTransparent(row3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        firstGlancePanel.add(row3, gbc);
        gbc.gridy = 1;
        firstGlancePanel.add(row0, gbc);
        gbc.gridy = 2;
        firstGlancePanel.add(row1, gbc);
        gbc.gridy = 3;
        firstGlancePanel.add(row2, gbc);

        reference.add(firstGlancePanel);
        return firstGlancePanel;
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
        template.selectionPanel.set(unit);

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
