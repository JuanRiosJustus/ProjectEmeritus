package main.ui.huds;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import main.constants.ColorPalette;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class ActivityLogHUD extends JScene {

    private ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private String last = "";
    private final JPanel container = new JPanel();
    private final Border buttonBorder = new EmptyBorder(5, 5, 5, 5);

    public ActivityLogHUD(int width, int height) {
        super(width, height, ActivityLogHUD.class.getSimpleName());
        add(contentPane(width, height));

        setOpaque(false);
    }

    private JComponent contentPane(int width, int height) {

        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ColorPalette.TRANSPARENT);
        //     container.setBorder(
        // BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorPalette.BEIGE),
        // BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        container.setOpaque(true);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;
        g.weighty = 1;
        g.fill = 1;
//        g.ipady = 5;

        int rowsToShow = 10;
        for (int row = 0; row < rowsToShow; row++) {
            g.gridy = row;
            JLabel label = new JLabel();
            label.setPreferredSize(new Dimension(width, (int) height / rowsToShow));
            label.setFont(FontPool.getInstance().getFont(16));
//            label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
            label.setForeground(ColorPalette.WHITE);
            label.setBackground(ColorPalette.TRANSLUCENT_BLACK_V2);
            label.setOpaque(true);
            label.setBorder(buttonBorder);
            container.add(label, g);
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
//        scrollPane.setAutoscrolls(true);
        return scrollPane;
    }

//    private JComponent contentPane(int width, int height) {
//
//        container.setPreferredSize(new Dimension(width, height));
//        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//        container.setBackground(ColorPalette.TRANSPARENT);
//            //     container.setBorder(
//            // BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ColorPalette.BEIGE),
//            // BorderFactory.createEmptyBorder(0, 0, 0, 0)));
//        container.setOpaque(true);
//
//        GridBagConstraints g = new GridBagConstraints();
//        g.gridx = 0;
//        g.gridy = 0;
//        g.weightx = 1;
//        g.weighty = 1;
//        g.ipady = 5;
//
//
//        int rowsToShow = 10;
//        int buttonWidth = 150;
//        int buttonHeight = height / rowsToShow;
//        for (int row = 0; row < rowsToShow; row++) {
//            g.gridy = row;
//            JLabel label = new JLabel("");
//            label.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
//            label.setFont(FontPool.getInstance().getFont(16));
////            label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
//            label.setForeground(ColorPalette.WHITE);
//            label.setBackground(ColorPalette.TRANSPARENT_BLACK);
//            label.setOpaque(true);
//            label.setBorder(buttonBorder);
//            container.add(label);
//        }
//
//
//         JScrollPane scrollPane = new JScrollPane(container,
//                 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//         scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
//         scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
//         scrollPane.getViewport().setOpaque(false);
//         scrollPane.getViewport().setBackground(ColorPalette.TRANSPARENT);
//         scrollPane.setOpaque(false);
//         scrollPane.setBackground(ColorPalette.TRANSPARENT);
//         scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//        return scrollPane;
//    }

    @Override
    public void jSceneUpdate(GameModel model) {
        if (last == null || model.logger.isEmpty()) { return; }
        if (last.equals(model.logger.peek())) { return; }

        last = model.logger.peek();

        // Move everything up 1
        int componentCount = container.getComponentCount();
        for (int i = 0; i < componentCount - 1; i++) {
            JLabel current = (JLabel) container.getComponent(i);
            JLabel next = (JLabel) container.getComponent(i + 1);
            current.setText(next.getText());
        }

        JLabel current = (JLabel) container.getComponent(componentCount - 1);
        current.setText("<html>" + model.logger.poll() + "</html>");
    }
}
