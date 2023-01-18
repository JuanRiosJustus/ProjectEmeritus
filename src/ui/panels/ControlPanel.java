package ui.panels;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.GameModel;
import game.components.Tile;
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
    public ConditionPanel condition = null;
    public AbilityPanel action = null;
    private final JPanel contentContainer = new JPanel();

    public ControlPanel(int width, int height) {
        super(width, height, "MainControllerPanel");
        add(contentPane(width / 4, height / 4));
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    private JPanel contentPane(int width, int height) {

        JPanel content = ComponentUtils.createTransparentPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        movement = new MovementPanel(width, height, "Move");
        movement.getEnterButton().setFont(movement.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(movement.getEnterButton(), width / 2, height / 2);
        content.add(movement.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;

//        action = new TurnStatusPanel(width, height, "Act");
        action = new AbilityPanel();
//        action.setText("Act");
//        action.setFont(action.getFont().deriveFont(30f));
        action.getEnterButton().setFont(action.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(action.getEnterButton(), width / 2, height / 2);
        content.add(action.getEnterButton(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        condition = new ConditionPanel(width, height);
        condition.getEnterButton().setFont(condition.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(condition.getEnterButton(), width / 2, height / 2);
        content.add(condition.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        endTurn = new EndTurnPanel(width, height);
        endTurn.getEnterButton().setFont(endTurn.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setSize(endTurn.getEnterButton(), width / 2, height / 2);
        content.add(endTurn.getEnterButton(), gbc);

        ComponentUtils.setSize(content, width, height);

        // Put the scene on bottom right corner
//        contentContainer = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        contentContainer.setLayout(new BorderLayout(10, 10));
        ComponentUtils.setTransparent(contentContainer);
        contentContainer.add(content, BorderLayout.LINE_END);
        contentContainer.setBackground(ColorPalette.TRANSPARENT);
        contentContainer.setOpaque(false);
        contentContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel b2 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b2.add(contentContainer, BorderLayout.PAGE_END);
        b2.setBackground(ColorPalette.TRANSPARENT);
        b2.setOpaque(false);
        b2.setBorder(new EmptyBorder(10, 10, 10, 10));

        movement.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(movement.getName(), movement);
            contentContainer.removeAll();
            contentContainer.add(movement, BorderLayout.LINE_END);
            contentContainer.revalidate();
            contentContainer.repaint();
        });
        movement.getExitButton().addActionListener(e -> reset());

        action.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(action.getName(), action);
            contentContainer.removeAll();
            contentContainer.add(action, BorderLayout.LINE_END);
            contentContainer.revalidate();
            contentContainer.repaint();
        });
        action.getExitButton().addActionListener(e -> reset());

        condition.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(condition.getName(), condition);
            contentContainer.removeAll();
            contentContainer.add(condition, BorderLayout.LINE_END);
            contentContainer.revalidate();
            contentContainer.repaint();
        });
        condition.getExitButton().addActionListener(e -> reset());

        endTurn.getEnterButton().addActionListener(e -> {
            SceneManager.instance().install(this.getName(), content);
            SceneManager.instance().install(endTurn.getName(), endTurn);
            contentContainer.removeAll();
            contentContainer.add(endTurn, BorderLayout.LINE_END);
            contentContainer.revalidate();
            contentContainer.repaint();
        });
        endTurn.getExitButton().addActionListener(e -> reset());

        return b2;
    }

    public void reset() {
        contentContainer.removeAll();
        contentContainer.add(SceneManager.instance().get(this.getName()), BorderLayout.LINE_END);
        contentContainer.revalidate();
        contentContainer.repaint();
    }

    public void update(GameModel model) {

        Entity unit = model.queue.peek();

        Entity entity = (Entity) model.state.get(GameStateKey.CURRENTLY_SELECTED);

        if (entity == null) { return; }

        if (condition.isShowing()) {
            condition.set(entity.get(Tile.class).unit);
        } else if (movement.isShowing()) {
            movement.set(entity.get(Tile.class).unit);
        } else if (action.isShowing()) {
            action.set(model, entity);
        } else if (endTurn.isShowing()) {
            endTurn.update(model);
        }

        if (model.state.getBoolean(Constants.RESET_UI)) {
            reset();
            model.state.set(Constants.RESET_UI, false);
        }


//        if (ability.isShowing()) {
//            ability.set(model, unit);
//        }
//        if (actions.isShowing()) {
//            actions.set(unit);
//        }
//        if (summary.isShowing()) {
//            summary.set(unit);
//        }
//        if (movement.isShowing()) {
//            movement.set(unit);
//        }
//        if (items.isShowing()) {
//            items.set(unit);
//        }
//        if (order.isShowing()) {
//            order.set(model.queue);
//        }
//        if (selection.isShowing()) {
////            selection.set(mousedAt);
////            engine.model.ui.summary.set(unit);
//        }
////        if (model.ui.wasUpdated)


        model.state.set(GameStateKey.CONDITION_UI_SHOWING, condition.isShowing());
        model.state.set(GameStateKey.MOVEMENT_UI_SHOWING, movement.isShowing());
        model.state.set(GameStateKey.ACTION_UI_SHOWING, action.isShowing());
        model.state.set(Constants.END_UI_SHOWING, endTurn.isShowing());


//        model.ui.set(Constants.ABILITY_UI_SHOWING, ability.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_SHOWING, settings.isShowing());
////        model.ui.set(Constants.ACTIONS_UI_ENDTURN, actions.endTurnToggleButton.isSelected());
//        model.ui.set(Constants.MOVEMENT_UI_SHOWING, movement.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_AUTOENDTURNS, settings.autoEndTurns.isSelected());
//        model.ui.set(Constants.SETTINGS_UI_FASTFORWARDTURNS, settings.fastForward.isSelected());

    }
}
