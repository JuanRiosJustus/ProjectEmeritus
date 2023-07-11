package ui.panels;

import game.components.NameTag;
import game.components.StatusEffects;
import game.components.Tile;
import game.components.Type;
import game.components.Types;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Level;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ResourceNode;
import game.stats.node.StatsNode;
import game.stats.node.StatsNodeModification;
import graphics.JScene;
import logging.ELogger;
import logging.ELoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;
import utils.MathUtils;
import utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import constants.ColorPalette;
import constants.Constants;

import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SummaryPanel extends ControlPanelInnerTemplate {

    private JKeyLabel nameFieldLabel;
    // private JKeyLabel statusFieldLabel;
    private JKeyLabel typeFieldLabel;
    private JKeyLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private Entity observing;
    private static final String defaultStr = "";
    private final Map<String, JKeyLabel> labelMap = new HashMap<>();
    private JPanel statusPanel;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public SummaryPanel(int width, int height) {
        super(width, (int) (height * .9), SummaryPanel.class.getSimpleName());

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
    }

    protected JScrollPane createMiddlePanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        int columnWidth = (int) (reference.getPreferredSize().getWidth() * .5);
        int height = (int) reference.getPreferredSize().getHeight();
        JPanel col;

        col = ComponentUtils.createJPanelColumn(labelMap, 
            new String[]{Constants.HEALTH, Constants.PHYSICAL_ATTACK, 
                Constants.PHYSICAL_DEFENSE, Constants.MAGICAL_ATTACK, 
                Constants.MAGICAL_DEFENSE}, 
            columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);

        gbc.gridx = 1;
        col = ComponentUtils.createJPanelColumn(labelMap, 
            new String[]{Constants.LEVEL, Constants.ENERGY, Constants.CLIMB, Constants.MOVE, Constants.SPEED}, 
            columnWidth, height);
        ComponentUtils.setTransparent(col);
        result.add(col, gbc);


        JScrollPane scrollPane = new JScrollPane(result,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    protected JScrollPane createTopRightPanel(JComponent reference) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        Dimension dimension = reference.getPreferredSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        int rowHeight = height / 4;

        JPanel row0 = new JPanel();
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);

        JPanel row1 = new JPanel();
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", defaultStr);
        ComponentUtils.setTransparent(healthFieldLabel);
        healthFieldLabel.setLabel("100%");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setLabel("100%");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));
        

        // JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        // statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        // ComponentUtils.setTransparent(statusFieldLabel);
        // row3.add(statusFieldLabel);
        // row3.setPreferredSize(new Dimension(width, rowHeight));

        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(0, 1));
        statusPanel.setPreferredSize(reference.getPreferredSize());

        content.add(row0);
        content.add(row1);
        content.add(row2);
        // content.add(row3);
        content.add(statusPanel);
        content.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(
            content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setTransparent(scrollPane.getViewport());
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    public void set(GameModel model, Entity tileEntity) {
        // if (unit == null || observing == unit) { return; }
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }

        forceSetUi(tileEntity);
    }

    public void forceSetUi(Entity tileEntity) {
        Tile tile = tileEntity.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        observing = tile.unit;
        Summary summary = observing.get(Summary.class);
        topLeft.set(observing);

        nameFieldLabel.label.setText(observing.get(Summary.class).getName());
        typeFieldLabel.label.setText(observing.get(Type.class).getTypes().toString());

        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setLabel(String.valueOf(health.current));
        }

        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setLabel(String.valueOf(energy.current));
        }

                        
        labelMap.get(Constants.LEVEL).key.setText(Constants.LEVEL + ": ");                
        Level level = observing.get(Level.class);
        labelMap.get(Constants.LEVEL).label.setText(level.current + " ( " + level.experience + " / " + level.threshold + " )");

        int buttonHeight = 0;

        statusPanel.removeAll();
        StatusEffects effects = observing.get(StatusEffects.class);
        for (Map.Entry<String, Object> entry : effects.getStatusEffects().entrySet()) {      
            JButton button = new JButton();
            button.setText(entry.getKey() + "'d from " + entry.getValue());
            buttonHeight = (int) button.getPreferredSize().getHeight();
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            statusPanel.add(button);
        }

        for (String key : summary.getKeySet()) {
            StatsNode stat = summary.getStatsNode(key);
            if (key == null || stat == null) { continue; }
            String capitalized = StringUtils.capitalize(key).replaceAll(" ", ""); 
            if (labelMap.get(capitalized) != null) {
                labelMap.get(capitalized).key.setText(capitalized + ": ");
                labelMap.get(capitalized).label.setText(stat.getBase() + " ( +" + stat.getMods() + " )");
            }

            for (Map.Entry<Object, StatsNodeModification> entry : stat.getModifications().entrySet()) {
                JButton button = new JButton();
                String text = capitalized + " " + (entry.getValue().value > 0 ? "increased" : "decreased");
                button.setText(text + " from " + entry.getValue().source);
                buttonHeight = (int) button.getPreferredSize().getHeight();
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                statusPanel.add(button);
            }
        }
        int paneWidth = (int) middleThird.getPreferredSize().getWidth() / 2;
        int paneHeight = buttonHeight * statusPanel.getComponentCount();
        statusPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
    }
}
