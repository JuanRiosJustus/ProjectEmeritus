package main.ui.huds.controls.v1;


import main.constants.Constants;
import main.game.components.Actions;
import main.game.components.ActionManager;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Action;
import main.game.stores.pools.action.ActionPool;
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
                        Constants.HP_COST, Constants.MP_COST,
                        Constants.HP_DAMAGE, Constants.MP_DAMAGE,
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

        List<Action> abilities = currentUnit.get(Actions.class)
                .getAbilities()
                .stream()
                .map(e -> ActionPool.getInstance().get(e))
                .toList();

        for (int index = 0; index < actionPanel.getComponents().length; index++) {
            JButton button = (JButton) actionPanel.getComponents()[index];
            Action action = (abilities.size() > index ? abilities.get(index) : null);
            if (action != null) {
                button.setText(action.name);
                button.setName(action.name);
                button.setBorderPainted(true);
                button.setFocusPainted(true);
                ComponentUtils.removeActionListeners(button);
                button.addActionListener(e -> {
                    // Get the currently observed unit from the HUD
                    Entity observing = currentUnit;
                    if (observing == null) { return; }
                    logger.info("Selected {} button while observing {}", button.getText(), observing.toString());
                    // Check that the current unit has the selected ability
                    Set<String> observingAbilities = observing.get(Actions.class).getAbilities();
                    if (!observingAbilities.contains(action.name)) { return; }
                    // Setup the UI to show the current ability's information
                    Statistics statistics = observing.get(Statistics.class);
                    scrollPane.get(Constants.NAME).setText(action.name);
                    scrollPane.get(Constants.IMPACT).setText(action.impact);
                    int temp = (int) action.getHealthDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_DAMAGE).setText(String.valueOf(temp));
                    } else { scrollPane.get(Constants.HP_DAMAGE).setText("~"); }

                    temp = (int) action.getEnergyDamage(observing);
                    if (temp != 0) {
                        scrollPane.get(Constants.MP_DAMAGE).setText(String.valueOf(temp));
                    } else { scrollPane.get(Constants.MP_DAMAGE).setText("~"); }

                    scrollPane.get(Constants.TYPE).setText(action.getTypes().toString());
                    scrollPane.get(Constants.ACC).setText(MathUtils.floatToPercent(action.accuracy));

                    temp = action.getCost(observing, Statistics.HEALTH);
                    if (temp != 0) {
                        scrollPane.get(Constants.HP_COST).setText(temp + " / " +
                                statistics.getStatCurrent(Statistics.HEALTH));
                    } else { scrollPane.get(Constants.HP_COST).setText("~"); }

                    temp = action.getCost(observing, Statistics.MANA);
                    if (temp != 0) {
                        scrollPane.get(Constants.MP_COST).setText(temp + " / " +
                                statistics.getStatCurrent(Statistics.MANA));
                    } else { scrollPane.get(Constants.MP_COST).setText("~"); }

                    scrollPane.get(Constants.AREA).setText(action.area + "");
                    scrollPane.get(Constants.RANGE).setText(action.range + "");
                    scrollPane.get(Constants.DESCRIPTION).setText(action.description);

                    // Set up the tiles based on the selected ability
                    Action actionObserving = ActionPool.getInstance().get(action.name);

                    ActionManager actionManager = observing.get(ActionManager.class);
                    actionManager.preparing = actionObserving;
                    ActionManager.act(model, observing, action, null, false);

                    logger.debug("{} is selected", button.getName());
                    gameModel.gameState.set(GameState.ACTION_PANEL_SELECTED_ACTION, action);
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
