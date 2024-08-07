package main.engine;

import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.constants.Settings;
import main.input.InputController;
import javax.swing.*;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class EngineView extends JFrame {

    private final JPanel container = new JPanel();
    private final Map<JComponent, String> sceneMap = new HashMap<>();
    
    public EngineView() {
        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);
        initialize(width, height);
//        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
//        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT);
//
//        addMouseMotionListener(InputController.getInstance().getMouse());
//        addMouseListener(InputController.getInstance().getMouse());
//        addKeyListener(InputController.getInstance().getKeyboard());
//        addMouseWheelListener(InputController.getInstance().getMouse());
//
////        InputController.getInstance().listenTo(this);
//
////         setUndecorated(true);
////         setExtendedState();
//        setFocusable(true);
//        requestFocusInWindow();
//        setSize(width, height);
//        setBackground(ColorPalette.BLACK);
//        setTitle(Constants.APPLICATION_NAME);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setResizable(false);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                super.windowClosing(e);
//                Engine.getInstance().stop();
//            }
//        });
//
//        CardLayout layout = new CardLayout();
//        container.setLayout(layout);
//
//        container.revalidate();
//        container.repaint();
//        container.setDoubleBuffered(true);
//
//        // TODO figure out why the bounds must be a little negative, and overly larger than width and height
//        setLayout(null);
//        container.setBounds(0, 0, width, height);
//
//        add(container);
//
//        setLocationRelativeTo(null);
//        setBackground(ColorPalette.BLACK);
//
//        revalidate();
//        repaint();
//        setVisible(true);
    }

    public void setEngineWidthAndHeight(int width, int height) {
        initialize(width, height);
    }


    private void initialize(int width, int height) {
        addMouseMotionListener(InputController.getInstance().getMouse());
        addMouseListener(InputController.getInstance().getMouse());
        addKeyListener(InputController.getInstance().getKeyboard());
        addMouseWheelListener(InputController.getInstance().getMouse());

//        InputController.getInstance().listenTo(this);

//         setUndecorated(true);
//         setExtendedState();
        setFocusable(true);
        requestFocusInWindow();
        setSize(width, height);
        setBackground(ColorPalette.BLACK);
        setTitle(Constants.APPLICATION_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Engine.getInstance().stop();
            }
        });

        CardLayout layout = new CardLayout();
        container.setLayout(layout);

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);

        // TODO figure out why the bounds must be a little negative, and overly larger than width and height
        setLayout(null);
        container.setBounds(0, 0, width, height);

        add(container);

        setLocationRelativeTo(null);
        setBackground(ColorPalette.BLACK);

        revalidate();
        repaint();
        setVisible(true);
    }

    private void append(JPanel scene) {
        if (sceneMap.get(scene) != null) { return; }
        sceneMap.put(scene, scene.getClass().getSimpleName());
        container.add(scene, scene.getClass().getSimpleName());
    }
    
    private void show(JPanel scene) {
        String id = sceneMap.get(scene);
        if (id == null) { return; }
        CardLayout cl = (CardLayout)(container.getLayout());
        cl.show(container, id);
    }

    public void stage(JPanel scene) {
        append(scene);
        show(scene);
    }

    public void render() {
        // Unless really need constrol of enging UI, can rely on manipulating scenes that are just put ont he engine
        // TODO Tentatively removed, might be safe to completely remove later
        // Needed for when we add other ui panels to engine
         revalidate();
         repaint();
    }
}