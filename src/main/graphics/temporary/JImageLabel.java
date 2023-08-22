package main.graphics.temporary;

import main.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class JImageLabel extends JPanel {

    public JLabel image;
    public JLabel label;

//    public JImageLabel(ImageIcon imageIcon, String value) {
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        image = new JLabel();
//        image.setIcon(imageIcon);
//        add(image);
//        label = new JLabel(value);
//        add(label);
//    }

    public JImageLabel(int width, int height) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        image = new JLabel();
        add(image);
        label = new JLabel("[Text Here]");
//        add(label);
//        setPreferredSize(width, height);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public void setImage(BufferedImage toResizeAndUse) {
        Dimension dim = getPreferredSize();
        int width = (int) (dim.getWidth() * .9);
        int height = (int) (dim.getHeight() * .9);
        BufferedImage resized = ImageUtils.getResizedImage(toResizeAndUse, width, height);
        image.setIcon(new ImageIcon(resized));
    }
    public void setText(String value) {
        label.setText(value);
    }
}
