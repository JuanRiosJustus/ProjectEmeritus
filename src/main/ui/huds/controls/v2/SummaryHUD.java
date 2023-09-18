package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.game.components.*;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.Resource;
import main.game.stats.Stat;
import main.game.stats.Modification;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.*;
import main.ui.huds.controls.HUD;
import main.ui.panels.Accordion;
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
    private JKeyValue typeField;
    private JKeyValue nameField;
    private JKeyValue experienceField;
    private JKeyProgress healthProgress;
    private JKeyProgress manaProgress;
    private JKeyProgress staminaProgress;
    private boolean initialized = false;
    private final JKeyValueMap combatStatPane;
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
        selection = new ImagePanel(width, (int) (height * .25));
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(ColorPalette.TRANSPARENT);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension((int) (height * .2), (int) (height * .25)));
        panel.add(selection);
        add(panel, constraints);

        // resources
        constraints.gridy = 1;
        JScrollPane pane = createScrollingResourcePane(width, (int) (height * .2));
        add(pane, constraints);

//        Accordion accordion = new Accordion();
//
//        accordion.addBar("Resources", pane);
//        add(accordion, constraints);

//        // mods and tags
//        JScrollPane modsAndTagsPane = createModsAndTagsPanel(width, (int) (height * .05));
//        constraints.gridy = 2;
//        add(modsAndTagsPane, constraints);

        // raw stats
        constraints.gridy = 2;
        combatStatPane = new JKeyValueMap(
                width,
                (int) (height * .5),
                new String[]{
                        Constants.HEALTH, Statistics.MANA,
                        Statistics.STAMINA,
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

        // button
        constraints.gridy = 3;
        getExitButton().setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(getExitButton(), constraints);
    }

    protected JScrollPane createScrollingResourcePane(int width, int height) {

        int fieldHeight = height / 7;
        typeField = new JKeyValue(width, fieldHeight, "Type");
        nameField = new JKeyValue(width, fieldHeight, "Name");
        experienceField = new JKeyValue(width, fieldHeight, "Experience");

        JKeyValue healthField = new JKeyValue((int) (width * .25), fieldHeight, "Health");
        healthProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Health");
//        healthProgress.getKey().setText("~");

        JKeyValue manaField = new JKeyValue((int) (width * .25), fieldHeight, "Mana");
        manaProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Mana");
//        energyProgress.getKey().setText("~");

        JKeyValue levelField = new JKeyValue((int) (width * .25), fieldHeight, "Stamina");
        staminaProgress = new JKeyProgress((int) (width * .75), fieldHeight, "Stamina");
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
        row2.add(manaField, gbc);
        gbc.weightx = .75;
        gbc.gridx = 1;
        row2.add(manaProgress, gbc);
        row2.setPreferredSize(new Dimension(new Dimension(width, fieldHeight)));

        // Create level field
        JPanel row3 = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = .25;
        row3.add(levelField, gbc);
        gbc.weightx = .75;
        gbc.gridx = 1;
        row3.add(staminaProgress, gbc);
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

    private JScrollPane createModsAndTagsPanel(int width, int height) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
//        content.setPreferredSize(new Dimension(width, height));

        content.add(tagPanel);
        content.add(modificationPanel);

        return createScalingPane(width, height, content);
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
//        // TODO cacheing on this object or mak observable... so many ui calls
        if (gameModel == null) { return; }
        if (currentUnit == null) { return; }

        Statistics statistics = currentUnit.get(Statistics.class);
        selection.set(currentUnit);

        if (!initialized) {
            updateUi(statistics, true);
            initialized = true;
        } else {
            updateUi(statistics, false);
        }
    }

    private void updateUi(Statistics statistics, boolean forceUpdate) {
        String temp = currentUnit.get(Identity.class).toString() + " (" + statistics.getSpecies() + ")";
        if (!nameField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            nameField.setValue(temp);
        }

        temp = currentUnit.get(Types.class).getTypes().toString();
        if (!typeField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            typeField.setValue(temp);
        }

        Resource health = statistics.getResourceNode(Statistics.HEALTH);
        int percentage = (int) MathUtils.map(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgress.getValue() != percentage || !forceUpdate) {
            healthProgress.setValue(percentage);
            healthProgress.setKey(String.valueOf(health.getCurrent()));
        }

        Resource energy = statistics.getResourceNode(Statistics.MANA);
        percentage = (int) MathUtils.map(energy.getPercentage(), 0, 1, 0, 100);
        if (manaProgress.getValue() != percentage || !forceUpdate) {
            manaProgress.setValue(percentage);
            manaProgress.setKey(String.valueOf(energy.getCurrent()));
        }

        Resource stamina = statistics.getResourceNode(Statistics.STAMINA);
        float percent = (float)stamina.getCurrent()/ (float)stamina.getTotal();
        percentage = (int) MathUtils.map(percent, 0, 1, 0, 100);
        if (staminaProgress.getValue() != percentage || !forceUpdate) {
            staminaProgress.setValue(percentage);
            staminaProgress.setKey(String.valueOf(stamina.getCurrent()));
//            Stat level = statistics.getStatsNode(Statistics.LEVEL);
//            staminaProgress.setKey(String.valueOf(level.getTotal()));
//            combatStatPane.get(Constants.LEVEL).setValue(level.getTotal() + "");
        }
        temp = stamina.getCurrent() + " / " + stamina.getTotal();
        if (!experienceField.getValue().equalsIgnoreCase(temp) || !forceUpdate) {
            experienceField.setValue(temp);
        }

        int buttonHeight = 0;

        Tags tags = currentUnit.get(Tags.class);
        if (tags.getTagMap().size() != tagPanel.getComponentCount() || !forceUpdate) {
            // to status panel?
            tagPanel.removeAll();
            for (Map.Entry<String, Object> entry : tags.getTagMap().entrySet()) {
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

        if (modCount != statistics.getModificationCount() || lastViewedUnit != currentUnit || !forceUpdate) {
            modificationPanel.removeAll();
            for (String key : statistics.getStatNodeNames()) {
                if (key.equalsIgnoreCase(Statistics.EXPERIENCE)) { continue; }
                if (key.equalsIgnoreCase(Statistics.LEVEL)) { continue; }
                Stat node = statistics.getStatsNode(key);
                boolean isPositive = node.getModified() > 0;
                boolean isNegative = node.getModified() < 0;
//                temp = node.getBase() +"";
//                if (isPositive) {
//                    temp += " ( " + ColorPalette.getHtmlColor((node.getModified() > 0 ? "+" : "") + node.getModified(), ColorPalette.HEX_CODE_RED) + " )";
//                } else if (isNegative) {
//                    temp += " ( " + ColorPalette.getHtmlColor((node.getModified() > 0 ? "+" : "") + node.getModified(), ColorPalette.HEX_CODE_GREEN) + " )";
//                } else {
//                    temp = node.getBase() + " ( " + (node.getModified() > 0 ? "+" : "") + node.getModified() + " )";
//                }
                temp = node.getBase() + " ( " + (node.getModified() > 0 ? "+" : "") + node.getModified() + " )";
//                temp = node.getBase() + " ( " + (node.getModified() > 0 ? "+" : "") + node.getModified() + " )";
                if (!combatStatPane.get(node.getName()).getValue().equalsIgnoreCase(temp)) {
                    combatStatPane.get(node.getName()).setValue(temp);
                    if (isNegative) {
                        combatStatPane.get(node.getName()).setValueColor(ColorPalette.DARK_RED_V1);
                    } else if (isPositive) {
                        combatStatPane.get(node.getName()).setValueColor(ColorPalette.DARK_GREEN_V1);
                    } else {
                        combatStatPane.get(node.getName()).setValueColor(ColorPalette.BLACK);
                    }
                }

                for (Map.Entry<Object, Modification> entry : node.getModifications().entrySet()) {
                    JButton button = new JButton();
                    String text = StringFormatter.format(
                            "{}{} {} from {}",
                            (entry.getValue().getValue() > 0 ? "+" : "-"),
                            StringUtils.valueToPercentOrInteger(Math.abs(entry.getValue().getValue())),
                            StringUtils.spaceByCapitalization(key),
                            entry.getValue().getSource().toString()
                    );

                    button.setText("<html>" + text + "</html>");
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
            modCount = statistics.getModificationCount();
            lastViewedUnit = currentUnit;
        }
    }
}
