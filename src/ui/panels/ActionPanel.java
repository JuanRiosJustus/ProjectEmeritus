package ui.panels;


import constants.ColorPalette;
import constants.GameStateKey;
import game.GameModel;
import game.components.MoveSet;
import game.components.Tile;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

public class ActionPanel extends JScene {
    private JKeyLabel nameField;
    private JKeyLabel typeField;
    private JKeyLabel damageField;
    private JKeyLabel accuracyField;
    private JKeyLabel areaOfEffectField;
    private JKeyLabel rangeField;
    private JKeyLabel energyCostField;
    private JKeyLabel healthCostField;
    private JTextArea descriptionField;
    private Entity observing;
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    private JPanel actionPanel;
    private ControlPanelSceneTemplate template;
    private GridBagConstraints constraints = new GridBagConstraints();
    private Ability selected = null;
    private JPanel descriptionPanel;

    public ActionPanel(int width, int height) {
        super(width, height, "Action");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        template = new ControlPanelSceneTemplate(width, (int) (height * .9), "ActionPanelTemplate");
        add(template);

        createTopRightPanel(template.topRight);

        createBottomHalfPanel(template.innerScrollPaneContainer);

        add(getExitButton());
    }

    private void createBottomHalfPanel(JPanel bottomHalfPanel) {
        descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new GridBagLayout());

        Dimension dimension = bottomHalfPanel.getPreferredSize();
        int rowHeight = (int) (dimension.getHeight() / 4);
        int rowWidth = (int) (dimension.getWidth() / 3);

        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        nameField = ComponentUtils.createFieldLabel("Name", "---");
        ComponentUtils.setSize(nameField, rowWidth, rowHeight);
        descriptionPanel.add(nameField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        damageField = ComponentUtils.createFieldLabel("Damage", "---");
        ComponentUtils.setSize(damageField, rowWidth, rowHeight);
        descriptionPanel.add(damageField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        typeField = ComponentUtils.createFieldLabel("Type", "---");
        ComponentUtils.setSize(typeField,  rowWidth, rowHeight);
        descriptionPanel.add(typeField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        accuracyField = ComponentUtils.createFieldLabel("Accuracy", "---");
        ComponentUtils.setSize(accuracyField,  rowWidth, rowHeight);
        descriptionPanel.add(accuracyField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        areaOfEffectField = ComponentUtils.createFieldLabel("Area", "---");
        ComponentUtils.setSize(areaOfEffectField,  rowWidth, rowHeight);
        descriptionPanel.add(areaOfEffectField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        rangeField = ComponentUtils.createFieldLabel("Range", "---");
        ComponentUtils.setSize(rangeField,  rowWidth, rowHeight);
        descriptionPanel.add(rangeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        healthCostField = ComponentUtils.createFieldLabel("Health Cost", "---");
        ComponentUtils.setSize(healthCostField,  rowWidth, rowHeight);
        descriptionPanel.add(healthCostField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        energyCostField = ComponentUtils.createFieldLabel("Energy Cost", "---");
        ComponentUtils.setSize(energyCostField,  rowWidth, rowHeight);
        descriptionPanel.add(energyCostField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = GridBagConstraints.REMAINDER;

        descriptionField = new JTextArea();
        descriptionField.setPreferredSize(new Dimension((int) (rowWidth * 1.5), rowHeight * 3));
        descriptionField.setLineWrap(true);
        descriptionField.setBackground(ColorPalette.TRANSPARENT);
        descriptionField.setEnabled(false);
        descriptionField.setDisabledTextColor(ColorPalette.TRANSPARENT);
        descriptionField.setFocusable(false);
        descriptionField.setEditable(false);
        descriptionField.setWrapStyleWord(true);
        descriptionPanel.add(descriptionField, constraints);

        descriptionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(descriptionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);

        bottomHalfPanel.add(descriptionPanel);
    }

    private void createTopRightPanel(JPanel topRightPanel) {
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridBagLayout());

        Dimension dimension = topRightPanel.getPreferredSize();

        constraints.gridx = 0;
        int actionCount = 0;
        for (int row = 0; row < 8; row++) {
            constraints.gridy = row;
            constraints.gridx = 0;
            constraints.weighty = 1;
            constraints.weightx = 1;
            JButton button1 = new JButton("Action " + actionCount++);
            button1.setPreferredSize(new Dimension((int) (dimension.getWidth() * .45), (int) (dimension.getHeight() / 2)));
            actionPanel.add(button1, constraints);

            constraints.gridx = 1;
            JButton button2 = new JButton("Action " + actionCount++);
            button2.setPreferredSize(new Dimension((int) (dimension.getWidth() * .45), (int) (dimension.getHeight() / 2)));
            actionPanel.add(button2, constraints);
        }

        JScrollPane scrollPane = new JScrollPane(actionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        ComponentUtils.setTransparent(scrollPane);
        Dimension dim = topRightPanel.getPreferredSize();
        scrollPane.getViewport().setPreferredSize(new Dimension((int) dim.getWidth(), (int) (dim.getHeight())));
//        scrollPane.getViewport().setPreferredSize(new Dimension(dim));
//        ComponentUtils.setSize(scrollPane, (int) dim.getWidth(), (int) dim.getHeight());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        topRightPanel.add(scrollPane);
    }

    public void set(GameModel model, Entity unit) {
        if (unit == null || observing == unit) { return; }
        Tile tile = unit.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        // Avoid multiple calls by checking if previously called
        if (observing == tile.unit) { return; }
        observing = tile.unit;
        selected = null;

        MoveSet moves = observing.get(MoveSet.class);

        template.selectionPanel.set(observing);

        ArrayList<Ability> abilities = (ArrayList<Ability>) moves.getCopy();
        for (int index = 0; index < actionPanel.getComponents().length; index++) {
            JButton button = (JButton) actionPanel.getComponents()[index];
            Ability ability = (abilities.size() > index ? abilities.get(index) : null);
            if (ability != null) {
                button.setVisible(true);
                button.setText(ability.name);
                button.setBorderPainted(true);
//                ComponentUtils.removeActionListeners(button);
                button.addActionListener(e -> {
                    nameField.setLabel(ability.name);
//                    damageField.setLabel(beautify(ability.healthDamage.base));
                    typeField.setLabel(ability.type.toString());
                    accuracyField.setLabel(beautify(ability.accuracy));
                    areaOfEffectField.setLabel(ability.area + "");
                    rangeField.setLabel(ability.range + "");
//                    healthCostField.setLabel(beautify(ability.healthCost.base));
//                    energyCostField.setLabel(beautify(ability.energyCost.base));
                    selected = ability;
                    model.state.set(GameStateKey.ACTION_PANEL_SELECTED_ACTION, ability);
                    //descriptionField.setText(ability.description);
                });
            } else {
                button.setText("");
                button.setBorderPainted(false);
            }
        }
        revalidate();
        repaint();

        logger.log("Updated condition panel for " + observing);
    }

    private static String beautify(double value) {
        if (value == 0) {
            return String.valueOf(0);
        } else if (value <= 1) {
            double percentage = value * 100;
            return String.format("%.0f%%", percentage);
        } else {
            return String.format("%.0f", value);
        }
    }

    public Ability getAbility() { return selected; }
}
