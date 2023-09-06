package main.ui.custom;

import main.constants.Constants;
import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.AssetPool;
import main.graphics.JScene;
import main.graphics.temporary.JImageLabel;
import main.utils.ImageUtils;
import main.utils.StringFormatter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import static main.ui.huds.controls.HUD.createScalingPane;

public class ImagePanel extends JScene {

    protected final JImageLabel imageLabel;
    protected final JKeyValue row1;
    protected final JKeyValue row2;
    protected final JKeyValue row3;
    protected final JKeyValue row4;
    protected Entity observing = null;
    protected JPanel container;

    public ImagePanel(int width, int height) {
        super(width, height, ImagePanel.class.getSimpleName());

        container = new JPanel();
        container.setBorder(new EmptyBorder(0, 5, 0, 5));
        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new GridBagLayout());


        int size = (int) (Math.min(width, height) * .7);
        imageLabel = new JImageLabel(size, size);
        imageLabel.setImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        imageLabel.setPreferredSize(new Dimension(size, size));
//        imageLabel.setOpaque(false);
//        imageLabel.setOpaque(true);
//        imageLabel.setBackground(ColorPalette.getRandomColor());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        container.add(imageLabel, gbc);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        int infoWidth = (int) (width - imageLabel.getPreferredSize().getWidth());
        infoPanel.setPreferredSize(new Dimension(infoWidth, size));
//        infoPanel.setOpaque(false);
//        infoPanel.setOpaque(true);
//        infoPanel.setBackground(Color.RED);
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.weightx = 1;
        gbc2.weighty = 1;
        gbc2.gridx = 0;
        gbc2.anchor =  GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        int rows = 4;

        row1 = new JKeyValue(infoWidth, size / rows, "Row 1");
        infoPanel.add(row1, gbc2);

        gbc2.gridy = 1;
        row2 = new JKeyValue(infoWidth, size / rows, "Row 2");
        infoPanel.add(row2, gbc2);

        gbc2.gridy = 2;
        row3 = new JKeyValue(infoWidth, size / rows, "Row 3");
        infoPanel.add(row3, gbc2);

        gbc2.gridy = 3;
        row4 = new JKeyValue(infoWidth, size / rows, "Row 4");
        infoPanel.add(row4, gbc2);



        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        container.add(infoPanel, gbc);

//        container.setOpaque(false);
//        container.setBackground(ColorPalette.GREEN);
//        container.setOpaque(true);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
//        row4 = new JKeyValue(width, (int) (height * .5), "DEF");
        JComboBox<String> sss = new JComboBox<>();
//        sss.addItem("TEST - TEST - TEST - TEST");
//        sss.addItem("tttttt");
//        row4.setOpaque(true);
//        row4.setBackground(ColorPalette.getRandomColor());

//        sss.getEditor().
        container.add(sss, gbc);
//        container.add(row4, gbc);

        add(createScalingPane(width, height, container));
    }

    public void set(Entity entity) {
        if (observing == entity || entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        int setupType = 0;
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            if (tile.getLiquid() > 0) {
                animation = AssetPool.getInstance().getAsset(tile.getLiquidAssetId());
            } else if (tile.getTerrain() > 0) {
                animation = AssetPool.getInstance().getAsset(tile.getTerrainAssetId());
            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
            setupType = 3;
        }

        if (animation == null) { return; }

        Dimension dimension = imageLabel.getPreferredSize();
        BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
        imageLabel.setImage(image);
        observing = entity;
        setup(entity, setupType);
//        label.setLabel(reference);
    }
    private void setup(Entity entity, int type) {
        if (type == 3) {

            Summary summary = entity.get(Summary.class);
            Types typing = entity.get(Types.class);
            int currentXP = summary.getStatCurrent(Constants.EXPERIENCE);
            int maxXP = summary.getStatTotal(Constants.EXPERIENCE);
            int level = summary.getStatTotal(Constants.LEVEL);
            row1.setKeyAndValue("Lvl " + level, currentXP + " / " + maxXP);

            int currentHP = summary.getStatCurrent(Constants.HEALTH);
            int maxHP = summary.getStatTotal(Constants.HEALTH);
            row3.setKeyAndValue("Health", currentHP + " / " + maxHP);

            int currentEP = summary.getStatCurrent(Constants.ENERGY);
            int maxEP = summary.getStatTotal(Constants.ENERGY);
            row4.setKeyAndValue("Energy", currentEP + " / " + maxEP);

            Tags tags = entity.get(Tags.class);
            row2.setKeyAndValue(entity.toString(), "");

//            int currentHP = summary.getStatCurrent(Constants.HEALTH);
//            int maxHP = summary.getStatTotal(Constants.HEALTH);
//            row4.setKeyAndValue("HP", currentHP + " / " + maxHP);
//            row3.setVisible(false);
//            row4.setVisible(false);
//            row1.setKey("");
//            row2.setVisible(false);
//            row3.setVisible(false);
//            row4.setVisible(false);
        }
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
