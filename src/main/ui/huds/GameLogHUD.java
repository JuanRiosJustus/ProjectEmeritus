package main.ui.huds;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import main.game.stores.pools.ColorPalette;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class GameLogHUD extends GameUI {
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private final Queue<String> queue = new LinkedList<>();
    private String last = "";
    private final JPanel container = new JPanel();
    private final Border buttonBorder = new EmptyBorder(5, 5, 5, 5);

    public GameLogHUD(int width, int height) {
        super(width, height, GameLogHUD.class.getSimpleName());
        add(contentPane(width, height));

        setBackground(ColorPalette.TRANSLUCENT_BLACK_V2);
//        setOpaque(false);
//        ComponentUtils.disable(this);
    }

    private JComponent contentPane(int width, int height) {

        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ColorPalette.TRANSPARENT);
        container.setFocusable(false);
        container.setOpaque(true);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;
        g.weighty = 1;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.BOTH;

        int rowsToShow = 8;
        for (int row = 0; row < rowsToShow; row++) {
            g.gridy = row;
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(width, height / rowsToShow));
            label.setFont(FontPool.getInstance().getFont(16));
            label.setForeground(ColorPalette.WHITE);
            label.setBackground(ColorPalette.TRANSPARENT);
            label.setOpaque(false);
            label.setFocusable(false);
            label.setBorder(buttonBorder);
            container.add(label, g);
        }


//        JScrollPane scrollPane = new JScrollPane(container,
//                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
//        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
//        scrollPane.getViewport().setOpaque(false);
//        scrollPane.getViewport().setBackground(ColorPalette.TRANSPARENT);
//        scrollPane.setOpaque(false);
//        scrollPane.setBackground(ColorPalette.TRANSPARENT);
//        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        return container;
    }

    public void log(Object source, String text) {
        queue.add("[" + source.toString() + "] " + text);
    }

    public void log(String text) { queue.add(text); }

    @Override
    public void gameUpdate(GameModel model) {

        if (last == null || model.mLogger.isEmpty()) { return; }
        if (last.equals(model.mLogger.peek())) { return; }

        last = model.mLogger.peek();

        // Move everything up 1
        int componentCount = container.getComponentCount();
        for (int i = 0; i < componentCount - 1; i++) {
            JLabel current = (JLabel) container.getComponent(i);
            JLabel next = (JLabel) container.getComponent(i + 1);
            current.setText(next.getText());
        }

        JLabel current = (JLabel) container.getComponent(componentCount - 1);
        current.setText(model.mLogger.poll());
//        current.setText("<html>" + model.logger.poll() + "</html>");
    }


//    @Override
//    public void jSceneUpdate(GameModel model) {
//
//        if (last == null || model.logger.isEmpty()) { return; }
//        if (last.equals(model.logger.peek())) { return; }
//
//        last = model.logger.peek();
//
//        // Move everything up 1
//        int componentCount = container.getComponentCount();
////        for (int i = 0; i < componentCount - 1; i++) {
////            JLabel current = (JLabel) container.getComponent(i);
////            JLabel next = (JLabel) container.getComponent(i + 1);
////            current.setText(next.getText());
////        }
//
////        JLabel current = (JLabel) container.getComponent(componentCount - 1);
////        current.setText("<html>" + model.logger.poll() + "</html>");
//        container.add(createEntry("<html>" + model.logger.poll() + "</html>"));
////        current.setText(model.logger.poll());
//
//    }
    private JLabel createEntry(String txt) {
        JLabel label = new JLabel();
//        label.setFont(FontPool.getInstance().getFont(14));
        label.setText(txt);
        label.setForeground(ColorPalette.WHITE);
        label.setBackground(ColorPalette.TRANSPARENT);
        label.setOpaque(true);
        label.setFocusable(false);
        label.setBorder(buttonBorder);
        return label;
    }
}
