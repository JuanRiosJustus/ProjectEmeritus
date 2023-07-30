package main.ui.screen.editor;

import main.input.InputController;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GameEditorMainPanel extends JPanel {

    private GameEditorState m_gme = new GameEditorState();
    private GameEditorSidePanel m_gep;
    private InputController inputController;


    public GameEditorMainPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setSize(width, height);
//        m_gep = new GameEditorSidePanel(100, height);

        setName("editor Screen");
        setDoubleBuffered(true);
        setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        m_gme.handleRenderAndUpdate(g, inputController, m_gep);
    }

    private void addKeyMouseMotionListener(InputController controls) {
        addKeyListener(controls.getKeyboard());
        addMouseListener(controls.getMouse());
        addMouseMotionListener(controls.getMouse());
        addMouseWheelListener(controls.getMouse());
    }

    public void linkToScreen(InputController controls, GameEditorSidePanel panel) {
        if (inputController != null) { return; }
        inputController = controls;
        m_gep = panel;
        addKeyMouseMotionListener(controls);
    }
}
