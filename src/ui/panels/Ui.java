package ui.panels;

import constants.Constants;
import graphics.JScene;

import javax.swing.*;
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

}
