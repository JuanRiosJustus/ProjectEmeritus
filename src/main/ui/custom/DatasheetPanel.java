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
    private final Map<String, JComponent> mComponentMap = new HashMap<>();

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
        this(width, height, 5);
    }

    public DatasheetPanel(int width, int height, int rowsInView) {
        // TODO why does this have to be multiplied by .8?
        mRowWidth = width;
        mRowHeight = height / rowsInView;

        mContainer.setLayout(new BoxLayout(mContainer, BoxLayout.Y_AXIS));

        setViewportView(mContainer);
        getViewport().setPreferredSize(new Dimension(width, height));

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        setBorder(BorderFactory.createEmptyBorder());
    }

    public void setBackground(Color color) {
        if (mContainer == null) { return; }
        mContainer.setBackground(color);
    }

    public Color getBackground() {
        if (mContainer == null) {
            return super.getBackground();
        }
        return mContainer.getBackground();
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
        if (mComponentMap.containsKey(labelName)) {
            return;
        }

        // Setup the row structure
        JPanel row = new JPanel();

        row.setLayout(new GridBagLayout());
        row.setBackground(ColorPalette.TRANSPARENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        row.add(value, gbc);
        int borderWidth = (int) (mRowWidth * .05);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row, mGridBagConstraints);
        mComponentMap.put(labelName, value);
    }

    public void addRowOutlineLabel(String labelName, String labelValue) {
        // if this already exists in map, clear the map

        OutlineLabel key = (OutlineLabel) mComponentMap.get(labelName);
        if (key != null) {
            key.setText(labelValue);
            return;
        }

        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
        row.setBackground(ColorPalette.TRANSPARENT);
//        row.setBackground(ColorPalette.getRandomColor());

//        System.out.println(mRowWidth + " x");
//        System.out.println(getPreferredSize().getWidth() + " xx");
//        System.out.println(getMinimumSize().getWidth() + " xxx");
//        System.out.println(getMaximumSize().getWidth() + " xxxx");
//
//        System.out.println(mRowHeight + " y");
//        System.out.println(getPreferredSize().getHeight() + " yy");
//        System.out.println(getMinimumSize().getHeight() + " yyy");
//        System.out.println(getMaximumSize().getHeight() + " yyyy");

        row.setPreferredSize(new Dimension((int) row.getPreferredSize().getWidth(), mRowHeight));
//        row.setMinimumSize(new Dimension(mRowWidth, mRowHeight));
//        row.setMaximumSize(new Dimension(mRowWidth, mRowHeight));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;

        key = new OutlineLabel();
        key.setText(labelName);
        SwingUiUtils.stylizeButtons(key, Color.WHITE, 16);
        key.setHorizontalAlignment(SwingConstants.LEFT);
        key.setBackground(ColorPalette.TRANSPARENT);
//        key.setBackground(ColorPalette.getRandomColor());
        row.add(key, gbc);

        OutlineLabel value = new OutlineLabel();
        value.setText(labelValue);
        SwingUiUtils.stylizeButtons(value, Color.WHITE, 16);
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        value.setBackground(ColorPalette.TRANSPARENT);
//        value.setBackground(ColorPalette.getRandomColor());

        gbc.weightx = 1;
        gbc.gridx = 1;
        row.add(value, gbc);

        int borderWidth = (int) (mRowWidth * .025);
        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        mGridBagConstraints.fill = GridBagConstraints.BOTH;
        mGridBagConstraints.gridy = mGridBagConstraints.gridy + 1;

        mContainer.add(row);
        mComponentMap.put(labelName, value);
    }

    public void addRow(String labelName, String value) {
        // if this already exists in map, clear the map

        JLabel label = (JLabel) mComponentMap.get(labelName);
        if (label != null) {
            label.setText(value);
            return;
        }

        JPanel row = new JPanel();
        row.setLayout(new GridBagLayout());
        row.setBackground(ColorPalette.getRandomColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new JLabel(labelName);
        SwingUiUtils.stylizeButtons(label, Color.WHITE, 16);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setBackground(ColorPalette.TRANSPARENT);

        row.add(label, gbc);

        JLabel component = new JLabel(value);
        SwingUiUtils.stylizeButtons(component, Color.WHITE, 16);
        component.setHorizontalAlignment(SwingConstants.RIGHT);
        component.setText(value);

        gbc.weightx = 0;
        gbc.gridx = 1;
        row.add(component, gbc);
//        int borderWidth = (int) (mRowWidth * .05);
//        row.setBorder(new EmptyBorder(0, borderWidth, 0, borderWidth));

        mGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        mGridBagConstraints.gridy++;

        mContainer.add(row);
//        mContainer.add(row, mGridBagConstraints);
        mComponentMap.put(labelName, component);
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
