package main.graphics.temporary;

import javax.swing.*;

import main.constants.ColorPalette;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class JImage extends JPanel {

    // protected final JLabel image = new JLabel();
    protected final JButton image = new JButton();
    protected final JButton descriptor = new JButton();
    // public final JLabel descriptor = new JLabel();
    // protected final JToggleButton descriptor = new JToggleButton("Yyooo");

    public JImage(ImageIcon imageIcon) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        image.setIcon(imageIcon);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.insets = new Insets(7, 0, 0, 0);
        add(image, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(descriptor, gbc);
    }

    public void setImage(ImageIcon newImage) { image.setIcon(newImage); }
    public void setText(String txt) { descriptor.setText(txt); }
    public void setAction(ActionListener e) { descriptor.addActionListener(e); }
    public void removeAllListeners() {
        for (ActionListener listener : descriptor.getActionListeners()) {
            descriptor.removeActionListener(listener);
        }
    }
    public void silenceButton() {
        descriptor.setDoubleBuffered(true);
        descriptor.setFocusPainted(false);
        descriptor.setBorderPainted(false);
        // descriptor.addActionListener(e -> {});
        // descriptor.setDoubleBuffered(true);
        // descriptor.setContentAreaFilled(false);
        // TODO this makes the button text white, do we want this
        descriptor.setForeground(ColorPalette.WHITE);
        descriptor.setBackground(ColorPalette.TRANSPARENT_BLACK_V1);
    }
}
