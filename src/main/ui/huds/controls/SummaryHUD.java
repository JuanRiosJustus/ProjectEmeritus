package main.ui.huds.controls;

import main.game.components.Identity;
import main.game.components.SecondTimer;
import main.game.components.Statistics;
import main.game.components.StatusEffects;
import main.game.components.Type;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stats.node.StatsNodeModification;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabel;
import main.ui.panels.ControlPanelPane;
import main.ui.panels.StatScrollPane;
import main.utils.ComponentUtils;
import main.utils.MathUtils;
import main.utils.StringFormatter;
import main.utils.StringUtils;

import javax.swing.*;

import main.constants.Constants;

import java.awt.*;
import java.util.Map;


public class SummaryHUD extends ControlPanelPane {

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
    private JPanel statusPanel;
    private SecondTimer timer = new SecondTimer();
    private final int MINIMUM_MIDDLE_PANEL_ITEM_HEIGHT = 150;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private StatScrollPane pane;
    public SummaryHUD(int width, int height) {
        super(width, height, "Summary");

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        pane = createMiddlePanel(middle);
        middle.add(pane);
    }

    protected StatScrollPane createMiddlePanel(JComponent reference) {
        return new StatScrollPane(
                (int)reference.getPreferredSize().getWidth(),
                (int)reference.getPreferredSize().getHeight(),
                new String[]{
                        Constants.HEALTH, Constants.ENERGY,
                        Constants.LEVEL, Constants.MOVE,
                        Constants.CLIMB, Constants.SPEED,
                        Constants.PHYSICAL_ATTACK, Constants.MAGICAL_ATTACK,
                        Constants.PHYSICAL_DEFENSE, Constants.MAGICAL_DEFENSE
                }
        );
    }

    protected JScrollPane createTopRightPanel(JComponent reference) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        nameFieldLabel = new JKeyLabel("", ""); //ComponentUtils.createFieldLabel("","[Name Field]");
        nameFieldLabel.value.setOpaque(false);
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = new JKeyLabel("", "");// ComponentUtils.createFieldLabel("", "[Types Field]");
        typeFieldLabel.value.setOpaque(false);
        ComponentUtils.setTransparent(typeFieldLabel);

        Dimension dimension = reference.getPreferredSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        int rowHeight = height / 4;

        JPanel row0 = new JPanel();
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);

        JPanel row1 = new JPanel();
        healthFieldLabel = new JKeyLabel("Health: ", "DEFAULT");
        healthFieldLabel.value.setOpaque(false);
        healthFieldLabel.setValue("");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = new JKeyLabel("Energy: ", "DEFAULT");
        energyFieldLabel.value.setOpaque(false);
        energyFieldLabel.setValue("");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        levelFieldLabel = new JKeyLabel("Lvl: ", "DEFAULT");
        levelFieldLabel.value.setOpaque(false);
        ComponentUtils.setTransparent(levelFieldLabel);
        levelFieldLabel.setValue("");
        levelFieldLabel.key.setFont(levelFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row3.add(levelFieldLabel);
        experienceProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        experienceProgressBar.setValue(0);
        row3.add(experienceProgressBar);
        row3.setPreferredSize(new Dimension(width, rowHeight));


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

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        // TODO cacheing on this object or mak observable... so many ui calls
        if (gameModel == null) { return; }
        model = gameModel;
        if (currentUnit == null) { return; }

        Statistics stats = currentUnit.get(Statistics.class);
        topLeft.set(currentUnit);
        String temp = "";

        temp = currentUnit.get(Identity.class).toString() + " (" + stats.getName() + ")";
        if (!nameFieldLabel.value.getText().equalsIgnoreCase(temp)) {
            nameFieldLabel.value.setText(temp);
        }

        temp = currentUnit.get(Type.class).getTypes().toString();
        if (!typeFieldLabel.value.getText().equalsIgnoreCase(temp)) {
            typeFieldLabel.value.setText(temp);
        }

        ResourceNode health = stats.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.mapToRange(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setValue(String.valueOf(health.getCurrent()));
        }

        ResourceNode energy = stats.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.mapToRange(energy.getPercentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setValue(String.valueOf(energy.getCurrent()));
        }

        StatsNode level = stats.getStatsNode(Constants.LEVEL);
        ResourceNode current = stats.getResourceNode(Constants.EXPERIENCE);
        float percent = (float)current.getCurrent()/ (float)current.getTotal();
        percentage = (int) MathUtils.mapToRange(percent, 0, 1, 0, 100);
        boolean noLevelLabel = levelFieldLabel.value.getText().isBlank();
        if (experienceProgressBar.getValue() != percentage || noLevelLabel) {
            experienceProgressBar.setValue(percentage);
            levelFieldLabel.setValue(String.valueOf(level.getTotal()));
            pane.get(Constants.LEVEL).value.setText(level.getTotal() + "");
        }

        int buttonHeight = 0;

        statusPanel.removeAll();
        StatusEffects effects = currentUnit.get(StatusEffects.class);
        for (Map.Entry<String, Object> entry : effects.getStatusEffects().entrySet()) {
            JButton button = new JButton();
            temp = entry.getKey() + "'d from " + entry.getValue();
            if (!button.getText().equalsIgnoreCase(temp)) {
                button.setText(temp);
            }
            buttonHeight = (int) button.getPreferredSize().getHeight();
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            statusPanel.add(button);
        }

        for (String key : pane.getKeySet()) {
            StatsNode stat = stats.getStatsNode(key);
            String capitalized = StringUtils.capitalize(key)
                    .replaceAll(" ", "")
                    .toLowerCase();
            temp = stat.getBase() + " ( +" + stat.getMods() + " )";
            if (!pane.get(capitalized).value.getText().equalsIgnoreCase(temp)) {
                pane.get(capitalized).value.setText(temp);
            }

            if (stat.getName().equalsIgnoreCase(Constants.EXPERIENCE)) { continue; }
            if (stat.getName().equalsIgnoreCase(Constants.LEVEL)) { continue; }

            for (Map.Entry<Object, StatsNodeModification> entry : stat.getModifications().entrySet()) {
                JButton button = new JButton();
                String text = StringFormatter.format(
                        "{} {} by {} from {}",
                        pane.get(capitalized).key.getText().replace(": ", ""),
                        (entry.getValue().value > 0 ? "increased" : "decreased"),
                        (entry.getValue().value >= 0 && entry.getValue().value <= 1 ?
                                StringUtils.valueToPercentOrInteger(entry.getValue().value) : entry.getValue().value),
                        entry.getValue().source
                );
                button.setText(text);
                buttonHeight = (int) button.getPreferredSize().getHeight();
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                statusPanel.add(button);
            }
        }
        int paneWidth = (int) middle.getPreferredSize().getWidth() / 2;
        int paneHeight = buttonHeight * statusPanel.getComponentCount();
        statusPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
    }
}
