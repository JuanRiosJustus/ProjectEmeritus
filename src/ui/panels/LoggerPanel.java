package ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import constants.ColorPalette;
import game.main.GameModel;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;

public class LoggerPanel extends JScene {

    private Logger logger = LoggerFactory.instance().logger(getClass());
    private String last = "";
    private final JPanel container = new JPanel();

    public LoggerPanel(int width, int height) {
        super(width, height, "MiniMapPanel");
        setPreferredSize(new Dimension(width, height));

        add(contentPane(width / 4, height / 4));

        setOpaque(false);
        setBackground(ColorPalette.TRANSPARENT);
    }

    private JPanel contentPane(int width, int height) {

        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ColorPalette.TRANSPARENT_BLACK);
        container.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;
        g.weighty = 1;

        for (int row = 0; row < 8; row++) {
            g.gridy = row;
            JLabel label = new JLabel(" ");
            label.setPreferredSize(new Dimension(150, 40));
            label.setBackground(ColorPalette.TRANSPARENT_BLACK);
            label.setForeground(ColorPalette.WHITE);
            label.setBorder(new EmptyBorder(5, 5, 5, 5));
            container.add(label);
        }

        JScrollPane scrollPane = new JScrollPane(container,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(ColorPalette.TRANSPARENT);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(ColorPalette.TRANSPARENT);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Put the scene on bottom right corner
        JPanel b1 = new JPanel();
        b1.setLayout(new BorderLayout(10, 10));
        b1.add(scrollPane, BorderLayout.LINE_START);
        b1.setBackground(ColorPalette.TRANSPARENT);
        b1.setOpaque(false);
        b1.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel b2 = new JPanel();
        b2.setLayout(new BorderLayout(10, 10));
        b2.add(b1, BorderLayout.PAGE_START);
        b2.setBackground(ColorPalette.BLUE);
        b2.setOpaque(false);
        b2.setBorder(new EmptyBorder(10, 10, 10, 10));

        return b2;
    }

    public void update(GameModel model) {
        if (last == null || model.uiLogQueue.isEmpty()) { return; }
        if (last.equals(model.uiLogQueue.peek())) { return; }

        last = model.uiLogQueue.peek();

        while (model.uiLogQueue.size() > 0) {
            JLabel label = new JLabel(model.uiLogQueue.poll());
            logger.log(label.getText());
            label.setPreferredSize(new Dimension(150, 40));
            label.setBackground(ColorPalette.TRANSPARENT_BLACK);
            label.setForeground(ColorPalette.WHITE);
            label.setBorder(new EmptyBorder(5, 5, 5, 5));
            container.add(label);
            container.remove(0);
        }

        revalidate();
        repaint();
    }
}
