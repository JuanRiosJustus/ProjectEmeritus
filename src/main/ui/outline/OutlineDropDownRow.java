package main.ui.outline;

import main.constants.SimpleCheckSum;
import main.game.stores.pools.FontPool;
import main.ui.custom.StringComboBox;
import main.ui.outline.production.core.OutlineComboBox;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class OutlineDropDownRow extends JPanel {

    private JLabel mLeftLabel = new OutlineLabel();
    private OutlineComboBox mRightDropDown = new OutlineComboBox();
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    public OutlineDropDownRow(Color color, int width, int height) {
        this("", color, width, height);
    }

    public OutlineDropDownRow(String leftLabel, Color color, int width, int height) {
//        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(width, height));
        setBackground(color);

        mLeftLabel = new OutlineLabel(leftLabel);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setBackground(color);
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));

        mRightDropDown = new OutlineComboBox(color, height);

        add(mLeftLabel, BorderLayout.WEST);
        add(mRightDropDown, BorderLayout.CENTER);
    }

//    public void setup(String leftLabel, Color color, int width, int height) {
////        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        setLayout(new BorderLayout());
//        setPreferredSize(new Dimension(width, height));
//        setBackground(color);
//        removeAll();
//
//        mLeftLabel = new OutlineLabel(leftLabel);
//        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
//        mLeftLabel.setBackground(color);
//        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
//
//        mRightDropDown = new OutlineComboBox(color, height);
//
//        add(mLeftLabel, BorderLayout.WEST);
//        add(mRightDropDown, BorderLayout.CENTER);
//    }

    public void setLeftLabel(String str) {
        if (!mSimpleCheckSum.isUpdated("left", str)) { return; }
        mLeftLabel.setText(str);
    }
    public void addItem(String str) {
        if (!mSimpleCheckSum.isUpdated("right", str)) { return; }
        mRightDropDown.addItem(str);
    }
    public String getSelectedItem() { return mRightDropDown.getSelectedItem(); }
    public void setSelectedIndex(int index) { mRightDropDown.setSelectedIndex(index); }
    public void addActionListener(ActionListener l) {
        mRightDropDown.addActionListener(l);
    }
    public StringComboBox getDropDown() { return mRightDropDown; }
}
