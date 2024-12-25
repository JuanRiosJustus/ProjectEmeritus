package main.ui.outline.production.labels;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineLabel;
import main.ui.outline.production.OutlineTextSliderUI;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineSliderWithNorthWestLabel extends GameUI {

    protected OutlineLabel mLabel = null;
    protected JSlider mSlider = null;
    protected String mHelperText;
    public OutlineSliderWithNorthWestLabel(Color color, int width, int height, String helperText) {
        super(width, height);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);

        mHelperText = helperText;

        int labelWidth = width;
        int labelHeight = (int) (height * .4);
        mLabel = new OutlineLabel();
        mLabel.setOutlineThickness(1);
        mLabel.setBackground(color);
        mLabel.setText(helperText);
        mLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMinimumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMaximumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        mLabel.setAlignmentX(SwingConstants.LEFT);
        mLabel.setFont(FontPool.getInstance().getFontForHeight(labelHeight));

        JPanel labelPanel = new GameUI();
        labelPanel.setBackground(color);
        labelPanel.setLayout(new BorderLayout());
        labelPanel.add(mLabel, BorderLayout.CENTER);

        int componentWidth = width;
        int componentHeight = height - labelHeight;
        mSlider = new JSlider();
        mSlider.setMajorTickSpacing(20);
        mSlider.setMinorTickSpacing(5);
        mSlider.setPaintLabels(true);
        mSlider.setMaximum(50);
        mSlider.setMinimum(0);
//        mSlider.setPaintTicks(true);
//        slider.setPaintTrack(true);
        mSlider.setBackground(color);
        mSlider.setFont(FontPool.getInstance().getFontForHeight((int) (componentHeight * .75)));
        mSlider.setForeground(Color.WHITE);
        mSlider.setPreferredSize(new Dimension(componentWidth, componentHeight));
        mSlider.setMinimumSize(new Dimension(componentWidth, componentHeight));
        mSlider.setMaximumSize(new Dimension(componentWidth, componentHeight));
        mSlider.setBorder(new EmptyBorder(5, 5, 5, 5));

//        mSlider.addItem("THIS IS THE TEXT VALUES");
//        mSlider.addItem("THIS IS THE TEXT VALUES");


        add(labelPanel);
        add(mSlider);
//        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public int getValue() { return mSlider.getValue(); }
    public void setValue(int value) {
        if (value > mSlider.getMaximum()) {
            mSlider.setValue(mSlider.getMaximum());
        }

        if (value < mSlider.getMinimum()) {
            mSlider.setValue(mSlider.getMinimum());
        }

        mSlider.setValue(value);
    }

    public int getMaximum() { return mSlider.getMaximum(); }
    public int getMinimum() { return mSlider.getMinimum(); }
    public JSlider getSlider() { return mSlider; }

    public void addAdditionalContext(String value) { mLabel.setText(mHelperText + " - " + value); }
}
