package main.ui.huds.controls;

import main.constants.Pair;
import main.constants.Tuple;
import main.ui.components.OutlineButton;
import main.ui.components.OutlineLabel;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class OutlineMapPanel extends JGamePanel {

    private JPanel mContainer = null;
    private final Map<Object, JComponent> mComponentMap = new LinkedHashMap<>();
    private final Map<String, Object> mReturnMap = new HashMap<>();
    private final Map<String, JComponent> mReturnMapV2 = new HashMap<>();
    private int mComponentWidth = 0;
    private int mComponentHeight = 0;
    private int mVisibleComponentsCount = 5;

    public OutlineMapPanel(int width, int height, int visible) {
        this(width, height, 0, 0, visible);
    }

    public OutlineMapPanel(int width, int height, int x, int y, int visible) {

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(width, height));
        setOpaque(true);

        mVisibleComponentsCount = visible;

        mContainer = new JGamePanel();
        mContainer.setLayout(new BoxLayout(mContainer, BoxLayout.Y_AXIS));
//        mContainer.setLayout(new GridBagLayout());

        JScrollPane pane;

        mComponentWidth = width;
        mComponentHeight = height / mVisibleComponentsCount;

        pane = SwingUiUtils.createBonelessScrollingPane(width, height, mContainer);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        add(pane, constraints);
        setBounds(x, y, width, height);
    }

    public OutlineButton createButton(Object id) {
        OutlineButton outlineButton = (OutlineButton) getOrPut(id, null);
        if (outlineButton == null) {
            outlineButton = (OutlineButton) getOrPut(id, new OutlineButton(id.toString()));
        }
        return outlineButton;
    }

//    public OutlineButton createButton(Object id) {
//        OutlineButton existingButton = (OutlineButton) putComponent(id, null);
//        if (existingButton == null) {
//            existingButton = (OutlineButton) putComponent(id, new OutlineButton(id.toString()));
//        }
//        OutlineButton outlineButton = new OutlineButton(id.toString());
//        putComponent(id, outlineButton);
//        return outlineButton;
//    }


    public OutlineButton createButton(Object id, String txt) {
        OutlineButton outlineButton = new OutlineButton(txt);
        getOrPut(id, outlineButton);
        return outlineButton;
    }


    public OutlineLabel putLabel(String id) {
        OutlineLabel outlineLabel = new OutlineLabel(id);
        getOrPut(id, outlineLabel);
        return outlineLabel;
    }

    public JComponent getOrPut(Object id, JComponent component) {
        // if component is null, just check if we have component with id
        JComponent current = mComponentMap.get(id);
        if (component == null) { return current; }
        if (current != null) {
            // Case where this component already exists, dont update anyhting
            if (component == current) { return component; }
            // Case where components are different but share id, use the new component
            int indexToUpdateAt = -1; // find the older component to replace
            for (int index = 0; index < mContainer.getComponentCount(); index++) {
                if (mContainer.getComponent(index) == current) { indexToUpdateAt = index; break; }
            }
            // THIS SHOULD HAPPEN, sO THEN WHY DOES IT?
            if (indexToUpdateAt == -1) {
                return null;
            }
            mContainer.remove(indexToUpdateAt);
            mContainer.add(component, indexToUpdateAt);
            // ensure the updated component fits within the contraints
        } else {
            mContainer.add(component);
        }
        current = component;
        current.setPreferredSize(new Dimension(mComponentWidth, mComponentHeight));
        current.setMinimumSize(current.getPreferredSize());
        current.setMaximumSize(current.getPreferredSize());

        // If this position was previous filled by another component, same id, replace old component
        mComponentMap.put(id, current);
        boolean mIsDirty = true;
        return current;
    }

