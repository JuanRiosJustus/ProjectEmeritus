package ui.panels;

import java.awt.Dimension;

import javax.swing.JPanel;

public class NullPanel extends JPanel {
    public NullPanel() {
        setLayout(null);
    }

    @Override
    public void setPreferredSize(Dimension dimension) {
        super.setPreferredSize(dimension);
    }
}
