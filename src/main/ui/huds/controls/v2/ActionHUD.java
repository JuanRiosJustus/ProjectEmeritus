package main.ui.huds.controls.v2;


import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.Abilities;
import main.game.components.Action;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.graphics.temporary.JKeyLabelOld;
import main.ui.huds.controls.HUD;
import main.ui.panels.ImagePanel;
import main.ui.custom.JKeyLabelArray;
import main.utils.ComponentUtils;
import main.utils.MathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActionHUD extends HUD {
    private JPanel actionPanel;
    private JTextArea description;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabelOld> labelMap = new HashMap<>();
    private final JKeyLabelArray scrollPane;

    public ActionHUD(int width, int height) {
        super(width, height, "Actions");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
//        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        // Image
        selection = new ImagePanel((int) (height * .2), (int) (height * .2));
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(selection.getPreferredSize());
        panel.add(selection);
        add(panel, constraints);

        constraints.gridy = 1;
        scrollPane =  new JKeyLabelArray(
                width,
                (int) (height * .35),
                new String[]{
                        Constants.NAME, Constants.TYPE,
                        Constants.ACC, Constants.IMPACT,
                        Constants.RANGE, Constants.AREA,
                        Constants.HP_COST, Constants.EP_COST,
                        Constants.HP_DAMAGE, Constants.EP_DAMAGE
                }
        );
        add(scrollPane, constraints);


        constraints.gridy = 2;
        description = new JTextArea();
        description.setPreferredSize(new Dimension(width, (int) (height * .1)));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(5, 5, 5, 5));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        add(description, constraints);

        constraints.gridy = 3;
        JScrollPane pane = createButtonPane(width, (int) (height * .3));
        add(pane, constraints);
//        setBackground(ColorPalette.BLACK);
//
        constraints.gridy = 4;
        getExitButton().setPreferredSize(new Dimension(width, (int) (height * .05)));
        add(getExitButton(), constraints);
    }

    protected JScrollPane createButtonPane(int width, int height) {
        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 2));
        actionPanel.setBackground(ColorPalette.getRandomColor());


        int buttons = 12;
        for (int i = 0; i < buttons; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setFocusPainted(false);
            actionPanel.add(button);
        }

        return createScalingPane(width, height, actionPanel);
    }
    @Override
    public void jSceneUpdate(GameModel model) {
        if (currentUnit == null) { return; }

        selection.set(currentUnit);

        List<Ability> abilities = currentUnit.get(Abilities.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().get(e))
                .toList();

        if (actionPanel == null) { return; }
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
                    // Get the currently observed unit from the HUD
                    Entity observing = currentUnit;
                    if (observing == null) { return; }
                    logger.info("Selected {} button while observing {}", button.getText(), observing.toString());
                    // Check that the current unit has the selected ability
                    Set<String> observingAbilities = observing.get(Abilities.class).getAbilities();
                    if (!observingAbilities.contains(ability.name)) { return; }
                    // Setup the UI to show the current ability's information
                    Summary summary = observing.get(Summary.class);
                    scrollPane.get(Constants.NAME).setLabel(ability.name);
                    scrollPane.get(Constants.IMPACT).setLabel(ability.impact);
                    int temp = (int) ability.getHealthDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_DAMAGE).setLabel(String.valueOf(temp));
                    } else { scrollPane.get(Constants.HP_DAMAGE).setLabel(""); }

                    temp = (int) ability.getEnergyDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_DAMAGE).setLabel(String.valueOf(temp));
                    } else { scrollPane.get(Constants.EP_DAMAGE).setLabel(""); }

                    scrollPane.get(Constants.TYPE).setLabel(ability.getTypes().toString());
                    scrollPane.get(Constants.ACC).setLabel(MathUtils.floatToPercent(ability.accuracy));

                    temp = ability.getHealthCost(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_COST).setLabel(temp + " / " +
                                summary.getStatCurrent(Constants.HEALTH));
                    } else { scrollPane.get(Constants.HP_COST).setLabel(""); }

                    temp = ability.getEnergyCost(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_COST).setLabel(temp + " / " +
                                summary.getStatCurrent(Constants.ENERGY));
                    } else { scrollPane.get(Constants.EP_COST).setLabel(""); }

                    scrollPane.get(Constants.AREA).setLabel(ability.area + "");
                    scrollPane.get(Constants.RANGE).setLabel(ability.range + "");
                    description.setText(ability.description);

                    // Set up the tiles based on the selected ability
                    Ability abilityObserving = AbilityPool.getInstance().get(ability.name);
                    Action action = observing.get(Action.class);
                    action.action = abilityObserving;
                    Action.act(model, observing, abilityObserving, null, false);
                });
            } else {
                button.setText("");
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            }
        }
//        logger.info("Updated condition panel for " + currentUnit);
    }
}