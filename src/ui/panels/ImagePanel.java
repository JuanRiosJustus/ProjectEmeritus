package ui.panels;

import game.components.Animation;
import game.components.statistics.Summary;
import game.entity.Entity;
import graphics.JScene;
import graphics.temporary.JImageLabel;
import utils.ImageUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ImagePanel extends JScene {

    private final JImageLabel content;
    private final JButton label;
    private Entity observing = null;

    public ImagePanel(int width, int height) {
        super(width, height, ImagePanel.class.getSimpleName());

        JPanel container = new JPanel();
        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        content = new JImageLabel(width, (int) (height * .8));
        container.add(content, gbc);
        
        label = new JButton("");
        label.setFocusPainted(false);
        container.add(label);


        // setLayout(new GridBagLayout());

        // GridBagConstraints gbc = new GridBagConstraints();
        // gbc.gridwidth = GridBagConstraints.REMAINDER;
        // gbc.fill = GridBagConstraints.VERTICAL;

        // content = new JImageLabel(width, (int) (height * .8));
        // add(content, gbc);
        
        // label = new JButton("");
        // label.setFocusPainted(false);
        // // label.setBorderPainted(false);
        // add(label);


        JScrollPane scrollPane = new JScrollPane(container,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setPreferredSize(getPreferredSize());
        scrollPane.setPreferredSize(getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane);
    }

    public void set(Entity entity) {
        if (observing == entity || entity == null) { return; }
        Animation animation = entity.get(Animation.class);
        if (animation == null) { return; }
        Dimension dimension = content.getPreferredSize();
        BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
        content.setImage(image);
        observing = entity;
        Summary summary = entity.get(Summary.class);
        label.setText(summary.getName());
    }
}
