package main.ui.huds.controls.v2;

import main.game.components.*;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stats.node.StatsNodeModification;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JKeyLabel;
import main.ui.custom.JKeyProgress;
import main.ui.huds.controls.HUD;
import main.ui.panels.ImagePanel;
import main.ui.custom.JKeyLabelArray;
import main.utils.MathUtils;
import main.utils.StringFormatter;
import main.utils.StringUtils;

import javax.swing.*;

import main.constants.Constants;

import java.awt.*;
import java.awt.Dimension;
import java.util.Map;


public class SummaryHUD extends HUD {
    private JPanel tagPanel;
    private JPanel modificationPanel;
    private SecondTimer timer = new SecondTimer();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastViewedUnit = null;
    public int modCount = 0;
    private JKeyLabel typeField;
    private JKeyLabel nameField;
    private JKeyLabel experienceField;
    private JKeyProgress healthProgress;
    private JKeyProgress energyProgress;
    private JKeyProgress levelProgress;
    private boolean initialized = false;
    private final JKeyLabelArray combatStatPane;
    public SummaryHUD(int width, int height) {
        super(width, height, "Summary");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        // Image
        selection = new ImagePanel((int) (height * .2), (int) (height * .2));
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension((int) (height * .2), (int) (height * .2)));
        panel.add(selection);
        add(panel, constraints);

        constraints.gridy = 1;
        JScrollPane pane = createScrollingResourcePane(width, (int) (height * .2));
        add(pane, constraints);

        constraints.gridy = 2;
        combatStatPane = new JKeyLabelArray(
                width,
                (int) (height * .55),
                new String[]{
                        Constants.HEALTH, Constants.ENERGY,
                        Constants.LEVEL, Constants.MOVE,
                        Constants.CLIMB, Constants.SPEED,
                        Constants.STRENGTH, Constants.INTELLIGENCE,
                        Constants.DEXTERITY, Constants.WISDOM,
                        Constants.CONSTITUTION, Constants.CHARISMA,
                        Constants.RESISTANCE, Constants.LUCK
                }
        );
        add(combatStatPane, constraints);
//        setBackground(ColorPalette.BLACK);