//    public JComponent putComponent(Object id, JComponent component) {
//        // Check if we've already added the component
//        JComponent current = mComponentMap.get(id);
//        // if the component exists, ignore
//        if (current != null) {
////            // Update the current structues with the current component if theyre not the same
//            return current;
//        }
//        current = component;
//        int currentWidth = (int) (mComponentWidth * 1);
//        int currentHeight = (int) (mComponentHeight * 1);
//        current.setPreferredSize(new Dimension(currentWidth, currentHeight));
//        current.setMinimumSize(current.getPreferredSize());
//        current.setMaximumSize(current.getPreferredSize());
//
//        // If this position was previous filled by another component, same id, replace old component
//
//        mComponentMap.put(id, current);
//        mContainer.add(current);
//        mIsDirty = true;
//        return current;
//    }

    public List<JComponent> getContents() {
        Set<JComponent> visited = new HashSet<>();
        Queue<JComponent> toVisit = new LinkedList<>();
        List<JComponent> contents = new ArrayList<>();
        toVisit.add(this);

        while (!toVisit.isEmpty()) {
            JComponent parent = toVisit.poll();

            if (visited.contains(parent)) { continue; }
            visited.add(parent);

            // Actual workb
            boolean isChild = true;

            for (Component child : parent.getComponents()) {
                if (child == null) { continue; }
                if (!(child instanceof JComponent toEnqueue)) { continue; }
                toVisit.add(toEnqueue);
                isChild = false;
            }
            if (isChild && parent != this) { contents.add(parent);  }
        }

        return contents;
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        List<JComponent> contents = getContents();
        for (JComponent component : contents) {
            component.setBackground(color);
        }
    }

    public void clear() {
        mContainer.removeAll();
        mComponentMap.clear();
    }



    public static void updateKeyValueLabel(Tuple<String, OutlineLabel, OutlineLabel> row, String k, String v) {
        OutlineLabel leftLabel = row.second;
        if (!leftLabel.getText().equalsIgnoreCase(k)) {
            leftLabel.setText(k);
        }

        OutlineLabel rightLabel = row.third;
        if (!rightLabel.getText().equalsIgnoreCase(v)) {
            rightLabel.setText(v);
        }
    }

    public Tuple<String, OutlineLabel, OutlineLabel> putKeyValue(String key) {
        return putKeyValue(key, key, "");
    }

    public Tuple<String, OutlineLabel, OutlineLabel> putKeyValue(String key, String leftTxt, String rightTxt) {
        // Check if this pair already exists

        JComponent row = mComponentMap.get(key);
        OutlineLabel left;
        OutlineLabel right;
        boolean isValid = row != null && row.getComponentCount() == 2
                && row.getComponent(0) instanceof  OutlineLabel
                && row.getComponent(1) instanceof OutlineLabel;

        if (isValid) {
            left = (OutlineLabel) row.getComponent(0);
            right = (OutlineLabel) row.getComponent(1);

            if (left != null) { left.setText(leftTxt); }
            if (right != null) { right.setText(rightTxt); }

            return new Tuple<>(key, left, right);
        }
        row = new JGamePanel(true);
        row.setPreferredSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setMinimumSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setMaximumSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        // Create new key value field
        left = new OutlineLabel(leftTxt, SwingConstants.LEFT, 2, true);
        left.setOpaque(true);

        right = new OutlineLabel(rightTxt, SwingConstants.RIGHT, 2, true);
        right.setOpaque(true);

        row.add(left, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0;
        row.add(right, gridBagConstraints);

        mComponentMap.put(key + "___LABEL___", left);
        mComponentMap.put(key + "___VALUE___", right);
        mComponentMap.put(row, row);

        getOrPut(key, row);

        return new Tuple<>(key, left, right);
    }

    public Map<String, JComponent> putKeyValueLabelV2(String key, String leftTxt, String rightTxt) {
        // Check if this pair already exists
        JPanel row = (JGamePanel) mComponentMap.get(key);
        boolean isValid = row != null
                && row.getComponentCount() == 2
                && row.getComponent(0) instanceof  OutlineLabel
                && row.getComponent(1) instanceof OutlineLabel;
        OutlineLabel left;
        OutlineLabel right;
        if (row != null && isValid) {
            left = (OutlineLabel) row.getComponent(0);
            right = (OutlineLabel) row.getComponent(1);
            if (left.getText() == null) {
                left.setText(leftTxt);
            } else {
                if (!left.getText().equalsIgnoreCase(leftTxt)) { left.setText(leftTxt); }
            }

            if (right.getText() == null) {
                right.setText(rightTxt);
            } else {
                if (!right.getText().equalsIgnoreCase(rightTxt)) { right.setText(rightTxt); }
            }

            mReturnMapV2.clear();
            mReturnMapV2.put("left", left);
            mReturnMapV2.put("right", right);
            mReturnMapV2.put("container", row);
            return mReturnMapV2;
        }

        row = new JGamePanel(true);
        row.setPreferredSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setMinimumSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setMaximumSize(new Dimension(mComponentWidth, mComponentHeight));
        row.setLayout(new GridBagLayout());


        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        // Create new key value field
        left = new OutlineLabel(leftTxt, SwingConstants.LEFT, 2, true);
        left.setOpaque(true);

        right = new OutlineLabel(rightTxt, SwingConstants.RIGHT, 2, true);
        right.setOpaque(true);

        row.add(left, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0;
        row.add(right, gridBagConstraints);

        mComponentMap.put(key + "___LABEL___", left);
        mComponentMap.put(key + "___VALUE___", right);
        mComponentMap.put(row, row);

        getOrPut(key, row);

        mReturnMapV2.clear();
        mReturnMapV2.put("left", left);
        mReturnMapV2.put("right", right);
        mReturnMapV2.put("container", row);
        return mReturnMapV2;
    }

    public JComponent getComponent(String key) {
        return mComponentMap.get(key);
    }
    public void setVisibility(String key, boolean value) {
        JComponent component = mComponentMap.get(key);
        if (component == null) { return; }
        component.setVisible(value);
    }

    public void putPair(String name, JComponent left, JComponent right) {
        JPanel rowOrColumn = new JGamePanel();
        rowOrColumn.setPreferredSize(new Dimension(mComponentWidth, mComponentHeight));
        rowOrColumn.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        gridBagConstraints.weighty = 1;

        rowOrColumn.add(left, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1;
        rowOrColumn.add(right, gridBagConstraints);

        getOrPut(name, rowOrColumn);
    }

    public Map<String, Pair<OutlineLabel, OutlineLabel>> createPairPane(
            String[] keys, boolean isLeftRight, boolean defaultBorder) {
        Map<String, Pair<OutlineLabel, OutlineLabel>> keyValueMap = new LinkedHashMap<>();
        for (String key : keys) {
            OutlineLabel left = new OutlineLabel(
                    key,
                    isLeftRight ? SwingConstants.LEFT : SwingConstants.TOP,
                    2,
                    defaultBorder
            );
            left.setText(key);
            OutlineLabel right = new OutlineLabel(
                    key,
                    isLeftRight ? SwingConstants.RIGHT : SwingConstants.BOTTOM,
                    2,
                    defaultBorder
            );
            right.setText("<Value Here>");
            Pair<OutlineLabel, OutlineLabel> item = new Pair<>(left, right);
            keyValueMap.put(key, item);
            putPair(key, left, right);
        }
        return keyValueMap;
    }

    @Override
    public void setFont(Font font) {
        if (mContainer == null || mContainer.getFont() == font) { return; }
        for (Map.Entry<Object, JComponent> entry : mComponentMap.entrySet()) {
            entry.getValue().setFont(font);
        }
        super.setFont(font);
        mContainer.setFont(font);
    }


    public JPanel getContainer() { return mContainer; }
    public int getComponentWidth() { return mComponentWidth; }
    public int getComponentHeight() { return mComponentHeight; }
    public int getVisibleComponentsCount() { return mVisibleComponentsCount; }
    public int getRowCount() { return mComponentMap.size(); }
//    public JComponent get(int index) { return (JComponent) mContainer.getComponent(index); }
//    public int get
}
