package ui.panels;


import constants.GameStateKey;
import game.components.Abilities;
import game.components.ActionManager;
import game.components.Statistics;
import game.components.Tile;
import game.entity.Entity;
import game.main.GameModel;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import logging.ELogger;
import logging.ELoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;
import utils.MathUtils;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionPanel extends ControlPanelInnerTemplate {

    private Entity observing;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabel> nameToJKeyLabelnMap = new HashMap<>();
    private boolean shouldUpdate = true;

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

    public void set(GameModel model, Entity tileEntity) {
        if (tileEntity == null || observing == tileEntity) { return; }
        Tile tile = tileEntity.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        // Avoid multiple calls by checking if observed unit is already being inspected
        if (observing == tile.unit) { return; }
        // if (shouldUpdate == false) { return; }

        // shouldUpdate = false;

        reset();
        observing = tile.unit;
        topLeft.set(observing);

        List<Ability> abilities = observing.get(Abilities.class).getAbilities()
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
                    logger.info("Selected {} button while observing {}", button.getText(), observing.toString());

                    nameToJKeyLabelnMap.get("NAME").label.setText(ability.name);
                    nameToJKeyLabelnMap.get("HEALTH DAMAGE").label.setText((int)ability.getHealthDamage(observing) + "");
                    nameToJKeyLabelnMap.get("ENERGY DAMAGE").label.setText((int)ability.getEnergyDamage(observing) + "");
                    nameToJKeyLabelnMap.get("TYPE").label.setText(ability.type.toString());
                    nameToJKeyLabelnMap.get("ACCURACY").label.setText(MathUtils.floatToPercent(ability.accuracy) + "");
                    nameToJKeyLabelnMap.get("AREA").label.setText((int)ability.area + "");
                    nameToJKeyLabelnMap.get("RANGE").label.setText((int)ability.range + "");
                    nameToJKeyLabelnMap.get("HEALTH COST").label.setText((int)ability.getHealthCost(observing) + "");
                    nameToJKeyLabelnMap.get("ENERGY COST").label.setText((int)ability.getEnergyCost(observing) + "");

                    ActionManager am = observing.get(ActionManager.class);
                    if (am.acted) { return; }
                    lastToggledButton = currentlyToggledButton;
                    currentlyToggledButton = button;
                    // if (button == null || button.getName() == null) { return; }
                    logger.debug("{} is selected", button.getName());
                    model.state.set(GameStateKey.ACTION_PANEL_SELECTED_ACTION, ability);
                });
            } else {
                button.setText("");
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            }
        }

        logger.info("Updated condition panel for " + observing);
    }

    private void reset() {
        for (int index = 0; index < actionPanel.getComponents().length; index++) { 
            JButton button = (JButton) actionPanel.getComponents()[index];
            button.setBorderPainted(true);
            button.setFocusPainted(true);
            button.setSelected(false);
        }

        observing = null;

        nameToJKeyLabelnMap.get("NAME").label.setText("");
        nameToJKeyLabelnMap.get("HEALTH DAMAGE").label.setText("");
        nameToJKeyLabelnMap.get("ENERGY DAMAGE").label.setText("");
        nameToJKeyLabelnMap.get("TYPE").label.setText("");
        nameToJKeyLabelnMap.get("ACCURACY").label.setText("");
        nameToJKeyLabelnMap.get("AREA").label.setText("");
        nameToJKeyLabelnMap.get("RANGE").label.setText("");
        nameToJKeyLabelnMap.get("HEALTH COST").label.setText("");
        nameToJKeyLabelnMap.get("ENERGY COST").label.setText("");

        lastToggledButton = currentlyToggledButton;
        currentlyToggledButton = null;
        if (lastToggledButton != null) { lastToggledButton.setSelected(false); }
    }

    public void setUI(GameModel model, Entity entity) {
        reset();
    }
}
