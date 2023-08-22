package main.ui.panels;

import main.ui.custom.HtmlKeyLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class StatScrollPane extends JScrollPane {

    private final Map<String, HtmlKeyLabel> keyLabelMap = new HashMap<>();
    private static final int MINIMUM_STAT_LABEL_ITEM_HEIGHT = 30;

    public StatScrollPane(int width, int height, String[] labels) {

        Dimension dimension = new Dimension((int) (width), (int) (height));

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        result.add(createJPanelColumns(keyLabelMap, labels, width, height), gbc);

        setViewportView(result);
        getViewport().setPreferredSize(new Dimension(width, height));

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder());
    }

    private static JPanel createJPanelColumns(Map<String, HtmlKeyLabel> map, String[] values, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;

        int labelWidth = width / 2;

        for (int index = 0; index < values.length; index++) {
            String value = values[index];
//            JKeyLabel label = new JKeyLabel(value + ": ", "");
            HtmlKeyLabel label = new HtmlKeyLabel();
            label.setKeyAndLabel(value + ": ", "");
//            label.setForeground(ColorPalette.getRandomColor());
//            label.setOpaque(true);
//            label.setBackground(ColorPalette.getRandomColor());
//            label.setBackground(ColorPalette.TRANSPARENT);
            label.setPreferredSize(new Dimension(labelWidth, MINIMUM_STAT_LABEL_ITEM_HEIGHT));

//            label.key.setFont(label.key.getFont().deriveFont(Font.BOLD));
//            label.value.setWrapStyleWord(true);
//            label.value.setLineWrap(true);
//            label.value.setOpaque(false);
//            label.value.setPreferredSize(new Dimension((int)
//                    (labelWidth - label.key.getPreferredSize().getWidth()), MINIMUM_STAT_LABEL_ITEM_HEIGHT ));
//            label.setBackground(ColorPalette.getRandomColor());
//             label.setBackground(ColorPalette.getRandomColor());

            gbc.gridx = (index % 2);
            gbc.gridy = (index / 2);
            if (gbc.gridx == 0 && index == values.length - 1) {

                gbc.fill = GridBagConstraints.BOTH;
                gbc.anchor = GridBagConstraints.CENTER;
                int height2 = 2;
                int width2 = 2;
                gbc.gridwidth = width2;
                gbc.gridheight = height2;
                label.setPreferredSize(new Dimension(labelWidth * width2, MINIMUM_STAT_LABEL_ITEM_HEIGHT * height2));
                label.setKeyAndLabel("", "");
            }
            panel.add(label, gbc);
            map.put(value, label);

        }

        panel.setBorder(new EmptyBorder(5, 5, 5,5));
        return panel;
    }

    public HtmlKeyLabel get(String name) {
        return keyLabelMap.get(name);
    }
}
