package main.ui.custom;


import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JKeyValueMapV2 extends JScrollPane {

    private final Map<String, JKeyValueV2> mStringToComponentMap = new HashMap<>();
    private static final int MINIMUM_STAT_LABEL_ITEM_HEIGHT = 24;

    public JKeyValueMapV2(int width, int height, Object[][] components) {

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel panel = setup(mStringToComponentMap, components, width);
        result.add(panel, gbc);

        setViewportView(result);
        getViewport().setPreferredSize(new Dimension(width, height));

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder());
    }

    private static JPanel setup(Map<String, JKeyValueV2> componentMap, Object[][] values, int width) {
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
            String key = (String) values[index][0];
            JComponent component = (JComponent) values[index][1];

            JKeyValueV2 keyLabel = new JKeyValueV2(width, MINIMUM_STAT_LABEL_ITEM_HEIGHT, key, component);
            keyLabel.setOpaque(false);
////            keyLabel.setBackground(ColorPalette.getRandomColor());
////            keyLabel.setBackground(ColorPalette.BLACK);

            gbc.gridy = index;

//            JPanel

            panel.add(keyLabel, gbc);
            componentMap.put(key, keyLabel);
        }

        return panel;
    }

    public JKeyValueV2 get(String name) {
        return mStringToComponentMap.get(name);
    }
    public Set<String> getKeys(String name) {
        return mStringToComponentMap.keySet().stream()
                .filter(e -> e.contains(name))
                .collect(Collectors.toSet());
    }

    public Set<String> getKeySet() { return mStringToComponentMap.keySet(); }

    public JKeyValueV2 getOrCreateAndGet(String name) {
        return null;
    }
}
