package graphics.temporary;

import javax.swing.*;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

public class JImage extends JPanel {

    protected final JLabel label = new JLabel();
    protected final JButton descriptor = new JButton();

    public JImage(ImageIcon imageIcon) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        label.setIcon(imageIcon);
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(label, gbc);
        gbc.gridy = 1;
        add(descriptor, gbc);

    }

    public void setImage(ImageIcon image) { label.setIcon(image); }
    public void setText(String txt) { descriptor.setText(txt); }
    public void setAction(ActionListener e) { descriptor.addActionListener(e); }
    public void removeAllListeners() {
        for (ActionListener listener : descriptor.getActionListeners()) {
            descriptor.removeActionListener(listener);
        }
    }
    public void silenceButton() {
        descriptor.setFocusPainted(false);
        descriptor.setBorderPainted(false);
        // TODO this makes the button text white, do we want this
        descriptor.setForeground(Color.WHITE);
    }
}
