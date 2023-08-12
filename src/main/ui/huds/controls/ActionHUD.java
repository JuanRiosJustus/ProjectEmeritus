package main.ui.huds.controls;


import main.constants.Constants;
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
import main.ui.panels.ControlPanelPane;
import main.ui.panels.StatScrollPane;
import main.utils.ComponentUtils;
import main.utils.MathUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionHUD extends ControlPanelPane {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private JPanel actionPanel;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private final Map<String, JKeyLabel> labelMap = new HashMap<>();
    private final StatScrollPane scrollPane;

    public ActionHUD(int width, int height) {
        super(width, height, "Actions");

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

        currentUnit = null;

        labelMap.get("NAME").value.setText("");
        labelMap.get("HEALTH DAMAGE").value.setText("");
        labelMap.get("ENERGY DAMAGE").value.setText("");
        labelMap.get("TYPE").value.setText("");
        labelMap.get("ACCURACY").value.setText("");
        labelMap.get("AREA").value.setText("");
        labelMap.get("RANGE").value.setText("");
        labelMap.get("HEALTH COST").value.setText("");
        labelMap.get("ENERGY COST").value.setText("");

        lastToggledButton = currentlyToggledButton;
        currentlyToggledButton = null;
        if (lastToggledButton != null) { lastToggledButton.setSelected(false); }
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
        if (gameModel == null) { return; }
        if (currentUnit == null) { return; }
        if (currentTile == null) { return; }
        topLeft.set(currentUnit);

        List<Ability> abilities = currentUnit.get(Abilities.class).getAbilities()
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
                    Entity observing = currentUnit;
                    logger.info("Selected {} button while observing {}", button.getText(), observing.toString());


//                    scrollPane.get(Constants.NAME).value.setText(ability.name);
//                    scrollPane.get(Constants.IMPACT).value.setText(ability.impact);
//                    int temp = 0;
//
//                    scrollPane.get(Constants.HP_DAMAGE).value.setText((int)ability.getHealthDamage(observing) + "");
//                    scrollPane.get(Constants.EP_DAMAGE).value.setText((int)ability.getEnergyDamage(observing) + "");
//                    scrollPane.get(Constants.TYPE).value.setText(ability.getTypes().toString());
//                    scrollPane.get(Constants.ACC).value.setText(MathUtils.floatToPercent(ability.accuracy));
//                    scrollPane.get(Constants.AREA).value.setText((int)ability.area + "");
//                    scrollPane.get(Constants.RANGE).value.setText((int)ability.range + "");
//                    scrollPane.get(Constants.HP_COST).value.setText((int)ability.getHealthCost(observing) + "");
//                    scrollPane.get(Constants.EP_COST).value.setText((int)ability.getEnergyCost(observing) + "");
//                    scrollPane.get(Constants.DESCRIPTION).value.setText(ability.description);

                    scrollPane.get(Constants.NAME).value.setText(ability.name);
                    scrollPane.get(Constants.IMPACT).value.setText(ability.impact);
                    int temp = (int) ability.getHealthDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_DAMAGE).value.setText(String.valueOf(temp));
                    } else {
                        scrollPane.get(Constants.HP_DAMAGE).value.setText("");
                    }

                    temp = (int) ability.getEnergyDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_DAMAGE).value.setText(String.valueOf(temp));
                    } else {
                        scrollPane.get(Constants.EP_DAMAGE).value.setText("");
                    }

                    scrollPane.get(Constants.TYPE).value.setText(ability.getTypes().toString());
                    scrollPane.get(Constants.ACC).value.setText(MathUtils.floatToPercent(ability.accuracy));

                    temp = (int)ability.getHealthCost(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_COST).value.setText(String.valueOf(temp));
                    } else {
                        scrollPane.get(Constants.HP_COST).value.setText("");
                    }

                    temp = (int)ability.getEnergyDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.EP_COST).value.setText(String.valueOf(temp));
                    } else {
                        scrollPane.get(Constants.EP_COST).value.setText("");
                    }

                    scrollPane.get(Constants.AREA).value.setText((int)ability.area + "");
                    scrollPane.get(Constants.RANGE).value.setText((int)ability.range + "");
                    scrollPane.get(Constants.DESCRIPTION).value.setText(ability.description);

                    ActionManager am = currentUnit.get(ActionManager.class);
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
