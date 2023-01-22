package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.GameModel;
import game.entity.Entity;
import graphics.JScene;
import ui.presets.SceneManager;
import utils.ComponentUtils;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ControlPanel extends JScene {

    public MovementPanel movement = null;
    public EndTurnPanel endTurn = null;
    public SummaryPanel summaryPanel = null;
    public ActionPanel action = null;
    private final JPanel container = new JPanel();

    public ControlPanel(int width, int height) {
        super(width, height, "MainControllerPanel");
        add(createContentPane(width / 4, height / 4));
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    private JPanel createContentPane(int width, int height) {

        JPanel content = ComponentUtils.createTransparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        movement = new MovementPanel(width, height);
        movement.getEnterButton().setFont(movement.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(movement.getEnterButton(), width / 2, height / 2);
        content.add(movement.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;

//        action = new TurnStatusPanel(width, height, "Act");
        action = new ActionPanel(width, height);//new AbilityPanel();
        action.getEnterButton().setFont(action.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(action.getEnterButton(), width / 2, height / 2);
        content.add(action.getEnterButton(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        summaryPanel = new SummaryPanel(width, height);
        summaryPanel.getEnterButton().setFont(summaryPanel.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(summaryPanel.getEnterButton(), width / 2, height / 2);
        content.add(summaryPanel.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        endTurn = new EndTurnPanel(width, height);
        endTurn.getEnterButton().setFont(endTurn.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(endTurn.getEnterButton(), width / 2, height / 2);
        content.add(endTurn.getEnterButton(), gbc);

        // Put the scene on bottom right corner
//        contentContainer = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        container.setLayout(new BorderLayout(10, 10));
        ComponentUtils.setTransparent(container);
        container.add(content, BorderLayout.LINE_END);
        container.setBackground(ColorPalette.TRANSPARENT);
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel b2 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b2.add(container, BorderLayout.PAGE_END);
        b2.setBackground(ColorPalette.TRANSPARENT);
        b2.setOpaque(false);
        b2.setBorder(new EmptyBorder(10, 10, 10, 10));

        movement.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(movement.getName(), movement);
            container.removeAll();
            container.add(movement, BorderLayout.LINE_END);
            container.revalidate();
            container.repaint();
        });
        movement.getExitButton().addActionListener(e -> reset());

        action.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(action.getName(), action);
            container.removeAll();
            container.add(action, BorderLayout.LINE_END);
            container.revalidate();
            container.repaint();
        });
        action.getExitButton().addActionListener(e -> reset());

        summaryPanel.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(summaryPanel.getName(), summaryPanel);
            container.removeAll();
            container.add(summaryPanel, BorderLayout.LINE_END);
            container.revalidate();
            container.repaint();
        });
        summaryPanel.getExitButton().addActionListener(e -> reset());

        endTurn.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(endTurn.getName(), endTurn);
            container.removeAll();
            container.add(endTurn, BorderLayout.LINE_END);
            container.revalidate();
            container.repaint();
        });
        endTurn.getExitButton().addActionListener(e -> reset());

        return b2;
    }

    public void reset() {
        container.removeAll();
        container.add(SceneManager.instance().get(this.getName()), BorderLayout.LINE_END);
        container.revalidate();
        container.repaint();
    }

    public void update(GameModel model) {

        Entity unit = model.queue.peek();

        Entity entity = (Entity) model.state.get(GameStateKey.CURRENTLY_SELECTED);

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

        if (model.state.getBoolean(Constants.RESET_UI)) {
            reset();
            model.state.set(Constants.RESET_UI, false);
        }

        model.state.set(GameStateKey.CONDITION_PANEL_SHOWING, summaryPanel.isShowing());
        model.state.set(GameStateKey.MOVEMENT_PANEL_SHOWING, movement.isShowing());
        model.state.set(GameStateKey.ACTION_PANEL_SHOWING, action.isShowing());
        model.state.set(Constants.END_UI_SHOWING, endTurn.isShowing());


//        model.ui.set(Constants.ABILITY_UI_SHOWING, ability.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_SHOWING, settings.isShowing());
////        model.ui.set(Constants.ACTIONS_UI_ENDTURN, actions.endTurnToggleButton.isSelected());
//        model.ui.set(Constants.MOVEMENT_UI_SHOWING, movement.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_AUTOENDTURNS, settings.autoEndTurns.isSelected());
//        model.ui.set(Constants.SETTINGS_UI_FASTFORWARDTURNS, settings.fastForward.isSelected());

    }
}
