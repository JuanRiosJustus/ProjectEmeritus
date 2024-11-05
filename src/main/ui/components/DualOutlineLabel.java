package main.ui.components;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class DualOutlineLabel extends JPanel {
    private final OutlineLabel mLeftOutlineLabel;
    private final OutlineLabel mRightOutlineLabel;

    public DualOutlineLabel() { this(1); }
    public DualOutlineLabel(int textThickness) {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.weightx = 1;

//        mLeftOutlineLabel.setBackground(Color.RED);
        gridBagConstraints.gridx = 0;
        mLeftOutlineLabel = new OutlineLabel(textThickness);
        mLeftOutlineLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(mLeftOutlineLabel, gridBagConstraints);

//        mRightOutlineLabel.setBackground(Color.GREEN);
        gridBagConstraints.gridx = 1;
        mRightOutlineLabel = new OutlineLabel(textThickness);
        mRightOutlineLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(mRightOutlineLabel, gridBagConstraints);
    }
    public void setLeftLabel(String txt) { mLeftOutlineLabel.setText(txt); }
    public void setRightLabel(String txt) { mRightOutlineLabel.setText(txt); }
    public void setLabelFonts(Font font) {
        mLeftOutlineLabel.setFont(font);
        mRightOutlineLabel.setFont(font);
    }
    public String getRightLabelText() { return mRightOutlineLabel.getText(); }
    public String getLeftLabelText() { return mLeftOutlineLabel.getText(); }
    public OutlineLabel getLeftOutlineLabel() { return mLeftOutlineLabel; }
    public OutlineLabel getRightOutlineLabel() { return mRightOutlineLabel; }
}
