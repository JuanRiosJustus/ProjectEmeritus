package engine;

import constants.ColorPalette;
import constants.Constants;
import input.InputController;
import ui.panels.SummaryPanel2;
import ui.screen.GamePanel;
import ui.screen.GameScreen;
import ui.screen.MainMenuPanel;
import utils.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EngineView extends JFrame {
    private final JPanel container = new JPanel();
    public EngineView(EngineController controller) {

        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;
        addMouseMotionListener(controller.model.input);
        setFocusable(true);
        requestFocusInWindow();
        setSize(width, height);
        setTitle(Constants.APPLICATION_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Engine.get().stop();
            }
        });

        container.setLayout(new OverlayLayout(container));
        container.add(setupGameMainPanel(controller.model));
        container.add(new GameScreen(controller));
//        container.add(new GamePanel());
        add(container);

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);
//        mainMenu();

//        set(null);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void set(JComponent component) {

        container.removeAll();

        container.setLayout(new OverlayLayout(container));
        container.add(setupGameMainPanel(Engine.get().controller().model));
        container.add(new GameScreen(Engine.get().controller()));

        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);
    }

    public void mainMenu() {

        container.removeAll();

        container.setLayout(new FlowLayout());
//        container.add(new MainMenuPanel(1280, 720));

        container.setBackground(Color.CYAN);
        container.revalidate();
        container.repaint();
        container.setDoubleBuffered(true);
    }

    public void createSummaryBox(Point point) {
//        SummaryPanel2 summaryPanel = new SummaryPanel2();
//        summaryPanel.setBounds(point.x, point.y, summaryPanel.getWidth(), summaryPanel.getHeight());
//
//        System.out.println("Setting at " + point);
//
//        JPanel popupPanel = new JPanel();
//        popupPanel.setBackground(ColorPalette.TRANSPARENT);
//        popupPanel.setOpaque(false);
//        popupPanel.setLayout(null);
//
////        popupPanel.setMaximumSize(summaryPanel.getMaximumSize());
////        popupPanel.setMinimumSize(summaryPanel.getMinimumSize());
////        popupPanel.setPreferredSize(summaryPanel.getPreferredSize());
//
//        popupPanel.setBorder(new LineBorder(ColorPalette.BLACK));
//        popupPanel.setVisible(true);
//
//        popupPanel.add(summaryPanel, BorderLayout.CENTER);
//
//        JButton popupCloseButton = new JButton("Close");
//        popupPanel.add(ComponentUtils.wrap(popupCloseButton), BorderLayout.SOUTH);
//
//        popupCloseButton.addActionListener(e -> {
////            overlapComponent.setEnabled(true);
//            popupPanel.setVisible(false);
//            container.remove(popupPanel);
//        });
//
//        container.add(popupPanel, 0);
        render(null);
    }

    private JPanel setupGameMainPanel(EngineModel model) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(model.ui, BorderLayout.EAST);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setOpaque(false);

        return panel;
    }

    private void addMouseMotionListener(InputController controls) {
        setFocusable(true);
        addKeyListener(controls.keyboard());
        requestFocusInWindow();
    }

    public void render(EngineController controller) {
        revalidate();
        repaint();
    }

    public JPanel getView() { return container; }
}
