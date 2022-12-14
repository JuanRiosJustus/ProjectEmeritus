package ui.presets;

import constants.Constants;
import input.InputController;
import ui.screen.editor.GameEditorMainPanel;
import ui.screen.editor.GameEditorSidePanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class EditorScene extends JPanel {
    public GameEditorMainPanel gameBoard;
    public GameEditorSidePanel m_panel;

    public EditorScene(int width, int height) {

        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setPreferredSize(new Dimension(width, height));
        setSize(new Dimension(width, height));

        init(InputController.instance());
//        setLayout(new BorderLayout());
//
//        m_board = new GameEditorScreen(getWidth() - Constants.SIDE_BAR_WIDTH, getHeight());
//        add(m_board, BorderLayout.CENTER);
//
//        m_panel = new GameEditorPanel(Constants.SIDE_BAR_WIDTH, getHeight(), getEscapeButton());
//        add(m_panel, BorderLayout.EAST);
//
//        m_board.linkToScreen(controls, m_panel);
    }

    public void init(InputController controls){
        removeAll();
        setLayout(new BorderLayout());

        gameBoard = new GameEditorMainPanel(getWidth() - Constants.SIDE_BAR_WIDTH, getHeight());
        add(gameBoard, BorderLayout.CENTER);

        m_panel = new GameEditorSidePanel(Constants.SIDE_BAR_WIDTH, getHeight());
//        JLayeredPane jlp = new JLayeredPane();
        add(m_panel, BorderLayout.EAST);


//        JButton jb = new JButton("Test");
//        jb.setOpaque(true);
//        jb.setBounds(50, 50, 100, 100);
//
//
//        JLayeredPane jlp = new JLayeredPane();
//        jlp.add(m_board, JLayeredPane.DEFAULT_LAYER);
//        jlp.add(jb, JLayeredPane.MODAL_LAYER);
////
//        add(jlp, BorderLayout.CENTER);

        gameBoard.linkToScreen(controls, m_panel);
        revalidate();
        repaint();
    }
}
