package main.ui.huds.controls.v1;

import main.game.components.*;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stats.node.StatsNode;
import main.game.stats.node.StatsNodeModification;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabelOld;
import main.ui.panels.ControlPanelPane;
import main.ui.panels.StatScrollPane;
import main.utils.ComponentUtils;
import main.utils.MathUtils;
import main.utils.StringFormatter;
import main.utils.StringUtils;

import javax.swing.*;

import main.constants.Constants;

import java.awt.*;
import java.awt.Dimension;
import java.util.Map;


public class MiniSummaryHUD extends ControlPanelPane {

    private JKeyLabelOld nameFieldLabel;
    // private JKeyLabel statusFieldLabel;
    private JKeyLabelOld typeFieldLabel;
    private JKeyLabelOld healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabelOld energyFieldLabel;
    private JProgressBar energyProgressBar;
    private JKeyLabelOld levelFieldLabel;
    private JProgressBar experienceProgressBar;
    private JPanel tagPanel;
    private JPanel modificationPanel;
    private SecondTimer timer = new SecondTimer();
    private final int MINIMUM_MIDDLE_PANEL_ITEM_HEIGHT = 150;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastViewedUnit = null;
    public int modCount = 0;

    private final StatScrollPane combatStatPane;
    public MiniSummaryHUD(int width, int height) {
        super(width, height, "Mini Summary");

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        combatStatPane = createMiddlePanel(middle);
        middle.add(combatStatPane);
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

        nameFieldLabel = new JKeyLabelOld("", ""); //ComponentUtils.createFieldLabel("","[Name Field]");
        nameFieldLabel.value.setOpaque(false);
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = new JKeyLabelOld("", "");// ComponentUtils.createFieldLabel("", "[Types Field]");
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
        healthFieldLabel = new JKeyLabelOld("Health: ", "DEFAULT");
        healthFieldLabel.value.setOpaque(false);
        healthFieldLabel.setValue("");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = new JKeyLabelOld("Energy: ", "DEFAULT");
        energyFieldLabel.value.setOpaque(false);
        energyFieldLabel.setValue("");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        levelFieldLabel = new JKeyLabelOld("Lvl: ", "DEFAULT");
        levelFieldLabel.value.setOpaque(false);
        ComponentUtils.setTransparent(levelFieldLabel);
        levelFieldLabel.setValue("");
        levelFieldLabel.key.setFont(levelFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row3.add(levelFieldLabel);
        experienceProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        experienceProgressBar.setValue(0);
        row3.add(experienceProgressBar);
        row3.setPreferredSize(new Dimension(width, rowHeight));


        tagPanel = new JPanel();
        tagPanel.setLayout(new GridLayout(0, 1));
//        tagPanel.setPreferredSize(reference.getPreferredSize());

        modificationPanel = new JPanel();
        modificationPanel.setLayout(new GridLayout(0, 1));
//        modificationPanel.setPreferredSize(reference.getPreferredSize());

        content.add(row0);
        content.add(row1);
        content.add(row2);
        content.add(row3);
        content.add(tagPanel);
        content.add(modificationPanel);
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

        Summary summary = currentUnit.get(Summary.class);
        topLeft.set(currentUnit);
        String temp;

        temp = currentUnit.get(Identity.class).toString() + " (" + summary.getSpecies() + ")";
        if (!nameFieldLabel.value.getText().equalsIgnoreCase(temp)) {
            nameFieldLabel.value.setText(temp);
        }

        temp = currentUnit.get(Types.class).getTypes().toString();
        if (!typeFieldLabel.value.getText().equalsIgnoreCase(temp)) {
            typeFieldLabel.value.setText(temp);
        }

        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.map(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setValue(String.valueOf(health.getCurrent()));
        }

        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.map(energy.getPercentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setValue(String.valueOf(energy.getCurrent()));
        }

        StatsNode level = summary.getStatsNode(Constants.LEVEL);
        ResourceNode current = summary.getResourceNode(Constants.EXPERIENCE);
        float percent = (float)current.getCurrent()/ (float)current.getTotal();
        percentage = (int) MathUtils.map(percent, 0, 1, 0, 100);
        boolean noLevelLabel = levelFieldLabel.value.getText().isBlank();
        if (experienceProgressBar.getValue() != percentage || noLevelLabel) {
            experienceProgressBar.setValue(percentage);
            levelFieldLabel.setValue(String.valueOf(level.getTotal()));
            combatStatPane.get(Constants.LEVEL).setText(level.getTotal() + "");
        }

        int buttonHeight = 0;

        Tags tags = currentUnit.get(Tags.class);
        if (tags.getTagMap().size() != tagPanel.getComponentCount()) {
            // to status panel?
            tagPanel.removeAll();
            for (Map.Entry<String, Object> entry : tags.getTagMap().entrySet()) {
                // Create of button if not available, else use existing
                JButton buttonTag = new JButton();

                buttonHeight = (int) buttonTag.getPreferredSize().getHeight();
                buttonTag.setBorderPainted(false);
                buttonTag.setFocusPainted(false);
//                buttonTag.setOpaque(true);
                buttonTag.setVisible(true);
//                buttonTag.setBackground(ColorPalette.getRandomColor());
                buttonTag.setText(entry.getKey() + " from " + entry.getValue());

                tagPanel.add(buttonTag);
            }
            int paneWidth = (int) topRight.getPreferredSize().getWidth() / 2;
            int paneHeight = buttonHeight * tagPanel.getComponentCount();
            tagPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
        }

        if (modCount != summary.getModificationCount() || lastViewedUnit != currentUnit) {
            modificationPanel.removeAll();
            for (String key : summary.getStatNodeNames()) {
                if (key.equalsIgnoreCase(Constants.EXPERIENCE)) { continue; }
                if (key.equalsIgnoreCase(Constants.LEVEL)) { continue; }
//            for (String key : combatStatPane.getKeySet()) {
                StatsNode node = summary.getStatsNode(key);
                temp = node.getBase() + " ( " + (node.getModified() > 0 ? "+" : "") + node.getModified() + " )";
                if (!combatStatPane.get(node.getName()).getText().equalsIgnoreCase(temp)) {
                    combatStatPane.get(node.getName()).setText(temp);
                }

                for (Map.Entry<Object, StatsNodeModification> entry : node.getModifications().entrySet()) {
                    JButton button = new JButton();
                    String text = StringFormatter.format(
                            "{} {} by {} from {}",
                            StringUtils.spaceByCapitalization(key),
                            (entry.getValue().value > 0 ? "increased" : "decreased"),
                            StringUtils.valueToPercentOrInteger(Math.abs(entry.getValue().value)),
                            entry.getValue().source.toString()
                    );

                    button.setText(text);
                    buttonHeight = (int) button.getPreferredSize().getHeight();
                    button.setFocusPainted(false);
                    button.setBorderPainted(false);
//                    button.setOpaque(true);
//                    button.setBackground(ColorPalette.getRandomColor());
                    button.setVisible(true);
                    modificationPanel.add(button);
                }
            }
            int paneWidth = (int) middle.getPreferredSize().getWidth() / 2;
            int paneHeight = buttonHeight * modificationPanel.getComponentCount();
            modificationPanel.setPreferredSize(new Dimension(paneWidth, paneHeight));
            modCount = summary.getModificationCount();
            lastViewedUnit = currentUnit;
        }
    }
}
