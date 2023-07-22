package engine;

import constants.ColorPalette;
import constants.Constants;
import input.InputController;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EngineView extends JFrame {

    private final JPanel container = new JPanel();
    
    public EngineView() {

        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
        addMouseMotionListener(InputController.instance().getMouse());
        addMouseListener(InputController.instance().getMouse());
        addKeyListener(InputController.instance().getKeyboard());
        addMouseWheelListener(InputController.instance().getMouse());

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
                Engine.instance().stop();
            }
        });

        
         
        OverlayLayout layout = new OverlayLayout(container);
        container.setLayout(layout);
        container.add(new JLabel());

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);

        add(container);

        setLocationRelativeTo(null);

        revalidate();
        repaint();
        setVisible(true);
    }

    public void setScene(JPanel scene) {
        container.removeAll();
    //    container.add(SceneManager.instance().getSceneSelectionPanel());
        container.add(scene);
        container.revalidate();
        container.repaint();
    }

    public void render() {
        // if (this.container.getComponents().length == 0) { return; }
        // if (this.container.getRootPane() == null) { return; }
        // Unless really need constrol of enging UI, can rely on manipulating scenes that are just put ont he engine
        // TODO Tentatively removed, might be safe to completely remove later
        // revalidate();
        // repaint();
    }
}