        constraints.gridy = 3;
        getExitButton().setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(getExitButton(), constraints);
    }

    protected JScrollPane createScrollingResourcePane(int width, int height) {

        int fieldHeight = height / 5;
        typeField = new JKeyLabel(width, fieldHeight, "Type");
        nameField = new JKeyLabel(width, fieldHeight, "Name");
        experienceField = new JKeyLabel(width, fieldHeight, "Experience");

        JKeyLabel healthField = new JKeyLabel((int) (width * .25), fieldHeight, "Health");
        healthProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Health");
//        healthProgress.getKey().setText("~");

        JKeyLabel energyField = new JKeyLabel((int) (width * .25), fieldHeight, "Energy");
        energyProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Energy");
//        energyProgress.getKey().setText("~");

        JKeyLabel levelField = new JKeyLabel((int) (width * .25), fieldHeight, "Level");
        levelProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Level");
//        levelProgress.getKey().setText("~");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        // Create health field
        JPanel row1 = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = .25;
        row1.add(healthField, gbc);
        gbc.weightx = .75;
        gbc.gridx = 1;
        row1.add(healthProgress, gbc);
        row1.setPreferredSize(new Dimension(new Dimension(width, fieldHeight)));

        // Create energy field
        JPanel row2 = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = .25;
        row2.add(energyField, gbc);
        gbc.weightx = .75;
        gbc.gridx = 1;
        row2.add(energyProgress, gbc);
        row2.setPreferredSize(new Dimension(new Dimension(width, fieldHeight)));

        // Create level field
        JPanel row3 = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = .25;
        row3.add(levelField, gbc);
        gbc.weightx = .75;
        gbc.gridx = 1;
        row3.add(levelProgress, gbc);
        row3.setPreferredSize(new Dimension(new Dimension(width, fieldHeight)));

        // Add tags and mod fields
        tagPanel = new JPanel();
        tagPanel.setLayout(new GridLayout(0, 1));

        modificationPanel = new JPanel();
        modificationPanel.setLayout(new GridLayout(0, 1));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(typeField);
        content.add(nameField);
        content.add(row1);
        content.add(row2);
        content.add(row3);
        content.add(experienceField);
        content.add(tagPanel);
        content.add(modificationPanel);
//        content.setOpaque(false);

        return createScalingPane(width, height, content);
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
//        // TODO cacheing on this object or mak observable... so many ui calls
        if (gameModel == null) { return; }
        if (currentUnit == null) { return; }

        Summary summary = currentUnit.get(Summary.class);
        selection.set(currentUnit);

        if (!initialized) {
            updateUi(summary, true);
            initialized = true;
        } else {
            updateUi(summary, false);
        }
    }

    private void updateUi(Summary summary, boolean forceUpdate) {
        String temp = currentUnit.get(Identity.class).toString() + " (" + summary.getUnit() + ")";
        if (!nameField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            nameField.setLabel(temp);
        }

        temp = currentUnit.get(Type.class).getTypes().toString();
        if (!typeField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            typeField.setLabel(temp);
        }

        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.mapToRange(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgress.getValue() != percentage || !forceUpdate) {
            healthProgress.setValue(percentage);
            healthProgress.setKey(String.valueOf(health.getCurrent()));
        }

        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.mapToRange(energy.getPercentage(), 0, 1, 0, 100);
        if (energyProgress.getValue() != percentage || !forceUpdate) {
            energyProgress.setValue(percentage);
            energyProgress.setKey(String.valueOf(energy.getCurrent()));
        }

        ResourceNode current = summary.getResourceNode(Constants.EXPERIENCE);
        float percent = (float)current.getCurrent()/ (float)current.getTotal();
        percentage = (int) MathUtils.mapToRange(percent, 0, 1, 0, 100);
        if (levelProgress.getValue() != percentage || !forceUpdate) {
            levelProgress.setValue(percentage);
            StatsNode level = summary.getStatsNode(Constants.LEVEL);
            levelProgress.setKey(String.valueOf(level.getTotal()));
            combatStatPane.get(Constants.LEVEL).setLabel(level.getTotal() + "");
        }
        temp = current.getCurrent() + " / " + current.getTotal();
        if (!experienceField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            experienceField.setLabel(temp);
        }

        int buttonHeight = 0;

        Tags tags = currentUnit.get(Tags.class);
        if (tags.getTags().size() != tagPanel.getComponentCount() || !forceUpdate) {
            // to status panel?
            tagPanel.removeAll();
            for (Map.Entry<String, Object> entry : tags.getTags().entrySet()) {
                // Create of button if not available, else use existing
                JButton buttonTag = new JButton();

                buttonHeight = (int) buttonTag.getPreferredSize().getHeight();
                buttonTag.setBorderPainted(false);
                buttonTag.setFocusPainted(false);
////                buttonTag.setOpaque(true);
                buttonTag.setVisible(true);
////                buttonTag.setBackground(ColorPalette.getRandomColor());
                buttonTag.setText(entry.getKey() + "'d from " + entry.getValue());

                tagPanel.add(buttonTag);
            }
            int paneWidth = (int) getPreferredSize().getWidth() / 2;
            int paneHeight = buttonHeight * tagPanel.getComponentCount();
            tagPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
        }

        if (modCount != summary.getModificationCount() || lastViewedUnit != currentUnit || !forceUpdate) {
            modificationPanel.removeAll();
            for (String key : summary.getStatNodeNames()) {
                if (key.equalsIgnoreCase(Constants.EXPERIENCE)) { continue; }
                if (key.equalsIgnoreCase(Constants.LEVEL)) { continue; }
                StatsNode node = summary.getStatsNode(key);
                temp = node.getBase() + " ( " + (node.getModified() > 0 ? "+" : "") + node.getModified() + " )";
                if (!combatStatPane.get(node.getName()).getValue().equalsIgnoreCase(temp)) {
                    combatStatPane.get(node.getName()).setLabel(temp);
                }

                for (Map.Entry<Object, StatsNodeModification> entry : node.getModifications().entrySet()) {
                    JButton button = new JButton();
                    String text = StringFormatter.format(
                            "{}{} {} from {}",
                            (entry.getValue().value > 0 ? "+" : "-"),
                            StringUtils.valueToPercentOrInteger(Math.abs(entry.getValue().value)),
                            StringUtils.spaceByCapitalization(key),
                            entry.getValue().source.toString()
                    );

                    button.setText(text);
                    buttonHeight = (int) button.getPreferredSize().getHeight();
                    button.setFocusPainted(false);
                    button.setBorderPainted(false);
                    button.setVisible(true);
                    modificationPanel.add(button);
                }
            }
            int paneWidth = (int) getPreferredSize().getWidth() / 2;
            int paneHeight = buttonHeight * modificationPanel.getComponentCount();
            modificationPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
            modCount = summary.getModificationCount();
            lastViewedUnit = currentUnit;
        }
    }
}
