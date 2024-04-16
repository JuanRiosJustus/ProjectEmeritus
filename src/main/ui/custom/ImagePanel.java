//package main.ui.custom;
//
//import main.game.components.*;
//import main.game.components.tile.Tile;
//import main.game.entity.Entity;
//import main.game.main.GameModel;
//import main.game.stores.pools.asset.AssetPool;
//import main.graphics.JScene;
//import main.graphics.temporary.JImageLabel;
//import main.utils.ImageUtils;
//import main.utils.StringFormatter;
//
//import java.awt.Dimension;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.image.BufferedImage;
//
//import javax.swing.JComboBox;
//import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;
//
//import static main.ui.huds.controls.HUD.createScalingPane;
//
//public class ImagePanel extends JScene {
//
//    protected final JImageLabel imageLabel;
//    protected final JKeyValue row1;
//    protected final JKeyValue row2;
//    protected final JKeyValue row3;
//    protected final JKeyValue row4;
//    protected Entity observing = null;
//    protected JPanel container;
//
//    public ImagePanel(int width, int height) {
//        super(width, height, ImagePanel.class.getSimpleName());
//
//        container = new JPanel();
//        container.setBorder(new EmptyBorder(0, 5, 0, 5));
//        container.setPreferredSize(new Dimension(width, height));
//        container.setLayout(new GridBagLayout());
//
//        int size = (int) (Math.min(width, height) * .7);
//        imageLabel = new JImageLabel(size, size);
//        imageLabel.setImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
//        imageLabel.setPreferredSize(new Dimension(size, size));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.weightx = 0;
//        gbc.weighty = 1;
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.anchor =  GridBagConstraints.NORTHWEST;
//        container.add(imageLabel, gbc);
//
//        JPanel infoPanel = new JPanel(new GridBagLayout());
//        int infoWidth = (int) (width - imageLabel.getPreferredSize().getWidth());
//        infoPanel.setPreferredSize(new Dimension(infoWidth, size));
//        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//
//        GridBagConstraints gbc2 = new GridBagConstraints();
//        gbc2.weightx = 1;
//        gbc2.weighty = 1;
//        gbc2.gridx = 0;
//        gbc2.anchor =  GridBagConstraints.NORTHWEST;
//        gbc2.fill = GridBagConstraints.BOTH;
//        int rows = 4;
//
//        row1 = new JKeyValue(infoWidth, size / rows, "Row 1");
//        infoPanel.add(row1, gbc2);
//
//        gbc2.gridy = 1;
//        row2 = new JKeyValue(infoWidth, size / rows, "Row 2");
//        infoPanel.add(row2, gbc2);
//
//        gbc2.gridy = 2;
//        row3 = new JKeyValue(infoWidth, size / rows, "Row 3");
//        infoPanel.add(row3, gbc2);
//
//        gbc2.gridy = 3;
//        row4 = new JKeyValue(infoWidth, size / rows, "Row 4");
//        infoPanel.add(row4, gbc2);
//
//
//
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.anchor =  GridBagConstraints.NORTHWEST;
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//        gbc.weightx = 1;
//        container.add(infoPanel, gbc);
//
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.anchor =  GridBagConstraints.NORTHWEST;
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 2;
//        gbc.weightx = 1;
////        row4 = new JKeyValue(width, (int) (height * .5), "DEF");
//        JComboBox<String> sss = SwingUiUtils.getComboBox();
////        sss.addItem("TEST - TEST - TEST - TEST");
////        sss.addItem("tttttt");
////        row4.setOpaque(true);
////        row4.setBackground(ColorPalette.getRandomColor());
//
////        sss.getEditor().
//        container.add(sss, gbc);
////        container.add(row4, gbc);
//
//        add(createScalingPane(width, height, container));
//    }
//
//    public void set(Entity entity) {
//        if (observing == entity || entity == null) { return; }
//        Animation animation = null;
//        String reference = entity.toString();
//        int setupType = 0;
//        if (entity.get(Tile.class) != null) {
//            Tile tile = entity.get(Tile.class);
//            if (tile.getLiquid() > 0) {
//                animation = AssetPool.getInstance().getAssetAnimation(tile.getLiquidAssetId());
//            } else if (tile.getTerrain() > 0) {
//                animation = AssetPool.getInstance().getAssetAnimation(tile.getTerrainAssetId());
//            }
//            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
//        } else if (entity.get(Animation.class) != null) {
//            animation = entity.get(Animation.class);
//            setupType = 3;
//        }
//
//        if (animation == null) { return; }
//
//        Dimension dimension = imageLabel.getPreferredSize();
//        BufferedImage image = ImageUtils.getResizedImage(animation.getFrame(0), dimension.width, dimension.height);
//        imageLabel.setImage(image);
//        observing = entity;
//        setup(entity, setupType);
////        label.setLabel(reference);
//    }
//    private void setup(Entity entity, int type) {
//        if (type == 3) {
//
//            Summary summary = entity.get(Summary.class);
////            Types typing = entity.get(Types.class);
//            int currentXP = summary.getStatCurrent(Summary.EXPERIENCE);
//            int maxXP = summary.getStatTotal(Summary.EXPERIENCE);
//            int level = summary.getStatTotal(Summary.LEVEL);
//            row1.setKeyAndValue("Lvl " + level, currentXP + " / " + maxXP);
//
//            int currentHP = summary.getStatCurrent(Summary.HEALTH);
//            int maxHP = summary.getStatTotal(Summary.HEALTH);
//            row3.setKeyAndValue("Health", currentHP + " / " + maxHP);
//
//            int currentMP = summary.getStatCurrent(Summary.MANA);
//            int maxMP = summary.getStatTotal(Summary.MANA);
//            row4.setKeyAndValue("Mana", currentMP + " / " + maxMP);
//
//            Tags tags = entity.get(Tags.class);
//            row2.setKeyAndValue(entity.toString(), "");
//
////            int currentHP = summary.getStatCurrent(Constants.HEALTH);
////            int maxHP = summary.getStatTotal(Constants.HEALTH);
////            row4.setKeyAndValue("HP", currentHP + " / " + maxHP);
////            row3.setVisible(false);
////            row4.setVisible(false);
////            row1.setKey("");
////            row2.setVisible(false);
////            row3.setVisible(false);
////            row4.setVisible(false);
//        }
//    }
//
//    @Override
//    public void jSceneUpdate(GameModel model) {
//
//    }
//}


