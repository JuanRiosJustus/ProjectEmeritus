package main.graphics.temporary;

import main.constants.Constants;

import javax.swing.*;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class JImage extends JPanel {
    protected final AbstractButton mContainer = new JButton();

    protected final AbstractButton mDescriptor = new JButton();
    protected final JPanel mBottomValue = new JPanel();
    protected BufferedImage mBlank = null;
    protected BufferedImage mImage = null;
    protected String mText = null;

    public JImage() {
        this(new BufferedImage(
                Constants.CURRENT_SPRITE_SIZE, Constants.CURRENT_SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB),
                true);
    }

    public JImage(BufferedImage bi) {
        this(bi, true);
    }

    public JImage(BufferedImage bi, boolean useLabel) {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        mImage = bi;
        mBlank = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
        mContainer.setIcon(new ImageIcon(bi));
        mContainer.setFocusPainted(false);
        mContainer.setBorderPainted(true);

        gbc.gridy = 0;
        gbc.gridx = 0;
        if (!useLabel) {
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
        }
        gbc.insets = new Insets(0, 0, 0, 0);
        add(mContainer, gbc);

        if (useLabel) {
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            mDescriptor.setVerticalTextPosition(JButton.TOP);
            mDescriptor.setHorizontalTextPosition(JButton.CENTER);
            mBottomValue.add(mDescriptor);
            add(mBottomValue, gbc);
        }
    }


    public AbstractButton getDescriptor() { return mDescriptor; }
    public AbstractButton getImageContainer() { return mContainer; }
    public void setBottomContent(JComponent content) { mBottomValue.removeAll(); mBottomValue.add(content); }
    public void setBufferedImage(BufferedImage bufferedImage) {
        mImage = bufferedImage;
        mBlank = new BufferedImage(mImage.getWidth(), mImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        mContainer.setIcon(new ImageIcon(mImage));
    }
    public void silence() {
        silence(true, false);
    }
    public void unsilence() {
        silence(false, false);
    }

    public void setText(String txt) {
        mText = txt;
        mDescriptor.setText(mText);
    }
    public void setAction(ActionListener e) { mDescriptor.addActionListener(e); }
    public void removeAllListeners() {
        for (ActionListener listener : mDescriptor.getActionListeners()) {
            mDescriptor.removeActionListener(listener);
        }
    }
    public void silence(boolean toSilence, boolean hideDescriptor) {

        if (toSilence) {
            mContainer.setIcon(new ImageIcon(mBlank));
        } else {
            mContainer.setIcon(new ImageIcon(mImage));
        }


        boolean painted = !toSilence;

        mContainer.setFocusPainted(painted);
        mContainer.setBorderPainted(painted);

        mDescriptor.setFocusPainted(painted);
        mDescriptor.setBorderPainted(painted);

        if (toSilence) {
            mDescriptor.setText(" ");
        } else {
            mDescriptor.setText(mText);
        }
        if (hideDescriptor) {
            mDescriptor.setText("");
            mDescriptor.setVisible(false);
        }

        mDescriptor.setVisible(!hideDescriptor);
    }
    public void hideDescriptor() {
        silence(false, true);
    }
    public void darken() {
        setBackground(getBackground().darker());
        setForeground(getForeground().darker());
    }

    public void lighten() {
        setBackground(getBackground().brighter());
        setForeground(getForeground().brighter());
    }
//    public void setBackground(Color color) {
//        super.setBackground(color);
//    }
}
