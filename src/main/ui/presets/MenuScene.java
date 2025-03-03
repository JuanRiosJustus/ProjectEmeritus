package main.ui.presets;

import main.game.main.GameControllerV1;
import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.engine.Engine;
import main.engine.EngineScene;
import main.ui.game.panels.NewGameStartup;
import main.ui.presets.editor.EditorScene;
import main.utils.ComponentUtils;
import main.utils.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuScene extends EngineScene {
    //
    public final JButton startButton = new JButton(Constants.START_BUTTON);
    public final JButton loadButton = new JButton(Constants.CONTINUE_BUTTON);
    public final JButton settingsButton = new JButton(Constants.SETTINGS_BUTTON);
    public final JButton editButton = new JButton(Constants.EDIT_BUTTON);
    public final JButton exitButton = new JButton(Constants.EXIT_BUTTON);
    private BufferedImage image = null;

//    private final GameEngineModel m_model;
//    private final InputController m_controls;

//    private GameScene m_gameScene;
//    private EditorScene m_editorScene;
    private final JPanel mainContainer = new JPanel(new GridBagLayout());

    private static final int SPACING = 15;

//    private void initScene(JPanel jPanel, GameUI panel) {
////        // look for GameUI that has same name
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
        super(width, height);

         setSize(new Dimension(width, height));

        try {
            image = ImageIO.read(new File("res/data/test5.png"));
            image = ImageUtils.getResizedImage(image, width, height);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        setLayout(null);
        int buttonHeight = (int) (getHeight() * .075);
        int buttonWidth = (int) (getWidth() * .2);

        double percentFromTop = 0;
        int y = (int) (getHeight() * .8);
        double percentFromLeft = .05;
        int x = (int) (getWidth() * percentFromLeft);

        JButton b0 = new JButton("New Game");
        b0.setBounds(x, (int) (getHeight() * .2), buttonWidth, buttonHeight);
        b0.addActionListener(e -> {
//            GameView game = GameController.getInstance().getView();
            NewGameStartup newGame = new NewGameStartup(width, height);
            Engine.getInstance().getController().stage(newGame);
        });


        JButton b1 = ComponentUtils.createJButton("Load Game");
        b1.setBounds(x, (int) (getHeight() * .3), buttonWidth, buttonHeight);
        b1.addActionListener(e -> {
            Engine.getInstance().getController().stage(GameControllerV1.getInstance());
        });


        JButton b2 = ComponentUtils.createJButton("Continue");
        b2.setBounds(x, (int) (getHeight() * .4), buttonWidth, buttonHeight);
        b2.addActionListener(e -> {
            Engine.getInstance().getController().stage(GameControllerV1.getInstance());
        });

        JButton b3 = ComponentUtils.createJButton("Editor");
        b3.setBounds(x, (int) (getHeight() * .5), buttonWidth, buttonHeight);
        b3.addActionListener(e -> {
            EditorScene editor = new EditorScene(getWidth(), getHeight());
            Engine.getInstance().getController().stage(editor);
        });

        JButton b4 = ComponentUtils.createJButton("Arena");
        b4.setBounds(x, (int) (getHeight() * .6), buttonWidth, buttonHeight);
        b4.addActionListener(e -> {
            EditorScene editor = new EditorScene(getWidth(), getHeight());
            Engine.getInstance().getController().stage(editor);
        });

        JButton b5 = ComponentUtils.createJButton("Settings");
        b5.setBounds(x, (int) (getHeight() * .7), buttonWidth, buttonHeight);
        b5.addActionListener(e -> Engine.getInstance().stop());

        JButton b6 = ComponentUtils.createJButton("Exit");
        b6.setBounds(x, (int) (getHeight() * .8), buttonWidth, buttonHeight);
        b6.addActionListener(e -> Engine.getInstance().stop());

        add(b0);
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);

//        Button b = new Button("Test");
//        b.setBounds(50, 50, 100, 50);
//        add(b);
        setBackground(ColorPalette.BEIGE);



//        BufferedImage myPicture = null;
//        try {
//            myPicture = ImageIO.read(new File("path-to-file"));
//            JLabel picLabel = new JLabel(new ImageIcon(ImageUtils.getResizedImage(myPicture, Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT)));
//            add(picLabel);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        add(mainContainer);

        setDoubleBuffered(true);
//        setupButtons(model, controls);
    }

    public JPanel createButtonPanel(int width, int height) {

        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) { return; }
        if (!isShowing()) { return; }
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void update() {

    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return this;
    }

}
