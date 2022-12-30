package ui.presets;

import constants.ColorPalette;
import constants.Constants;
import engine.Engine;
import game.GameController;
import ui.presets.SceneManager;
import utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;

public class MenuScene extends JPanel {
    //
    public final JButton startButton = new JButton(Constants.START_BUTTON);
    public final JButton loadButton = new JButton(Constants.CONTINUE_BUTTON);
    public final JButton settingsButton = new JButton(Constants.SETTINGS_BUTTON);
    public final JButton editButton = new JButton(Constants.EDIT_BUTTON);
    public final JButton exitButton = new JButton(Constants.EXIT_BUTTON);

//    private final GameEngineModel m_model;
//    private final InputController m_controls;

//    private GameScene m_gameScene;
//    private EditorScene m_editorScene;
    private final JPanel mainContainer = new JPanel(new GridBagLayout());

    private static final int SPACING = 15;

//    private void initScene(JPanel jPanel, JGamePanel panel) {
////        // look for jgamepanel that has same name
////        for (int i = 0; i < jPanel.getComponents().length; i++) {
////            Component component = jPanel.getComponent(i);
////            System.out.println(component.getName() + " ?");
////            if (component.getName().equals(panel.getName())) {
////                jPanel.remove(i);
////                break;
////            }
////        }
////
////        jPanel.add(panel, panel.getName());
////
////        CardLayout cl = (CardLayout)jPanel.getLayout();
////        cl.show(jPanel, panel.getName());
////        revalidate();
////        repaint();
//    }

    public MenuScene(int width, int height) {

        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        setSize(new Dimension(width, height));

        setLayout(null);
        int buttonHeight = 50;
        int buttonWidth = 150;

        int y = (int) (getHeight() * .8);

        JButton b1 = ComponentUtils.createJButton("Start");
        b1.setBounds((int) (getWidth() * .15), y, buttonWidth, buttonHeight);
//        b1.addActionListener(e -> Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.GAME_SCENE)));
        b1.addActionListener(e -> SceneManager.instance().setScene(Constants.GAME_SCENE));

        JButton b2 = ComponentUtils.createJButton("Load");
        b2.setBounds((int) (getWidth() * .30), y, buttonWidth, buttonHeight);

        JButton b3 = ComponentUtils.createJButton("Editor");
        b3.setBounds((int) (getWidth() * .45), y, buttonWidth, buttonHeight);
//        b3.addActionListener(e -> Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.EDIT_SCENE)));
        b3.addActionListener(e -> SceneManager.instance().setScene(Constants.EDIT_SCENE));
//
        JButton b4 = ComponentUtils.createJButton("Settings");
        b4.setBounds((int) (getWidth() * .60), y, buttonWidth, buttonHeight);
        b4.addActionListener(e -> Engine.instance().run());

        JButton b5 = ComponentUtils.createJButton("Exit");
        b5.setBounds((int) (getWidth() * .75), y, buttonWidth, buttonHeight);
        b5.addActionListener(e -> Engine.instance().stop());

        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        setBackground(ColorPalette.BEIGE);

        add(mainContainer);

        setDoubleBuffered(true);
//        setupButtons(model, controls);
    }

//    private JPanel createMenu() {
//        JGamePanel jgp = new JGamePanel(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT, "Main Menu");
//        jgp.setLayout(new GridBagLayout());
//
//        m_gameButton.addActionListener(e -> {
//            m_gameScene = new GameScene(m_model, m_controls);
//            System.out.println(m_gameScene.getName() + " ?");
//
//        });
//        m_editorButton.addActionListener(e -> {
//            m_editorScene = new EditorScene(m_model, m_controls);
//            System.out.println(m_editorScene.getName() + " ?");
//        });
//
//        m_exitButton.addActionListener(e -> {
//            System.exit(0);
//        });
//
//        jgp.add(m_gameButton, m_constraints);
//        jgp.add(m_editorButton, m_constraints);
//        jgp.add(m_exitButton, m_constraints);
//
//        return jgp;
//    }


//    public void addButton(JButton button) {
//        for (int i = 0; i < getComponentCount(); i++) {
//            Component comp = getComponent(i);
//            if (comp == button) {
//                getComponents()[i] = button;
//                return;
//            }
//        }
//        add(button, m_constraints);
//    }
//
//    public JButton getCampaignButton() { return m_campaign; }
//    public JButton getEditorButton() { return m_creation; }
}
