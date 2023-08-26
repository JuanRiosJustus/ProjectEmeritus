package main.ui.custom;


import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

public class JKeyValueArray extends JScrollPane {

    private final Map<String, JKeyValue> keyLabelMap = new HashMap<>();
//    private final Map<JPanel>
    private static final int MINIMUM_STAT_LABEL_ITEM_HEIGHT = 24;

    public JKeyValueArray(int width, int height, String[] labels) {

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

    private static JPanel createJPanelColumns(Map<String, JKeyValue> map, String[] values, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        for (int index = 0; index < values.length; index++) {
            String value = values[index];

            JKeyValue keyLabel = new JKeyValue(width, MINIMUM_STAT_LABEL_ITEM_HEIGHT, value);
            keyLabel.setOpaque(false);
//            keyLabel.setBackground(ColorPalette.getRandomColor());
//            keyLabel.setBackground(ColorPalette.BLACK);

            gbc.gridy = index;

            panel.add(keyLabel, gbc);
            map.put(value, keyLabel);
        }

        return panel;
    }

    public JKeyValue get(String name) {
        return keyLabelMap.get(name);
    }

    public JKeyValue getOrCreateAndGet(String name) {
        return null;
    }
}
