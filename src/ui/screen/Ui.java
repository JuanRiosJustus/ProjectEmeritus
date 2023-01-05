package ui.screen;

import constants.Constants;
import game.GameModel;
import game.entity.Entity;
import graphics.JScene;
import ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class Ui extends JScene {

    private final CarouselPanel carousel;
    public final QueuePanel order;
    public final ActionPanel actions;
    public final SelectionPanel selection;

    public MovementPanel movement = new MovementPanel();
    public LogPanel log = new LogPanel();
    public AbilityPanel ability = new AbilityPanel();
    public SummaryPanel summary = new SummaryPanel();
    public SettingsPanel settings = new SettingsPanel();
    public ItemsPanel items = new ItemsPanel();
    private static final GridBagConstraints gbc = new GridBagConstraints();

    public Ui() {
        super(Constants.SIDE_BAR_WIDTH, Constants.APPLICATION_HEIGHT, "ScreenUI");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        order = new QueuePanel(Constants.SIDE_BAR_WIDTH, (int) (Constants.APPLICATION_HEIGHT * .075));
        add(order);

        selection = new SelectionPanel(Constants.SIDE_BAR_WIDTH, (int) (Constants.APPLICATION_HEIGHT * .125));
        add(selection);

        actions = new ActionPanel(Constants.SIDE_BAR_WIDTH, (int) (Constants.APPLICATION_HEIGHT * .15));
        add(actions);

        carousel = new CarouselPanel(Constants.SIDE_BAR_WIDTH, (int) (Constants.APPLICATION_HEIGHT * .65));
        carousel.ride(new JScene[] {movement, ability, summary, items, log, settings});

        add(carousel);
//
//
////        backForth = new CarouselControlPanel(Constants.SIDE_BAR_WIDTH, (int) (Constants.APPLICATION_HEIGHT * .001));
//        add(backForth);
//        backForth.setBackground(ColorPalette.GREEN);

//        add(getEscapeButton(), m_constraints);
//        add(m_log, m_constraints);
//        add(getEscapeButton(), m_constraints);
//        m_log.setBackground(Color.BLUE);

//        setBackground(Color.BLACK);
//        setBackground(new Color(0, 0, 0, 0));
//        setOpaque(false);

//        setUIFont(new FontUIResource(FontStore.get().getFont(16)));
    }

    public JPanel getContainer() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(this, BorderLayout.EAST);
//        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setBackground(Color.RED);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);


        return panel;
    }
    public static void setUIFont(FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource orig) {
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

    public void disablePanels() { carousel.disableButtons(); }
    public void enablePanels() { carousel.enableButtons(); }
    public void exitToMain() { carousel.forceExitToMain(); }
    public AbilityPanel getAbilities() { return ability; }
    public LogPanel getLog() { return log; }

    public void update(GameModel model) {

        Entity unit = model.queue.peek();

        if (ability.isShowing()) {
            ability.set(model, unit);
        }
        if (actions.isShowing()) {
            actions.set(unit);
        }
        if (summary.isShowing()) {
            summary.set(unit);
        }
        if (movement.isShowing()) {
            movement.set(unit);
        }
        if (items.isShowing()) {
            items.set(unit);
        }
        if (order.isShowing()) {
            order.set(model.queue);
        }
        if (selection.isShowing()) {
//            selection.set(mousedAt);
//            engine.model.ui.summary.set(unit);
        }
//        if (model.ui.wasUpdated)
        model.ui.set(Constants.ABILITY_UI_SHOWING, ability.isShowing());
        model.ui.set(Constants.SETTINGS_UI_SHOWING, settings.isShowing());
//        model.ui.set(Constants.ACTIONS_UI_ENDTURN, actions.endTurnToggleButton.isSelected());
        model.ui.set(Constants.MOVEMENT_UI_SHOWING, movement.isShowing());
//        model.ui.set(Constants.SETTINGS_UI_AUTOENDTURNS, settings.autoEndTurns.isSelected());
//        model.ui.set(Constants.SETTINGS_UI_FASTFORWARDTURNS, settings.fastForward.isSelected());

    }

}
