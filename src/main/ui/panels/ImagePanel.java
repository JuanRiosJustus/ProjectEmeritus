package main.ui.panels;

import main.game.components.Animation;
import main.game.components.Identity;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.AssetPool;
import main.graphics.JScene;
import main.graphics.temporary.JImageLabel;
import main.ui.huds.controls.HUD;
import main.utils.ImageUtils;
import main.utils.StringFormatter;

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


        add(HUD.createScalingPane(width, height, container));
    }

    public void set2(Entity entity) {
        if (entity == null) { return; }
        Animation animation = entity.get(Animation.class);
        if (animation == null) { return; }
        Dimension dimension = content.getPreferredSize();
        BufferedImage image = ImageUtils.getResizedImage(animation.toImage(), dimension.width, dimension.height);
        content.setImage(image);
        observing = entity;
        Identity identity = entity.get(Identity.class);
        label.setText(identity.toString());
    }

    public void set(Entity entity) {
        if (observing == entity || entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            if (tile.getLiquid() > 0) {
                animation = AssetPool.getInstance().getAnimation(tile.getLiquidId());
            } else if (tile.getTerrain() > 0) {
                animation = AssetPool.getInstance().getAnimation(tile.getTerrainId());
            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
        }

        if (animation == null) { return; }

        Dimension dimension = content.getPreferredSize();
        BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
        content.setImage(image);
        observing = entity;
        label.setText(reference);


//        if (entity.get(Tile.class) != null) {
//            Tile tile = entity.get(Tile.class);
//            if (tile.getLiquid() > 0) {
//                Animation animation = AssetPool.getInstance().getAnimation(tile.getLiquidId());
//            } else if (tile.getTerrain() > 0) {
//                Animation animation = AssetPool.getInstance().getAnimation(tile.getTerrainId());
//            }
//
//            Dimension dimension = content.getPreferredSize();
//            BufferedImage image = ImageUtils.getResizedImage(tile.ge, dimension.width, dimension.height);
//            content.setImage(image);
//            observing = entity;
//            Identity identity = entity.get(Identity.class);
//            label.setText(identity.toString());
//        } else if (entity.get(Animation.class) != null) {
//            Animation animation = entity.get(Animation.class);
//            if (animation == null) { return; }
//            Dimension dimension = content.getPreferredSize();
//            BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
//            content.setImage(image);
//            observing = entity;
//            Identity identity = entity.get(Identity.class);
//            label.setText(identity.toString());
//        }

    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }

//    @Override
//    public void update(GameModel model) {
//
//    }
}
