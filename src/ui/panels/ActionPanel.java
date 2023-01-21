package ui.panels;


import constants.ColorPalette;
import constants.GameStateKey;
import game.GameModel;
import game.components.MoveSet;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import graphics.temporary.JKeyValueLabel;
import utils.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActionPanel extends JScene {
    private JKeyValueLabel nameField;
    private JKeyValueLabel typeField;
    private JKeyValueLabel damageField;
    private JKeyValueLabel accuracyField;
    private JKeyValueLabel areaOfEffectField;
    private JKeyValueLabel rangeField;
    private JKeyValueLabel energyCostField;
    private JKeyValueLabel healthCostField;
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

        createBottomHalfPanel(template.bottomHalf);

        add(getExitButton());
    }

    private JScrollPane createBottomHalfPanel(JPanel reference) {
        descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new GridBagLayout());

        int rowHeight = reference.getHeight() / 6;

        constraints.gridx = 0;
        constraints.gridy = 0;

        nameField = ComponentUtils.createFieldLabel("Name", "---");
        nameField.setBackground(ColorPalette.RED);
        ComponentUtils.setSize(nameField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(nameField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        damageField = ComponentUtils.createFieldLabel("Damage", "---");
        ComponentUtils.setSize(damageField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(damageField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        typeField = ComponentUtils.createFieldLabel("Type", "---");
        ComponentUtils.setSize(typeField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(typeField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        accuracyField = ComponentUtils.createFieldLabel("Accuracy", "---");
        ComponentUtils.setSize(accuracyField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(accuracyField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        areaOfEffectField = ComponentUtils.createFieldLabel("Area", "---");
        ComponentUtils.setSize(areaOfEffectField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(areaOfEffectField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        rangeField = ComponentUtils.createFieldLabel("Range", "---");
        ComponentUtils.setSize(rangeField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(rangeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        healthCostField = ComponentUtils.createFieldLabel("Health Cost", "---");
        ComponentUtils.setSize(healthCostField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(healthCostField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        energyCostField = ComponentUtils.createFieldLabel("Energy Cost", "---");
        ComponentUtils.setSize(energyCostField, reference.getWidth() / 3, rowHeight);
        descriptionPanel.add(energyCostField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = GridBagConstraints.REMAINDER;


        descriptionField = new JTextArea();
        descriptionField.setSize(reference.getWidth(), rowHeight * 3);
        descriptionField.setLineWrap(true);
        descriptionField.setBackground(ColorPalette.TRANSPARENT);
        descriptionField.setEnabled(false);
        descriptionField.setDisabledTextColor(Color.BLACK);
        descriptionField.setFocusable(false);
        descriptionField.setEditable(false);
        descriptionField.setWrapStyleWord(true);
        descriptionPanel.add(descriptionField, constraints);

        descriptionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(descriptionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, reference.getWidth(), reference.getHeight());

        reference.add(scrollPane);
        return scrollPane;
    }

    private JPanel createTopRightPanel(JPanel reference) {
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridBagLayout());

        constraints.gridx = 0;
        for (int row = 0; row < 8; row++) {
            constraints.gridy = row;
            actionPanel.add(new JButton("Action " + (row + 1)), constraints);
        }

        JScrollPane scrollPane = new JScrollPane(actionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setSize(scrollPane, template.bottomHalf.getWidth(), template.bottomHalf.getHeight());

        reference.add(scrollPane);
        return actionPanel;
    }

    public void set(GameModel model, Entity unit) {
        if (unit == null || observing == unit) { return; }
        observing = unit;
        selected = null;

        MoveSet moves = unit.get(MoveSet.class);

        template.selectionPanel.set(unit);

        actionPanel.removeAll();
        model.state.set(GameStateKey.ACTION_UI_SELECTED_ACTION, null);
        descriptionPanel.setVisible(false);

        constraints.gridy = 0;
        constraints.gridx = 0;
        for (Ability ability : moves.getCopy()) {
            JButton button = new JButton(ability.name);
            actionPanel.add(button, constraints);
            button.addActionListener(e -> {
                descriptionPanel.setVisible(true);
                nameField.setLabel(ability.name);
                descriptionField.setText(ability.description);
                damageField.setLabel(beautify(ability.healthDamage.base));
                typeField.setLabel(ability.type.toString());
                accuracyField.setLabel(beautify(ability.accuracy));
                areaOfEffectField.setLabel(ability.area + "");
                rangeField.setLabel(ability.range + "");
                healthCostField.setLabel(beautify(ability.healthCost.base));
                energyCostField.setLabel(beautify(ability.energyCost.base));
                selected = ability;
                model.state.set(GameStateKey.ACTION_UI_SELECTED_ACTION, ability);
                revalidate();
                repaint();
            });
            constraints.gridy++;
        }

        revalidate();
        repaint();
        logger.log("Updated condition panel for " + unit);
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
