package ui.panels;

import logging.Logger;
import logging.LoggerFactory;
import graphics.JScene;

import javax.swing.*;
import java.awt.*;

public class CarouselPanel extends JScene {
    public CarouselPanel(int width, int height, String name) {
        super(width, height, name);
    }

//    private final JPanel content = new JPanel();
//    private final JPanel buttons;
//    private final GridBagConstraints constraints = new GridBagConstraints();
//    private final GridBagConstraints buttonConstraints = new GridBagConstraints();
//    private final Logger logger = LoggerFactory.instance().logger(getClass());
//
//    private Component[] defaultComponents;
//
//    public CarouselPanel(int width, int height) {
//        super(width, height, "Main");
//        content.setLayout(new CardLayout());
//
//        buttons = new JPanel();
//        buttons.setLayout(new GridBagLayout());
//
//        content.add(buttons, "BUTTONS");
//
//        add(content);
//
//        defaultComponents = getComponents();
//    }
//
//    public void ride(JScene[] panelScenes) {
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.fill = GridBagConstraints.VERTICAL;
//
//
//        for (JScene scene : panelScenes) {
//            JButton enter = scene.getEnterButton();
//            JButton exit = scene.getExitButton();
//            buttons.add(enter, gbc);
//            content.add(scene, enter.getName());
//
//            enter.addActionListener(e -> {
//                CardLayout cl = (CardLayout) content.getLayout();
//                cl.show(content, enter.getName());
//            });
//
//            exit.addActionListener(e -> {
//                CardLayout cl = (CardLayout) content.getLayout();
//                cl.show(content, "BUTTONS");
//            });
//
//            scene.mold(getWidth(), getHeight());
//        }
//    }
//
//    public void addMenuOptionButtons(JScene panel) {
//
//        JButton onEnter = panel.getEnterButton();
//        content.add(onEnter, buttonConstraints);
//        onEnter.addActionListener(e -> {
//            // save original buttons to return to
//            defaultComponents = getComponents();
//            removeAll();
//            add(panel, constraints);
//            revalidate();
//            repaint();
////            EmeritusLogger.get().log("Panel changed to " + onEnter.getText());
//        });
//
//        JButton onExit = panel.getExitButton();
//        onExit.addActionListener(e -> {
//            removeAll();
//            for (Component c : defaultComponents) { add(c, constraints); }
//            revalidate();
//            repaint();
////            EmeritusLogger.get().log("Panel changed to " + getName());
//        });
//    }
//
//    public void forceExitToMain() {
//        CardLayout cl = (CardLayout) content.getLayout();
//        cl.show(content, "BUTTONS");
//        logger.log("Panel changed to " + "BUTTONS");
//    }
//
//    public void disableButtons() {
//        for (Component component : content.getComponents()) {
//            JButton button = (JButton) component;
//            button.setEnabled(false);
//        }
//    }
//    public void enableButtons() {
//        for (Component component : content.getComponents()) {
//            JButton button = (JButton) component;
//            button.setEnabled(true);
//        }
//    }
}
