package ui.panels;


import constants.GameStateKey;
import game.components.NameTag;
import game.components.Tile;
import game.components.Types;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MovementPanel extends JScene {

    private JKeyLabel nameFieldLabel;
    private JKeyLabel statusFieldLabel;
    private JKeyLabel typeFieldLabel;
    private JKeyLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private Entity observing;
    private final Map<String, JKeyLabel> labelMap = new HashMap<>();
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    private final JButton undoButton = new JButton("Undo Movement");

    private final ControlPanelSceneTemplate template;

    public MovementPanel(int width, int height) {
        super(width, height, "Movement");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        template = new ControlPanelSceneTemplate(width, (int) (height * .9), "SummaryPanelTemplate");
        add(template);

        createTopRightPanel(template.topRight);

        createBottomHalfPanel(template.innerScrollPaneContainer);

        add(getExitButton());
    }

    private void createBottomHalfPanel(JPanel bottomHalfPanel) {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        int columnWidth = (int) (bottomHalfPanel.getPreferredSize().getWidth() * .5);
        int height = (int) (bottomHalfPanel.getPreferredSize().getHeight() * 1.5);
        JPanel col;

        col = createJPanelColumn(labelMap,
                new String[]{"Climb", "Move", "Speed"}, columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);

        bottomHalfPanel.add(result);
    }

    

    private JPanel createJPanelColumn(Map<String, JKeyLabel> container, String[] values, int width, int height) {

        JPanel column = new JPanel();
        column.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        for (int row = 0; row < values.length; row++) {
            gbc.gridy = row;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.gridx = 0;
            JKeyLabel label = ComponentUtils.createFieldLabel(values[row], "", BoxLayout.X_AXIS);
            label.setPreferredSize(new Dimension(width, (int) (height * .1)));
//            ComponentUtils.setSize( label, width, (int) (height * .1));
            ComponentUtils.setTransparent(label);
            ComponentUtils.setTransparent(label.key);
            ComponentUtils.setTransparent(label.label);
            label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
            column.add(label, gbc);
            container.put(values[row], label);
        }

        ComponentUtils.setTransparent(column);
        return column;
    }

    private void createTopRightPanel(JPanel topRightPanel) {
        JPanel result = ComponentUtils.createTransparentPanel(new GridBagLayout());
        ComponentUtils.setTransparent(result);

        nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        ComponentUtils.setTransparent(statusFieldLabel);

        Dimension dimension = topRightPanel.getPreferredSize();
        int rowHeights = (int) (dimension.getHeight() / 3);
        int width = (int) dimension.getWidth();

        JPanel row0 = ComponentUtils.createTransparentPanel(new FlowLayout());
        row0.add(nameFieldLabel);
        row0.add(statusFieldLabel);
        ComponentUtils.setSize(row0, width, rowHeights);
        ComponentUtils.setTransparent(row0);

        JPanel row1 = ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", "");
        row1.add(undoButton);
        ComponentUtils.setSize(row1, (int) dimension.getWidth(), rowHeights);
        ComponentUtils.setTransparent(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", "");
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setLabel("100%");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        ComponentUtils.setTransparent(energyProgressBar);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        ComponentUtils.setSize(row2, width, rowHeights);
        ComponentUtils.setTransparent(row2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        ComponentUtils.setTransparent(statusFieldLabel);
        row3.add(statusFieldLabel);
        ComponentUtils.setSize(row3, width, rowHeights);
        ComponentUtils.setTransparent(row3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;

        result.add(row0, gbc);
        gbc.gridy = 1;
        result.add(undoButton, gbc);

        JScrollPane pane = new JScrollPane(result,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setPreferredSize(topRightPanel.getPreferredSize());

        topRightPanel.add(result);

    }

    public void set(GameModel model, Entity unit) {
        if (unit == null || observing == unit) { return; }
        Tile tile = unit.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        observing = tile.unit;
        Summary stats = observing.get(Summary.class);

        ComponentUtils.removeActionListeners(undoButton);
        undoButton.addActionListener(e -> {
            model.state.set(GameStateKey.UI_UNDO_MOVEMENT_PRESSED, true);
        });

        template.selectionPanel.set(observing);
        nameFieldLabel.label.setText(observing.get(Summary.class).getName());
        typeFieldLabel.label.setText(observing.get(Summary.class).getTypes().toString());

//        Health health = observing.get(Health.class);
//        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
//        healthProgressBar.setValue(percentage);
//        healthFieldLabel.setLabel(String.valueOf(health.current));
//
//        Energy energy = observing.get(Energy.class);
//        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
//        energyProgressBar.setValue(percentage);
//        energyFieldLabel.setLabel(String.valueOf(energy.current));

        for (String key : stats.getKeySet()) {
            StatsNode stat = stats.getNode(key);
            if (key == null || stat == null) { continue; }
            String capitalized = handle(key);
            ScalarNode scalar = (ScalarNode) stat;
            if (labelMap.get(capitalized) != null) {
                labelMap.get(capitalized).key.setText(capitalized + ": ");
                labelMap.get(capitalized).label.setText(scalar.getBase() + " ( " + scalar.getMods() + " )");
            }
        }

        revalidate();
        repaint();
        logger.info("Polling Movement panel for " + observing);
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

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods);
    }
}
