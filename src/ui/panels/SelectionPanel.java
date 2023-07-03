package ui.panels;

import game.components.Animation;
import game.entity.Entity;
import graphics.JScene;
import graphics.temporary.JImageLabel;
import utils.ImageUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

public class SelectionPanel extends JScene {
    private final JImageLabel content;
    private Entity observing = null;

    public SelectionPanel(int width, int height) {
        super(width, height, SelectionPanel.class.getSimpleName());

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        content = new JImageLabel(width, height);

        add(content, gbc);
    }

    public void set(Entity entity) {
        if (observing == entity || entity == null) { return; }
        Animation animation = entity.get(Animation.class);
        Dimension dimension = content.getPreferredSize();
        BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
        content.setImage(image);
        observing = entity;

    }
}
