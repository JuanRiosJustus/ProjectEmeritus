package ui.panels;

import constants.*;
import game.GameModel;
import game.entity.Entity;
import graphics.JScene;
import ui.presets.SceneManager;
import utils.ComponentUtils;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JScene {

    public MovementPanel movement = null;
    public EndTurnPanel endTurn = null;
    public SummaryPanel summaryPanel = null;
    public ActionPanel action = null;
    private final JPanel innerContainer = new JPanel();

    public ControlPanel(int width, int height) {
        super(width, height, "Control panel");

        add(createContentPane(width, height, 3));

        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    private JPanel createContentPane(int panelWidth, int panelHeight, int shrink) {

        int width = panelWidth / shrink;
        int height = panelHeight / shrink;

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        ComponentUtils.setMinMaxThenPreferredSize(content, width, height);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        movement = new MovementPanel(width, height);
        movement.getEnterButton().setFont(movement.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(movement.getEnterButton(), width / 2, height / 2);
        content.add(movement.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;

        action = new ActionPanel(width, height);
        action.getEnterButton().setFont(action.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(action.getEnterButton(), width / 2, height / 2);
        content.add(action.getEnterButton(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        summaryPanel = new SummaryPanel(width, height);
        summaryPanel.getEnterButton().setFont(summaryPanel.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(summaryPanel.getEnterButton(), width / 2, height / 2);
        content.add(summaryPanel.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        endTurn = new EndTurnPanel(width, height);
        endTurn.getEnterButton().setFont(endTurn.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(endTurn.getEnterButton(), width / 2, height / 2);
        content.add(endTurn.getEnterButton(), gbc);

        // Put the scene on bottom right corner
        innerContainer.setLayout(new BorderLayout());
        innerContainer.setPreferredSize(new Dimension(panelWidth, height));
        ComponentUtils.setTransparent(innerContainer);
        innerContainer.add(content, BorderLayout.LINE_END);
        innerContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel outerContainer = new JPanel();
        outerContainer.setPreferredSize(new Dimension(panelWidth, panelHeight));
        outerContainer.setLayout(new BorderLayout());
        outerContainer.add(innerContainer, BorderLayout.PAGE_END);
        ComponentUtils.setTransparent(outerContainer);
        outerContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        movement.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(movement.getName(), movement);
            innerContainer.removeAll();
            innerContainer.add(movement, BorderLayout.LINE_END);
            innerContainer.revalidate();
            innerContainer.repaint();
        });
        movement.getExitButton().addActionListener(e -> reset());

        action.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(action.getName(), action);
            innerContainer.removeAll();
            innerContainer.add(action, BorderLayout.LINE_END);
            innerContainer.revalidate();
            innerContainer.repaint();
        });
        action.getExitButton().addActionListener(e -> reset());

        summaryPanel.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(summaryPanel.getName(), summaryPanel);
            innerContainer.removeAll();
            innerContainer.add(summaryPanel, BorderLayout.LINE_END);
            innerContainer.revalidate();
            innerContainer.repaint();
        });
        summaryPanel.getExitButton().addActionListener(e -> reset());

        endTurn.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(endTurn.getName(), endTurn);
            innerContainer.removeAll();
            innerContainer.add(endTurn, BorderLayout.LINE_END);
            innerContainer.revalidate();
            innerContainer.repaint();
        });
        endTurn.getExitButton().addActionListener(e -> reset());

        return outerContainer;
    }

    public void reset() {
        innerContainer.removeAll();
        innerContainer.add(SceneManager.instance().get(this.getName()), BorderLayout.LINE_END);
        innerContainer.revalidate();
        innerContainer.repaint();
    }

    public void update(GameModel model) {

        Entity unit = model.unitTurnQueue.peek();

        Entity entity = (Entity) model.state.getObject(GameStateKey.CURRENTLY_SELECTED);

        if (entity == null) { return; }

        if (summaryPanel.isShowing()) {
            summaryPanel.set(model, entity);
        } else if (movement.isShowing()) {
            movement.set(model, entity);
        } else if (action.isShowing()) {
            action.set(model, entity);
        } else if (endTurn.isShowing()) {
            endTurn.update(model);
        }

        if (model.state.getBoolean(GameStateKey.UI_GO_TO_CONTROL_HOME)) {
            reset();
            model.state.set(GameStateKey.UI_GO_TO_CONTROL_HOME, false);
        }

        model.state.set(GameStateKey.UI_SUMMARY_PANEL_SHOWING, summaryPanel.isShowing());
        model.state.set(GameStateKey.UI_MOVEMENT_PANEL_SHOWING, movement.isShowing());
        model.state.set(GameStateKey.UI_ACTION_PANEL_SHOWING, action.isShowing());
        model.state.set(Constants.END_UI_SHOWING, endTurn.isShowing());


//        model.ui.set(Constants.ABILITY_UI_SHOWING, ability.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_SHOWING, settings.isShowing());
////        model.ui.set(Constants.ACTIONS_UI_ENDTURN, actions.endTurnToggleButton.isSelected());
//        model.ui.set(Constants.MOVEMENT_UI_SHOWING, movement.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_AUTOENDTURNS, settings.autoEndTurns.isSelected());
//        model.ui.set(Constants.SETTINGS_UI_FASTFORWARDTURNS, settings.fastForward.isSelected());

    }
}
