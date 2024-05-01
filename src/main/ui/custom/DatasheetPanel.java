package main.ui.custom;


import main.game.stores.pools.ColorPalette;
import main.ui.components.OutlineLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DatasheetPanel extends JScrollPane {

    private final Map<String, JKeyValue> mStringToComponentMap = new HashMap<>();
    private GridBagConstraints mGridBagConstraints = new GridBagConstraints();
    private final JPanel mContainer = new JPanel();
    private int mMinimumHeightPerItem = 24;
    private int mRowHeight;
    private int mRowWidth;
    private final Map<String, JPanel> mComponentMap = new HashMap<>();
    private final Map<String, JComponent> mComponentMapV2 = new HashMap<>();

    public DatasheetPanel(int width, int height, Object[][] components) {

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel = setup(mStringToComponentMap, components, width, mMinimumHeightPerItem);
        result.add(panel, gbc);

        setViewportView(result);
        getViewport().setPreferredSize(new Dimension(width, height));

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder());
    }

    public DatasheetPanel(int width, int height) {

        mRowWidth = width;
        mRowHeight = height / 5;
        mContainer.setLayout(new BoxLayout(mContainer, BoxLayout.Y_AXIS));
//        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//        mContainer.setLayout(new GridBagLayout());
//        mContainer.setPreferredSize(new Dimension(width, height));
//        mContainer.setMaximumSize(new Dimension(width, height));
//        mContainer.setMinimumSize(new Dimension(width, height));
        mContainer.setBackground(ColorPalette.TRANSPARENT);

        setViewportView(mContainer);
        getViewport().setPreferredSize(new Dimension(width, height));

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder());
    }


    private boolean contains(JPanel parent, Component toFind) {
        for (Component iteration : parent.getComponents()) {
            if (iteration != toFind) { continue; }
            return true;
        }
        return false;
    }

    public void addRowComponent(String labelName, JComponent value) {
        // if this already exists in map, clear the map

        if (mComponentMapV2.containsKey(labelName)) {
            return;
        }


        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
//        row.setBackground(ColorPalette.getRandomColor());
        row.setBackground(ColorPalette.TRANSPARENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        row.add(value, gbc);
        int borderWidth = (int) (mRowWidth * .05);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row, mGridBagConstraints);
        mComponentMapV2.put(labelName, value);
    }

    public void addRowOutlineLabel(String labelName, String value) {
        // if this already exists in map, clear the map

        OutlineLabel label = (OutlineLabel) mComponentMapV2.get(labelName);
        if (label != null) {
            label.setText(value);
            return;
        }

        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
//        row.setBackground(ColorPalette.getRandomColor());
        row.setBackground(ColorPalette.TRANSPARENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new OutlineLabel();
        label.setText(labelName);
        SwingUiUtils.stylizeButtons(label, Color.WHITE, 16);
        label.setHorizontalAlignment(SwingConstants.LEFT);
//        label.setFocusPainted(false);
//        label.setBorderPainted(false);
//        label.setBackground(ColorPalette.getRandomColor());
        label.setBackground(ColorPalette.TRANSPARENT);

        row.add(label, gbc);

        OutlineLabel component = new OutlineLabel();
        component.setText(value);
        SwingUiUtils.stylizeButtons(component, Color.WHITE, 16);
        component.setHorizontalAlignment(SwingConstants.RIGHT);
//        component.setFocusPainted(false);
//        component.setBorderPainted(false);
        component.setText(value);
        component.setBackground(ColorPalette.TRANSPARENT);

        gbc.weightx = 0;
        gbc.gridx = 1;
        row.add(component, gbc);
        int borderWidth = (int) (mRowWidth * .05);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row, mGridBagConstraints);
        mComponentMapV2.put(labelName, component);
    }

    public void addRowLabel(String labelName, String value) {
        // if this already exists in map, clear the map

        JLabel label = (JLabel) mComponentMapV2.get(labelName);
        if (label != null) {
            label.setText(value);
            return;
        }

        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
//        row.setBackground(ColorPalette.getRandomColor());
        row.setBackground(ColorPalette.TRANSPARENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new JLabel(labelName);
        SwingUiUtils.stylizeButtons(label, Color.WHITE, 16);
        label.setHorizontalAlignment(SwingConstants.LEFT);
//        label.setFocusPainted(false);
//        label.setBorderPainted(false);
//        label.setBackground(ColorPalette.getRandomColor());
        label.setBackground(ColorPalette.TRANSPARENT);

        row.add(label, gbc);

        JLabel component = new JLabel(value);
        SwingUiUtils.stylizeButtons(component, Color.WHITE, 16);
        component.setHorizontalAlignment(SwingConstants.RIGHT);
//        component.setFocusPainted(false);
//        component.setBorderPainted(false);
        component.setText(value);

        gbc.weightx = 0;
        gbc.gridx = 1;
        row.add(component, gbc);
        int borderWidth = (int) (mRowWidth * .05);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row, mGridBagConstraints);
        mComponentMapV2.put(labelName, component);
    }


    public void addRowButton(String labelName, String value) {
        // if this already exists in map, clear the map

        JButton label = (JButton) mComponentMapV2.get(labelName);
        if (label != null) {
            label.setText(value);
            return;
        }

        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
//        row.setBackground(ColorPalette.getRandomColor());
        row.setBackground(ColorPalette.TRANSPARENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new JButton(labelName);
        SwingUiUtils.stylizeButtons(label, Color.WHITE, 16);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFocusPainted(false);
        label.setBorderPainted(false);
//        label.setBackground(ColorPalette.getRandomColor());
        label.setBackground(ColorPalette.TRANSPARENT);

        row.add(label, gbc);

        JButton component = new JButton(value);
        SwingUiUtils.stylizeButtons(component, Color.WHITE, 16);
        component.setHorizontalAlignment(SwingConstants.RIGHT);
        component.setFocusPainted(false);
        component.setBorderPainted(false);
        component.setText(value);

        gbc.weightx = 0;
        gbc.gridx = 1;
        row.add(component, gbc);
        int borderWidth = (int) (mRowWidth * .05);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row, mGridBagConstraints);
        mComponentMapV2.put(labelName, component);
    }



    private static JPanel setup(Map<String, JKeyValue> componentMap, Object[][] values, int width, int mMinimumHeightPerItem) {
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

            JKeyValue keyLabel = new JKeyValue(width, mMinimumHeightPerItem, key, component);
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

    public JKeyValue get(String name) {
        return mStringToComponentMap.get(name);
    }
    public Set<String> getKeys(String name) {
        return mStringToComponentMap.keySet().stream()
                .filter(e -> e.contains(name))
                .collect(Collectors.toSet());
    }

    public Set<String> getKeySet() { return mStringToComponentMap.keySet(); }
    public JKeyValue getOrCreateAndGet(String name) {
        return null;
    }
    public static JLabel getJLabelLabelComponent(DatasheetPanel mapV2, String key) {
        return mapV2.get(key).getKeyComponent();
    }
    public static JLabel getJLabelComponent(DatasheetPanel mapV2, String key) {
        return (JLabel) mapV2.get(key).getValueComponent();
    }

    public static JComboBox getJComboBoxComponent(DatasheetPanel mapV2, String key) {
        return (JComboBox) mapV2.get(key).getValueComponent();
    }
}
