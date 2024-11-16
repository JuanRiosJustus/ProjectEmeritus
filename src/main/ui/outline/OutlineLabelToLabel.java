package main.ui.outline;

import main.game.stores.pools.FontPool;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;

public class OutlineLabelToLabel extends JPanel {
    private JLabel mLeftLabel = new OutlineLabel("");
    private JLabel mRightLabel = new OutlineLabel("");
//    public OutlineLabelToLabel() { }
    public OutlineLabelToLabel(int width, int height) { this("", width, height); }
    public OutlineLabelToLabel(String str, int width, int height) { this(str, "", width, height); }
    public OutlineLabelToLabel(String str, Color color, int width, int height) {
        this(str, "", color,  width, height);
    }
    public OutlineLabelToLabel(String left, String right, int width, int height) {
        this(left, right, Color.WHITE, width, height);
    }
    public OutlineLabelToLabel(String left, String right, Color color, int width, int height) {
        setLayout(new BorderLayout());
//        setLayout(new FlowLayout(FlowLayout.L));
        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);
        removeAll();

        int fontHeight = (int)(height * .9);

//        leftLabel = new OutlineLabel();
        mLeftLabel = new OutlineLabel();
        mLeftLabel.setText(left);
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
//        leftLabel.setPreferredSize(new Dimension((int) (width * .75), height));
//        leftLabel.setMinimumSize(leftLabel.getPreferredSize());
//        leftLabel.setMaximumSize(leftLabel.getPreferredSize());
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mLeftLabel.setBackground(color);
//        leftLabel.setBackground(ColorPalette.getRandomColor());
//        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());

//        rightLabel = new OutlineLabel();
        mRightLabel = new OutlineLabel();
        mRightLabel.setText(right);
        mRightLabel.setHorizontalAlignment(JTextField.RIGHT);
//        rightLabel.setPreferredSize(new Dimension((int) (width  * .25), height));
        mRightLabel.setFont(FontPool.getInstance().getFontForHeight(fontHeight));
        mRightLabel.setBackground(color);
//        currentTileHeightField.setBackground(keyValuePanel.getBackground());


        add(mLeftLabel, BorderLayout.WEST);
        add(mRightLabel, BorderLayout.CENTER);
    }
    public void setLeftLabel(String str) {
        mLeftLabel.setText(str);
    }
    public void setRightLabel(String str) {
        mRightLabel.setText(str);
    }
    public void setFont(Font font) {
        if (mLeftLabel != null) {
            mLeftLabel.setFont(font);
        }
        if (mRightLabel != null) {
            mRightLabel.setFont(font);
        }
    }
}
