package main.ui.huds.controls.v1;


import main.constants.Constants;
import main.game.components.Abilities;
import main.game.components.Action;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;
import main.game.stores.pools.ability.AbilityPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabelOld;
import main.constants.GameState;
import main.ui.panels.ControlPanelPane;
import main.ui.panels.StatScrollPane;
import main.utils.ComponentUtils;
import main.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MiniActionHUD extends ControlPanelPane {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabelOld> labelMap = new HashMap<>();
    private final StatScrollPane scrollPane;

    public MiniActionHUD(int width, int height) {
        super(width, height, "Mini Actions");

        scrollPane = createTopRightPanel(topRight);
        topRight.add(scrollPane);

        JScrollPane middleScroller = createMiddlePanel(middle);
        middle.add(middleScroller);
    }

    protected StatScrollPane createTopRightPanel(JComponent reference) {
        return new StatScrollPane(
                (int)reference.getPreferredSize().getWidth(),
                (int)reference.getPreferredSize().getHeight(),
                new String[]{
                        Constants.NAME, Constants.TYPE,
                        Constants.ACC, Constants.IMPACT,
                        Constants.RANGE, Constants.AREA,
                        Constants.HP_COST, Constants.EP_COST,
                        Constants.HP_DAMAGE, Constants.EP_DAMAGE,
                        Constants.DESCRIPTION
                }
        );
    }

    protected JScrollPane createMiddlePanel(JComponent reference) {

        actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 4));
        actionPanel.setPreferredSize(reference.getPreferredSize());

        for (int i = 0; i < 12; i++) {
            JButton button = new JButton(String.valueOf(i));
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

        currentUnit = null;

//        labelMap.get("NAME").value.setText("");
//        labelMap.get("HEALTH DAMAGE").value.setText("");
//        labelMap.get("ENERGY DAMAGE").value.setText("");
//        labelMap.get("TYPE").value.setText("");
//        labelMap.get("ACCURACY").value.setText("");
//        labelMap.get("AREA").value.setText("");
//        labelMap.get("RANGE").value.setText("");
//        labelMap.get("HEALTH COST").value.setText("");
//        labelMap.get("ENERGY COST").value.setText("");

//        lastToggledButton = currentlyToggledButton;
//        currentlyToggledButton = null;
//        if (lastToggledButton != null) { lastToggledButton.setSelected(false); }
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (currentUnit == null) { return; }
        if (currentTile == null) { return; }
        topLeft.set(currentUnit);

        List<Ability> abilities = currentUnit.get(Abilities.class)
                .getAbilities()
                .stream()
                .map(e -> AbilityPool.getInstance().get(e))
                .toList();

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
                    scrollPane.get(Constants.NAME).setText(ability.name);
                    scrollPane.get(Constants.IMPACT).setText(ability.impact);
                    int temp = (int) ability.getHealthDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_DAMAGE).setText(String.valueOf(temp));
                    } else { scrollPane.get(Constants.HP_DAMAGE).setText("~"); }

                    temp = (int) ability.getEnergyDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_DAMAGE).setText(String.valueOf(temp));
                    } else { scrollPane.get(Constants.EP_DAMAGE).setText("~"); }

                    scrollPane.get(Constants.TYPE).setText(ability.getTypes().toString());
                    scrollPane.get(Constants.ACC).setText(MathUtils.floatToPercent(ability.accuracy));

                    temp = ability.getHealthCost(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_COST).setText(temp + " / " +
                                summary.getStatCurrent(Constants.HEALTH));
                    } else { scrollPane.get(Constants.HP_COST).setText("~"); }

                    temp = ability.getEnergyCost(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_COST).setText(temp + " / " +
                                summary.getStatCurrent(Constants.ENERGY));
                    } else { scrollPane.get(Constants.EP_COST).setText("~"); }

                    scrollPane.get(Constants.AREA).setText(ability.area + "");
                    scrollPane.get(Constants.RANGE).setText(ability.range + "");
                    scrollPane.get(Constants.DESCRIPTION).setText(ability.description);

                    // Set up the tiles based on the selected ability
                    Ability abilityObserving = AbilityPool.getInstance().get(ability.name);

                    Action action = observing.get(Action.class);
                    action.action = abilityObserving;
                    Action.act(model, observing, ability, null, false);

                    logger.debug("{} is selected", button.getName());
                    gameModel.gameState.set(GameState.ACTION_PANEL_SELECTED_ACTION, ability);
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
