package main.engine;

import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.input.InputControllerV1;

import javax.swing.*;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class EngineView extends JFrame {

    private final JPanel container = new JPanel();
    private final Map<JComponent, String> sceneMap = new HashMap<>();
    
    public EngineView() {
        initialize(Constants.APPLICATION_WIDTH, Constants.APPLICATION_HEIGHT);
    }

    public void setEngineWidthAndHeight(int width, int height) {
        initialize(width, height);
    }


    private void initialize(int width, int height) {
        addMouseMotionListener(InputControllerV1.getInstance().getMouse());
        addMouseListener(InputControllerV1.getInstance().getMouse());
//        addKeyListener(InputController.getInstance().getKeyboard());
        addMouseWheelListener(InputControllerV1.getInstance().getMouse());

        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

//        setLocationRelativeTo(GraphicsEnvironment.getCenterPoint );
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

        container.setLayout(new CardLayout());
        container.setDoubleBuffered(true);

        add(container);
        setLocationRelativeTo(null);
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
        scene.setBackground(ColorPalette.BLACK);
        append(scene);
        show(scene);
    }

    public void render() {
        // Unless really need constrol of enging UI, can rely on manipulating scenes that are just put ont he engine
        // TODO Tentatively removed, might be safe to completely remove later
        // Needed for when we add other ui panels to engine
         revalidate();
         repaint();
//         System.out.println("t " +(iter++));
    }
}