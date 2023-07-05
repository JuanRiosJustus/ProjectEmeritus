package ui.panels;


import constants.GameStateKey;
import game.components.Tile;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import logging.ELogger;
import logging.ELoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.MathUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    private Ability selected = null;
    private final Map<String, JKeyLabel> nameToJKeyLabelnMap = new HashMap<>();

    public ActionPanel(int width, int height) {
        super(width, (int) (height * .9), "Action");

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
    }

    private JScrollPane createTopRightPanel(JPanel reference) {
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
            new String[]{ "ACC", "Chance the ability will land successfully" },

            new String[]{ "AREA", "Surrounding impacted tiles from the target"},
            new String[]{ "RANGE", "How far the ability can be used from"},
            new String[]{ "HP COST", "Health Cost to use the ability"},

            new String[]{ "NRG COST", "Energy cost to use the ability"},
            new String[]{ "HP DMG", "Health Damage that can be caused by the ability"},
            new String[]{ "NRG DMG", "Energy Damage that can be caused by the ability"}
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

        return scrollPane;
    }


    private JScrollPane createMiddlePanel(JPanel reference) {

        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 3));
        actionPanel.setPreferredSize(reference.getPreferredSize());

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton(String.valueOf(i));
            // button.setBorderPainted(false);
            button.setFocusPainted(false);
            actionPanel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(actionPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        return scrollPane;
    }

    public void set(GameModel model, Entity unit) {
        if (unit == null || observing == unit) { return; }
        Tile tile = unit.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        // Avoid multiple calls by checking if previously called
        if (observing == tile.unit) { return; }
        observing = tile.unit;
        selected = null;

        topLeft.set(observing);

        List<Ability> abilities = observing.get(Summary.class).getAbilities()
            .stream().map(e -> AbilityPool.getInstance().getAbility(e)).toList();
        for (int index = 0; index < actionPanel.getComponents().length; index++) {
            JButton button = (JButton) actionPanel.getComponents()[index];
            Ability ability = (abilities.size() > index ? abilities.get(index) : null);
            if (ability != null) {
                button.setVisible(true);
                button.setText(ability.name);
                button.setBorderPainted(true);
//                ComponentUtils.removeActionListeners(button);
                button.addActionListener(e -> {
                    
                    // labelMap.
                    nameToJKeyLabelnMap.get("NAME").label.setText(ability.name);
                    nameToJKeyLabelnMap.get("HP DMG").label.setText((int)ability.getHealthDamage(observing) + "");
                    nameToJKeyLabelnMap.get("NRG DMG").label.setText((int)ability.getEnergyDamage(observing) + "");
                    nameToJKeyLabelnMap.get("TYPE").label.setText(ability.type.toString());
                    nameToJKeyLabelnMap.get("ACC").label.setText(MathUtils.floatToPercent(ability.accuracy) + "");
                    nameToJKeyLabelnMap.get("AREA").label.setText((int)ability.area + "");
                    nameToJKeyLabelnMap.get("RANGE").label.setText((int)ability.range + "");
                    nameToJKeyLabelnMap.get("HP COST").label.setText((int)ability.getHealthCost(observing) + "");
                    nameToJKeyLabelnMap.get("NRG COST").label.setText((int)ability.getEnergyCost(observing) + "");

                    selected = ability;
                    model.state.set(GameStateKey.ACTION_PANEL_SELECTED_ACTION, ability);
                });
            } else {
                button.setText("");
                // button.setBorderPainted(false);
            }
        }
        revalidate();
        repaint();

        logger.info("Updated condition panel for " + observing);
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
