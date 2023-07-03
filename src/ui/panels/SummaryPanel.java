package ui.panels;

import game.components.NameTag;
import game.components.Tile;
import game.components.Types;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ScalarNode;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;
import utils.MathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SummaryPanel extends JScene {

    private JKeyLabel nameFieldLabel;
    private JKeyLabel statusFieldLabel;
    private JKeyLabel typeFieldLabel;
    private JKeyLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private Entity observing;
    private static final String defaultStr = "";
    private SelectionPanel selection = null;
    private final Map<String, JKeyLabel> labelMap = new HashMap<>();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    private final ControlPanelSceneTemplate template;

    public SummaryPanel(int width, int height) {
        super(width, height, "Summary");

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
        int height = (int) bottomHalfPanel.getPreferredSize().getHeight();
        JPanel col;

        col = ComponentUtils.createJPanelColumn(labelMap, 
            new String[]{"Health", "Physical Attack", "Physical Defense","Magical Attack", "Magical Defense"}, 
            columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);

        gbc.gridx = 1;
        col = ComponentUtils.createJPanelColumn(labelMap, 
            new String[]{"Level", "Energy", "Climb", "Move", "Speed"}, 
            columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);
//
//        JScrollPane scrollPane = new JScrollPane(result,
//                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
//        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
////        ComponentUtils.setTransparent(scrollPane);
////        ComponentUtils.setSize(scrollPane, template.bottomHalf.getWidth(), template.bottomHalf.getHeight());

        bottomHalfPanel.add(result);
    }

    private void createTopRightPanel(JPanel toAddTo) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        //ComponentUtils.createTransparentPanel(new GridBagLayout());
        ComponentUtils.setTransparent(content);

        nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        Dimension dimension = toAddTo.getPreferredSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        int rowHeight = height / 4;

        JPanel row0 = new JPanel();
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);
//        row0.setPreferredSize(new Dimension(width, rowHeight));
        ComponentUtils.setTransparent(row0);

        JPanel row1 = new JPanel();//ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        ComponentUtils.setTransparent(healthFieldLabel);
        healthFieldLabel.setLabel("100%");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));
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
        row2.setPreferredSize(new Dimension(width, rowHeight));
        ComponentUtils.setTransparent(row2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        ComponentUtils.setTransparent(statusFieldLabel);
        row3.add(statusFieldLabel);
        row3.setPreferredSize(new Dimension(width, rowHeight));
        ComponentUtils.setTransparent(row3);

        content.add(row0);
        content.add(row1);
        content.add(row2);
        content.add(row3);

        toAddTo.add(content);
    }

    public void set(GameModel model, Entity unit) {
        // if (unit == null || observing == unit) { return; }
        if (unit == null) { return; }
        Tile tile = unit.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        observing = tile.unit;
        Summary stats = observing.get(Summary.class);

        template.selectionPanel.set(observing);

        nameFieldLabel.label.setText(observing.get(Summary.class).getName());
        typeFieldLabel.label.setText(observing.get(Summary.class).getTypes().toString());

        Health health = observing.get(Health.class);
        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
        healthProgressBar.setValue(percentage);
        healthFieldLabel.setLabel(String.valueOf(health.current));

        Energy energy = observing.get(Energy.class);
        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
        energyProgressBar.setValue(percentage);
        energyFieldLabel.setLabel(String.valueOf(energy.current));

        for (String key : stats.getKeySet()) {
            ScalarNode stat = stats.getScalarNode(key);
            if (key == null || stat == null) { continue; }
            String capitalized = handle(key);
            if (labelMap.get(capitalized) != null) {
                labelMap.get(capitalized).key.setText(capitalized + ": ");
                labelMap.get(capitalized).label.setText(stat.getBase() + " ( +" + stat.getMods() + " )");
            }
        }

        revalidate();
        repaint();
        logger.info("Updated condition panel for " + observing);
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
