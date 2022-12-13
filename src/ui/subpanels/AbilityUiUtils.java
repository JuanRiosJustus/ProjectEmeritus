package ui.subpanels;

import game.stores.pools.ability.Ability;
import utils.ComponentUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.util.List;

public class AbilityUiUtils {

    private final static GridBagConstraints gbc = ComponentUtils.verticalGBC();

    public static void monitorAbilityRange(JPanel panel, List<Ability> source, StringBuilder result) {
        if (panel.getComponentCount() != source.size()) {
            panel.removeAll();
            for (Ability ability : source) {
                JButton button = ComponentUtils.createJButton(ability.name);
                button.addActionListener(e -> {
                    result.delete(0, result.length());
                    result.append(ability.range);
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
                    result.append(ability.range);
                });
            }
        }
    }

    public static void monitorAbilityName(JPanel panel, List<Ability> source, StringBuilder result) {
        if (panel.getComponentCount() != source.size()) {
            panel.removeAll();
            for (Ability ability : source) {
                JButton button = ComponentUtils.createJButton(ability.name);
                button.addActionListener(e -> {
                    result.delete(0, result.length());
                    result.append(ability.name);
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
                });
            }
        }
    }
}
