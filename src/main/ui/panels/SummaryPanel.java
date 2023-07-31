package main.ui.panels;

import main.constants.ColorPalette;
import main.game.components.NameTag;
import main.game.components.SecondTimer;
import main.game.components.Statistics;
import main.game.components.StatusEffects;
import main.game.components.Tile;
import main.game.components.Type;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stats.node.StatsNodeModification;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabel;
import main.utils.ComponentUtils;
import main.utils.MathUtils;
import main.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.constants.Constants;

import java.awt.*;
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
    private JKeyLabel levelFieldLabel;
    private JProgressBar experienceProgressBar;
    private static final String defaultStr = "";
    private final Map<String, JKeyLabel> labelMap = new HashMap<>();
    private JPanel statusPanel;
    private SecondTimer timer = new SecondTimer();
    private final int MINIMUM_MIDDLE_PANEL_ITEM_HEIGHT = 150;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public SummaryPanel(int width, int height) {
        super(width, height, SummaryPanel.class.getSimpleName());

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
        double height = reference.getPreferredSize().getHeight();
        double width = reference.getPreferredSize().getWidth();

        result.add(
            createJPanelColumn(
                labelMap, 
                new String[]{ 
                    Constants.HEALTH, Constants.ENERGY,
                    Constants.LEVEL, Constants.MOVE,
                    Constants.CLIMB, Constants.SPEED,
                    Constants.PHYSICAL_ATTACK, Constants.MAGICAL_ATTACK,
                    Constants.PHYSICAL_DEFENSE, Constants.MAGICAL_DEFENSE
                }, 
                (int)Math.max(width, width),
                (int)Math.max(MINIMUM_MIDDLE_PANEL_ITEM_HEIGHT, height)
                
            ), gbc
        );

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

    @Override
    protected void update() {

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
        healthFieldLabel.setLabel("");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", defaultStr);
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setLabel("");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        levelFieldLabel = ComponentUtils.createFieldLabel("Lvl: ", defaultStr);
        ComponentUtils.setTransparent(levelFieldLabel);
        levelFieldLabel.setLabel("");
        levelFieldLabel.key.setFont(levelFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row3.add(levelFieldLabel);
        experienceProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        experienceProgressBar.setValue(0);
        row3.add(experienceProgressBar);
        row3.setPreferredSize(new Dimension(width, rowHeight));
        

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
        content.add(row3);
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

    public void set(GameModel model, Entity entity) {
        if (entity == null) { return; }
        Tile tile = entity.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }

        observing = tile.unit;
        update(model);
    }

    @Override
    public void update(GameModel model) {
        if (observing == null) { return; }

        Statistics stats = observing.get(Statistics.class);
        topLeft.set(observing);

        nameFieldLabel.label.setText(observing.get(NameTag.class).toString());
        typeFieldLabel.label.setText(observing.get(Type.class).getTypes().toString());

        ResourceNode health = stats.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.mapToRange(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setLabel(String.valueOf(health.getCurrent()));
        }

        ResourceNode energy = stats.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.mapToRange(energy.getPercentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setLabel(String.valueOf(energy.getCurrent()));
        }

        StatsNode level = stats.getStatsNode(Constants.LEVEL);
        ResourceNode current = stats.getResourceNode(Constants.EXPERIENCE);
        float percent = (float)current.getCurrent()/ (float)current.getTotal();
        percentage = (int) MathUtils.mapToRange(percent, 0, 1, 0, 100);
        boolean noLevelLabel = levelFieldLabel.label.getText().isBlank();
        if (experienceProgressBar.getValue() != percentage || noLevelLabel) {
            experienceProgressBar.setValue(percentage);
            levelFieldLabel.setLabel(String.valueOf(level.getTotal()));
            labelMap.get(Constants.LEVEL.toLowerCase()).label.setText(level.getTotal() + "");
        }

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

        for (String key : labelMap.keySet()) {
            StatsNode stat = stats.getStatsNode(key);
            if (stat == null) { continue; }
            String capitalized = StringUtils.capitalize(key)
                    .replaceAll(" ", "")
                    .toLowerCase();
            if (labelMap.get(capitalized) != null) {
                labelMap.get(capitalized).label.setText(stat.getBase() + " ( +" + stat.getMods() + " )");
            }

            if (stat.getName().equalsIgnoreCase(Constants.EXPERIENCE)) { continue; }
            if (stat.getName().equalsIgnoreCase(Constants.LEVEL)) { continue; }
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

    public static JPanel createJPanelColumn(Map<String, JKeyLabel> container, String[] values, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        int labelHeight = height / (values.length / 2);
        int labelWidth = width / 2;

        for (int index = 0; index < values.length; index++) {
            String value = values[index];
            JKeyLabel label = new JKeyLabel(StringUtils.spaceByCapitalization(value) + ": ", "???");
            // label.setBackground(ColorPalette.getRandomColor());
            label.setBackground(ColorPalette.TRANSPARENT);
            label.setPreferredSize(new Dimension(labelWidth, labelHeight));
            label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
            // label.label.setOpaque(false);
            // label.setBackground(ColorPalette.getRandomColor());

            gbc.gridx = (index % 2);
            gbc.gridy = (index / 2);
            panel.add(label, gbc);
            container.put(value.toLowerCase(), label);
        }

        // ComponentUtils.setTransparent(column);
        panel.setBorder(new EmptyBorder(5, 5, 5,5));
        return panel;
    }
}
