package ui.panels;

import game.components.SpriteAnimation;
import game.components.Tile;
import game.entity.Entity;
import graphics.JScene;
import graphics.temporary.JImageLabel;

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

    public void set(Entity mousedAt) {
        if (observing == mousedAt || mousedAt == null) { return; }
        Tile details = mousedAt.get(Tile.class);
        if (details.unit != null) {
            SpriteAnimation spriteAnimation = details.unit.get(SpriteAnimation.class);
            jImageLabel.setImage(new ImageIcon(spriteAnimation.getFrame(0)));
            observing = details.unit;
            jImageLabel.setLabel(details.unit.toString());
        }

    }
}
