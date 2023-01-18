package ui.panels;

import game.components.Animation;
import game.entity.Entity;
import graphics.JScene;
import graphics.temporary.JImageLabel;
import utils.ImageUtils;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

public class SelectionPanel extends JScene {

    private final BufferedImage image = null;
    private final JImageLabel jImageLabel = new JImageLabel(null, "");
    private Entity observing = null;

    public SelectionPanel(int width, int height) {
        super(width, height, "Selection");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        JPanel panel = new JPanel();
        panel.add(jImageLabel);

        add(panel, gbc);
    }

    public void set(Entity entity) {
        if (observing == entity || entity == null) { return; }
        Animation animation = entity.get(Animation.class);
        jImageLabel.setImage(new ImageIcon(ImageUtils.getResizedImage(animation.getFrame(0), 100, 100)));
//        jImageLabel.setLabel(entity.toString());
        observing = entity;

    }
}
