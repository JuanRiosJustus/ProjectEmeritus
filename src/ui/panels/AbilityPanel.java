package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.GameModel;
import game.components.MoveSet;
import game.components.Tile;
import game.entity.Entity;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import graphics.JScene;
import utils.ComponentUtils;
import graphics.temporary.JKeyValueLabel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

public class AbilityPanel extends JScene {

    private JKeyValueLabel nameField;
    private JTextArea descriptionField;
    private JKeyValueLabel typeField;
    private JKeyValueLabel damageField;
    private JKeyValueLabel accuracyField;
    private JKeyValueLabel areaOfEffectField;
    private JKeyValueLabel rangeField;
    private JKeyValueLabel energyCostField;
    private JKeyValueLabel healthCostField;
    private JPanel description;
    private String lastObservingAbility = null;
    private Entity lastObservingUnit = null;
    private final StringBuilder monitoring = new StringBuilder();
    private final JPanel abilitiesButtonsPanel;
    private final static GridBagConstraints gbc = ComponentUtils.verticalGBC();

    public AbilityPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT, "Combat");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new GridBagLayout());

        description = new JPanel();
        description.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        abilitiesButtonsPanel = new JPanel();
        abilitiesButtonsPanel.setLayout(new GridBagLayout());
        add(abilitiesButtonsPanel, gbc);

        nameField = ComponentUtils.createFieldLabel("Ability", "---");
        add(nameField, gbc);

        descriptionField = new JTextArea();
        descriptionField.setColumns(2 * descriptionField.getFont().getSize());
        descriptionField.setSize(getWidth() - 10,  3 *descriptionField.getFont().getSize());
        descriptionField.setLineWrap(true);
//        descriptionField.setRows(Constants.SIDE_BAR_MAIN_PANEL_HEIGHT / 2 / descriptionField.getFont().getSize());
        descriptionField.setBackground(ColorPalette.TRANSPARENT);
        descriptionField.setEnabled(false);
        descriptionField.setDisabledTextColor(Color.BLACK);
        descriptionField.setFocusable(false);
        descriptionField.setEditable(false);
        descriptionField.setWrapStyleWord(true);
//        textArea = ComponentUtils.createFieldLabel("Ability", "---");
        add(descriptionField, gbc);

        damageField = ComponentUtils.createFieldLabel("Damage", "---");
        add(damageField, gbc);

        typeField = ComponentUtils.createFieldLabel("Type", "---");
        add(typeField, gbc);

        accuracyField = ComponentUtils.createFieldLabel("Accuracy", "---");
        add(accuracyField, gbc);

        areaOfEffectField = ComponentUtils.createFieldLabel("Area of Effect", "---");
        add(areaOfEffectField, gbc);

        rangeField = ComponentUtils.createFieldLabel("Range", "---");
        add(rangeField, gbc);

        healthCostField = ComponentUtils.createFieldLabel("Health Cost", "---");
        add(healthCostField, gbc);

        energyCostField = ComponentUtils.createFieldLabel("Energy Cost", "---");
        add(energyCostField, gbc);

        add(getExitButton(), gbc);

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

    public void set(GameModel model, Entity unit) {
        unit = unit.get(Tile.class).unit;
        if (unit == null) { return; }

        show(monitoring.toString());

        if (lastObservingUnit == unit) { return; }
        lastObservingUnit = unit;

        List<Ability> abilities = lastObservingUnit.get(MoveSet.class).getCopy();
        monitorAbilityName(abilitiesButtonsPanel, model, abilities, monitoring);

        revalidate();
        repaint();
    }


    private void monitorAbilityName(JPanel panel, GameModel model, List<Ability> source, StringBuilder result) {
        if (panel.getComponentCount() != source.size()) {
            panel.removeAll();
            for (Ability ability : source) {
                JButton button = ComponentUtils.createJButton(ability.name);
                button.addActionListener(e -> {
                    result.delete(0, result.length());
                    result.append(ability.name);
                    model.state.set(GameStateKey.ACTION_UI_SELECTED_ACTION, ability.name);
                });
                panel.add(button, gbc);
            }
        } else {
            for (int index = 0; index < panel.getComponentCount(); index++) {
                JButton button = (JButton) panel.getComponent(index);
                Ability ability = source.get(index);
                button.setText(ability.name);
                button.addActionListener(e -> {
                    result.delete(0, result.length());
                    result.append(ability.name);
                    model.state.set(GameStateKey.ACTION_UI_SELECTED_ACTION, ability.name);
                });
            }
        }
    }

    public Ability getSelected() {
        return AbilityPool.instance().getAbility(lastObservingAbility);
    }

    public void show(String ability) {
        if (lastObservingAbility != null && lastObservingAbility.equals(ability)) { return; }
        lastObservingAbility = ability;
        Ability attack = AbilityPool.instance().getAbility(ability);
        if (attack == null) { return; }

        nameField.setLabel(attack.name);
        descriptionField.setText(attack.description);
        damageField.setLabel(beautify(attack.healthDamage.base));
        typeField.setLabel(attack.type.toString());
        accuracyField.setLabel(beautify(attack.accuracy));
        areaOfEffectField.setLabel(attack.area + "");
        rangeField.setLabel(attack.range + "");
        healthCostField.setLabel(beautify(attack.healthCost.base));
        energyCostField.setLabel(beautify(attack.energyCost.base));
    }
}