package main.ui.custom;

import main.constants.Constants;
import main.constants.GameState;
import main.game.components.*;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.temporary.JImageLabel;
import main.ui.huds.controls.HUD;
import main.utils.ImageUtils;
import main.utils.StringFormatter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ImagePanel extends HUD {

    protected final JImageLabel mImageLabel;
    protected final DatasheetPanel mKeyValueMap;
    protected final JComboBox<String> mComboBox;
    private final Map<String, Integer> mHashStates = new HashMap<>();
    private int mHistoryState = 0;
    private BufferedImage mCurrentImage = null;
    protected Entity observing = null;
    protected JPanel container;

    public ImagePanel(int width, int height) {
        super(width, height, ImagePanel.class.getSimpleName());

        container = new JPanel();
        container.setBorder(new EmptyBorder(0, 5, 0, 5));
        container.setPreferredSize(new Dimension(width, height));
        container.setLayout(new GridBagLayout());

        int imageSize = (int) (Math.min(width, height) * .7);
        mImageLabel = new JImageLabel(imageSize, imageSize);
        mImageLabel.setImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        mImageLabel.setPreferredSize(new Dimension(imageSize, imageSize));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        container.add(mImageLabel, gbc);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.weightx = 1;
        gbc2.weighty = 1;
        gbc2.gridx = 0;
        gbc2.anchor =  GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;


        mKeyValueMap = new DatasheetPanel(
                width - imageSize - 10,
                height - imageSize,
                new Object[][]{
                        new Object[]{ Constants.NAME, new JLabel() },
                        new Object[]{ Statistics.LEVEL, new JLabel() },
                        new Object[]{ Statistics.HEALTH, new JLabel() },
                        new Object[]{ Statistics.TYPES, new JLabel() },
                }
        );

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        container.add(mKeyValueMap, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        mComboBox = SwingUiUtils.getComboBoxLeftAligned();

        container.add(mComboBox, gbc);

        setOpaque(true);
        setBackground(ColorPalette.TRANSPARENT);

        container.setOpaque(true);
        container.setBackground(ColorPalette.TRANSPARENT);

        add(createScalingPane(width, height, container));
    }

    public void set(Entity entity) {
        if (entity == null) { return; }
        Animation animation = null;
        String reference = entity.toString();
        int setupType = 0;
        if (entity.get(Tile.class) != null) {
            Tile tile = entity.get(Tile.class);
            if (tile.getLiquid() != null) {
                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
            } else if (tile.getTerrain() != null) {
                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
            }
//            if (tile.getLiquid() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
//            } else if (tile.getTerrain() != null) {
//                animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
//            }
            reference = StringFormatter.format("Row: {}, Col: {}", tile.row, tile.column);
        } else if (entity.get(Animation.class) != null) {
            animation = entity.get(Animation.class);
            setupType = 3;
        }
//
//        if (animation == null) { return; }

        if (animation != null && mCurrentImage != animation.getFrame(0)) {
            mCurrentImage = animation.getFrame(0);
            Dimension dimension = mImageLabel.getPreferredSize();
            BufferedImage image = ImageUtils.getResizedImage(mCurrentImage, dimension.width, dimension.height);
            mImageLabel.setImage(image);
        }

        observing = entity;
        setup(entity, setupType);
    }

    public void setLabelIfDifferent(String key, String value) {
        if (!DatasheetPanel.getJLabelComponent(mKeyValueMap, key).getText().equals(value)) {
            DatasheetPanel.getJLabelComponent(mKeyValueMap, key).setText(value);
        }
    }
    private void setup(Entity entity, int type) {
        if (type == 3) {

            Statistics statistics = entity.get(Statistics.class);
            int currentXP = statistics.getStatModified(Statistics.LEVEL);
            int level = statistics.getStatBase(Statistics.LEVEL);
            int maxXP = Statistics.getExperienceNeeded(level);

            int currentHP = statistics.getStatCurrent(Statistics.HEALTH);
            int maxHP = statistics.getStatTotal(Statistics.HEALTH);

            setLabelIfDifferent(Constants.NAME, entity.toString());

            String lvlTxt = "Lvl " + level;
            if (!DatasheetPanel.getJLabelLabelComponent(mKeyValueMap, Constants.LEVEL).getText().equals(lvlTxt)) {
                DatasheetPanel.getJLabelLabelComponent(mKeyValueMap, Constants.LEVEL).setText(lvlTxt);
            }

            setLabelIfDifferent(Constants.NAME, entity.toString());
            setLabelIfDifferent(Constants.LEVEL, currentXP + " / " + maxXP);
            setLabelIfDifferent(Statistics.HEALTH, currentHP + " / " + maxHP);
            setLabelIfDifferent(Statistics.TYPES, statistics.getType().toString());

        }
        History history = entity.get(History.class);
        if (mHistoryState != history.getHashState()) {
            mHistoryState = history.getHashState();
            mComboBox.removeAllItems();
            int index = 0;
            for (String event : history.getLogs()) {
                mComboBox.addItem((index == 0 ? "(LATEST) " : "(" + index + ") ") + event);
                index++;
            }
        }
    }

    private Entity lastSelected;
    private Entity currentSelected;
    @Override
    public void jSceneUpdate(GameModel model) {
        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        if (currentSelected != null) {
            Tile tile = currentSelected.get(Tile.class);
            Entity unit = tile.getUnit();
            set(unit);
        }
    }
}
