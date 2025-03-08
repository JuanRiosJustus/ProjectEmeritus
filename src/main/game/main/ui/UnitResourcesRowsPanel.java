package main.game.main.ui;

import main.constants.Quadruple;
import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.custom.ResourceBar;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineLabel;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class UnitResourcesRowsPanel extends GameUI {

    private JPanel mContentPanel = null;
    private Color mResourceRowColor = null;
    private int mRowHeight = 0;
    private Map<String, Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar>> map = new LinkedHashMap<>();
    public UnitResourcesRowsPanel(int width, int height, Color background) {
        super(width, height);

        mContentPanel = new JPanel();
        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
//        mResourceRows.setPreferredSize(new Dimension(width, height));
//        mResourceRows.setMinimumSize(new Dimension(width, height));
//        mResourceRows.setMaximumSize(new Dimension(width, height));
        mContentPanel.setBackground(background.darker());
        mContentPanel.setOpaque(true);



        mRowHeight = (int) (height * .1);
        mResourceRowColor = mContentPanel.getBackground();

//        mContentPanel.add(Box.createRigidArea(new Dimension(0, mRowHeight)));
//        createResourceRow("Health");
//        createResourceRow("Mana");
//        createResourceRow("Stamina");

        setOpaque(false);
        add(new NoScrollBarPane(mContentPanel, width, height, true, 1));
    }

    public Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar> createResourceRow(String name) {

        Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar> components = map.get(name);

        if (components != null) {
            return components;
        } else {
            Random random = new Random();
            components = createResourceRow(name, random.nextInt(50, 2000) + "/" + random.nextInt(2000, 4000));
            map.put(name, components);
        }


        mContentPanel.add(components.getFirst());
        mContentPanel.add(Box.createRigidArea(new Dimension(0, mRowHeight)));

        return components;
    }
    private Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar> createResourceRow(String header, String value) {

        Quadruple<JPanel, OutlineLabel, OutlineLabel, ResourceBar> components = null;
        int resourceRowWidth = mWidth;
        int resourceRowHeight = (int) (mHeight * .9);
        Color color = mResourceRowColor;

        int labelWidth = resourceRowWidth;
        int labelHeight = (int) (resourceRowHeight * .3);
        OutlineLabel label = new OutlineLabel();
        label.setPreferredSize(new Dimension(labelWidth, labelHeight));
        label.setMinimumSize(new Dimension(labelWidth, labelHeight));
        label.setMaximumSize(new Dimension(labelWidth, labelHeight));
        label.setText(StringUtils.convertSnakeCaseToCapitalized(header));

        int labelValueWidth = resourceRowWidth;
        int labelValueHeight =  (int) (resourceRowHeight * .45);
        OutlineLabel labelValue = new OutlineLabel();
        labelValue.setPreferredSize(new Dimension(labelValueWidth, labelValueHeight));
        labelValue.setMinimumSize(new Dimension(labelValueWidth, labelValueHeight));
        labelValue.setMaximumSize(new Dimension(labelValueWidth, labelValueHeight));
        labelValue.setFont(FontPoolV1.getInstance().getFontForHeight((int) (labelValueHeight)));
        labelValue.setHorizontalTextPosition(SwingConstants.CENTER);
        labelValue.setText(value);


        int resourceBarWidth = (int) (resourceRowWidth);
        int resourceBarInnerWidth = (int) (resourceRowWidth * .9);
        int resourceBarHeight = (int) (resourceRowHeight * .35);
        ResourceBar resourceBar = new ResourceBar(resourceBarInnerWidth, resourceBarHeight, Color.WHITE, Color.YELLOW, 1);
        resourceBar.setResourceLabelVisible(false);

        JPanel panel = new GameUI();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(resourceRowWidth, resourceRowHeight));
//        panel.setMinimumSize(new Dimension(width, height));
//        panel.setMaximumSize(new Dimension(width, height));
        panel.setBackground(color);

        components = new Quadruple<>(panel, label, labelValue, resourceBar);

        // Health header
        panel.add(SwingUiUtils.createWrapperJPanelLeftAlign(labelWidth, labelHeight, label));
        panel.add(SwingUiUtils.createWrapperJPanelLeftAlign(labelValueWidth, labelValueHeight, labelValue));
        panel.add(SwingUiUtils.createWrapperJPanelLeftAlign(resourceBarWidth, resourceBarHeight, resourceBar));

        return components;
    }
}
