package ui.panels;

import graphics.JScene;

import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class CarouselControlPanel extends JScene {

    public final JButton forward = new JButton("Forward");
    public final JButton backward = new JButton("backward");

    public CarouselControlPanel(int width, int height) {
        super(width, height, "Backward Forward");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.VERTICAL;

        add(getExitButton(), constraints);
//        add(forward, constraints);
    }

}
