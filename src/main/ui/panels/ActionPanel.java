package main.ui.panels;


import main.game.components.Abilities;
import main.game.components.ActionManager;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabel;
import main.ui.GameState;
import main.utils.ComponentUtils;
import main.utils.MathUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionPanel extends ControlPanelPane {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabel> nameToJKeyLabelnMap = new HashMap<>();

    public ActionPanel(int width, int height) {
        super(width, height, ActionPanel.class.getSimpleName());

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
    }

    protected JScrollPane createTopRightPanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(
            (int)reference.getPreferredSize().getWidth(), 
            (int)(reference.getPreferredSize().getHeight() * 1)
        ));
        // result.setPreferredSize(reference.getPreferredSize());
        int rows = 5;
        int columns = 2;
        result.setLayout(new GridLayout(rows, columns));

        String[][] nameAndToolTips = new String[][]{
            new String[]{ "NAME", "What the unit is referenced as" },
            new String[]{ "TYPE", "The elements associated by the unit"},
            new String[]{ "ACCURACY", "Chance the ability will land successfully" },

            new String[]{ "AREA", "Surrounding impacted tiles from the target"},
            new String[]{ "RANGE", "How far the ability can be used from"},
            new String[]{ "HEALTH COST", "Health Cost to use the ability"},

            new String[]{ "ENERGY COST", "Energy cost to use the ability"},
            new String[]{ "HEALTH DAMAGE", "Health Damage that can be caused by the ability"},
            new String[]{ "ENERGY DAMAGE", "Energy Damage that can be caused by the ability"},
            new String[]{ "ENERGY DAMAGE", "Energy Damage that can be caused by the ability"}
        };

        double height = reference.getPreferredSize().getHeight();
        double width = reference.getPreferredSize().getWidth();
        Dimension buttonDimension = new Dimension((int) (width / columns), (int) (height / rows));

        for (String[] nameAndToolTip : nameAndToolTips) {
            JKeyLabel kl = new JKeyLabel(nameAndToolTip[0] + ":", "");
            kl.key.setFont(kl.key.getFont().deriveFont(Font.BOLD));
            kl.setPreferredSize(buttonDimension);
            kl.setToolTipText(nameAndToolTip[1]);
            nameToJKeyLabelnMap.put(nameAndToolTip[0], kl);
            result.add(kl);
        }

        JScrollPane scrollPane = new JScrollPane(
            result,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }


    protected JScrollPane createMiddlePanel(JComponent reference) {

        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 3));
        actionPanel.setPreferredSize(reference.getPreferredSize());

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFocusPainted(false);
            actionPanel.add(button);

            // JButton button = new JButton(String.valueOf(i));
            // button.setFocusPainted(false);
            // actionPanel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(actionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private void reset() {
        for (int index = 0; index < actionPanel.getComponents().length; index++) { 
            JButton button = (JButton) actionPanel.getComponents()[index];
            button.setBorderPainted(true);
            button.setFocusPainted(true);
            button.setSelected(false);
        }

        unit = null;

        nameToJKeyLabelnMap.get("NAME").value.setText("");
        nameToJKeyLabelnMap.get("HEALTH DAMAGE").value.setText("");
        nameToJKeyLabelnMap.get("ENERGY DAMAGE").value.setText("");
        nameToJKeyLabelnMap.get("TYPE").value.setText("");
        nameToJKeyLabelnMap.get("ACCURACY").value.setText("");
        nameToJKeyLabelnMap.get("AREA").value.setText("");
        nameToJKeyLabelnMap.get("RANGE").value.setText("");
        nameToJKeyLabelnMap.get("HEALTH COST").value.setText("");
        nameToJKeyLabelnMap.get("ENERGY COST").value.setText("");

        lastToggledButton = currentlyToggledButton;
        currentlyToggledButton = null;
        if (lastToggledButton != null) { lastToggledButton.setSelected(false); }
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (unit == null) { return; }
        if (tile == null) { return; }
        topLeft.set(unit);

        List<Ability> abilities = unit.get(Abilities.class).getAbilities()
                .stream().map(e -> AbilityPool.getInstance().getAbility(e)).toList();
        for (int index = 0; index < actionPanel.getComponents().length; index++) {
            JButton button = (JButton) actionPanel.getComponents()[index];
            Ability ability = (abilities.size() > index ? abilities.get(index) : null);
            if (ability != null) {
                button.setText(ability.name);
                button.setName(ability.name);
                button.setBorderPainted(true);
                button.setFocusPainted(true);
                ComponentUtils.removeActionListeners(button);
                button.addActionListener(e -> {
                    Entity observing = unit;
                    logger.info("Selected {} button while observing {}", button.getText(), observing.toString());

                    nameToJKeyLabelnMap.get("NAME").value.setText(ability.name);
                    nameToJKeyLabelnMap.get("HEALTH DAMAGE").value.setText((int)ability.getHealthDamage(observing) + "");
                    nameToJKeyLabelnMap.get("ENERGY DAMAGE").value.setText((int)ability.getEnergyDamage(observing) + "");
                    nameToJKeyLabelnMap.get("TYPE").value.setText(ability.getTypes().toString());
                    nameToJKeyLabelnMap.get("ACCURACY").value.setText(MathUtils.floatToPercent(ability.accuracy) + "");
                    nameToJKeyLabelnMap.get("AREA").value.setText((int)ability.area + "");
                    nameToJKeyLabelnMap.get("RANGE").value.setText((int)ability.range + "");
                    nameToJKeyLabelnMap.get("HEALTH COST").value.setText((int)ability.getHealthCost(observing) + "");
                    nameToJKeyLabelnMap.get("ENERGY COST").value.setText((int)ability.getEnergyCost(observing) + "");

                    ActionManager am = unit.get(ActionManager.class);
                    if (am.acted) { return; }
                    lastToggledButton = currentlyToggledButton;
                    currentlyToggledButton = button;
                    // if (button == null || button.getName() == null) { return; }
                    logger.debug("{} is selected", button.getName());
                    gameModel.gameState.set(GameState.ACTION_PANEL_SELECTED_ACTION, ability);
                });
            } else {
                button.setText("");
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            }
        }

//        logger.info("Updated condition panel for " + unit);
    }
}
