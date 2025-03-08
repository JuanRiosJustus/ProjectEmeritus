package main.ui.presets.loadout;

import main.constants.Constants;
import main.game.components.IdentityComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.stores.pools.ColorPaletteV1;
import main.graphics.Animation;
import main.game.entity.Entity;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.temporary.JImage;
import main.ui.components.Datasheet;
import main.ui.outline.OutlineLabel;
import main.ui.components.ResourceBar;
import main.ui.custom.SwingUiUtils;
import main.utils.ImageUtils;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SummaryCard extends JPanel {

    private Entity mEntity;
    private final OutlineLabel mNameTag = new OutlineLabel("N/A",  SwingConstants.LEFT, 1);
    private final OutlineLabel mTypeTag = new OutlineLabel("N/A", JLabel.CENTER, 1);
    private final OutlineLabel mLevelTag = new OutlineLabel("N/A", SwingConstants.LEFT, 1);
    private final ResourceBar mHealthBar =
            ResourceBar.createResourceBar("", ResourceBar.EXCLUDE_LABEL_SHOW_CENTERED_VALUE);
    private final ResourceBar mManaBar =
            ResourceBar.createResourceBar("", ResourceBar.EXCLUDE_LABEL_SHOW_CENTERED_VALUE);
    private final ResourceBar mStaminaBar =
            ResourceBar.createResourceBar("", ResourceBar.EXCLUDE_LABEL_SHOW_CENTERED_VALUE);
    private final ResourceBar mExperienceBar =
            ResourceBar.createResourceBar("XP", ResourceBar.EXCLUDE_LABEL_SHOW_CURRENT_TO_MAX_VALUE);

    private JImage mJImage = new JImage();
    private final JPanel mMainPanel = new JPanel();
    private JPanel mRow1 = new JPanel();
    private final JPanel mRow2 = new JPanel();
    private Datasheet mDatasheet = new Datasheet();
    private Datasheet mStatistics = new Datasheet();
    private Datasheet mActions = new Datasheet();
    public SummaryCard(int width, int height, Entity entity,  Color color) {
        update(width, height, entity, color);
    }

    public void update(Entity entity,  Color color) {
        update(-1, -1, entity, color);
    }
    public void update(int width, int height, Entity unitEntity, Color color) {
        int spriteSizes = Constants.NATIVE_SPRITE_SIZE;
        removeAll();
        mRow1.removeAll();
        mRow2.removeAll();
        mMainPanel.removeAll();

        mEntity = unitEntity;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(color);
        if (width >= 1 && height >= 1) {
            setPreferredSize(new Dimension(width, height));
        }
        // get the image to show for this row

        BufferedImage image = null;
        Animation animation;
        StatisticsComponent statisticsComponent = null;
        if (unitEntity != null) {
            statisticsComponent = unitEntity.get(StatisticsComponent.class);
            String id = AssetPool.getInstance().getOrCreateAsset(
                    spriteSizes,
                    spriteSizes,
                    statisticsComponent.getUnit(),
                    AssetPool.STATIC_ANIMATION,
                    0,
                    statisticsComponent.getUnit()
            );
            if (id == null) {
                image = new BufferedImage(spriteSizes, spriteSizes, BufferedImage.TYPE_INT_ARGB);
            } else {
                animation = AssetPool.getInstance().getAnimation(id);
                image = animation.toImage();
            }
        } else {
            image = new BufferedImage(
                    spriteSizes,
                    spriteSizes,
                    BufferedImage.TYPE_INT_ARGB
            );
        }

        if (image == null) { return; }
        // The image to show the unit being looked at
        int innerPanelHeight = (int) getPreferredSize().getHeight();
        int innerPanelWidth = (int) getPreferredSize().getWidth();
        mJImage = new JImage(ImageUtils.getResizedImage(image, spriteSizes, spriteSizes), false);
        mJImage.setPreferredSize(new Dimension(spriteSizes, (int) getPreferredSize().getHeight()));
        mJImage.setBackground(color);
        mJImage.setOpaque(false);

        // The data for the unit to be loking at
        mMainPanel.setLayout(new GridBagLayout());
//        mMainPanel.setPreferredSize(new Dimension(innerPanelWidth, innerPanelHeight));
        mMainPanel.setOpaque(false);

        int rowHeights = innerPanelHeight / 4;

        GridBagConstraints gbc = SwingUiUtils.createGbc(0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        mRow1 = createRow1(unitEntity, innerPanelWidth, rowHeights);
        mMainPanel.add(mRow1, gbc);

        mRow2.setLayout(new BoxLayout(mRow2, BoxLayout.X_AXIS));
        mRow2.setBackground(ColorPaletteV1.TRANSPARENT);
        mRow2.setOpaque(true);

        mHealthBar.setBackground(ColorPaletteV1.BLACK);
        mHealthBar.setForeground(ColorPaletteV1.DARK_RED_V1);
        if (unitEntity != null) {
            mHealthBar.setResourceValue(0,
                    statisticsComponent.getCurrentHealth(), statisticsComponent.getTotalHealth());
        }

        mManaBar.setBackground(ColorPaletteV1.BLACK);
        mManaBar.setForeground(ColorPaletteV1.PURPLE);
        if (unitEntity != null) {
            mManaBar.setResourceValue(0,
                    statisticsComponent.getCurrentMana(), statisticsComponent.getTotalMana());
        }

        mStaminaBar.setBackground(ColorPaletteV1.BLACK);
        mStaminaBar.setForeground(ColorPaletteV1.GOLD);
        if (unitEntity != null) {
            mStaminaBar.setResourceValue(0,
                    statisticsComponent.getCurrentStamina(), statisticsComponent.getTotalStamina());
        }

        mStatistics = new Datasheet();
        mStatistics.setBackground(color);
        if (unitEntity != null) {
            mStatistics.addItem("Statistics");
            List<String> exclude = Arrays.asList("Level", "Experience");
            List<String> keys = statisticsComponent.getStatisticNodeKeys().stream().filter(e -> !exclude.contains(e)).toList();
            for (String key : keys) {
                mStatistics.addItem(key + ": " + statisticsComponent.getTotal(key));
            }
        }

        mRow2.add(mHealthBar);
        mRow2.add(mManaBar);
        mRow2.add(mStaminaBar);

        gbc.gridy = 1;
        mMainPanel.add(mRow2, gbc);

        gbc.gridy = 2;
        mMainPanel.add(mStatistics, gbc);

        gbc.gridy = 3;
        mActions = new Datasheet();
        mActions.setBackground(color);
        if (unitEntity != null) {
            mActions.addItem("Actions");
            Set<String> abilities = statisticsComponent.getAbilities();
            for (String key : abilities) {
                mActions.addItem(key);
            }
        }


        mMainPanel.add(mActions, gbc);
        if (unitEntity != null) {
            mExperienceBar.setResourceValue(0,
                    statisticsComponent.getLevel(), StatisticsComponent.getExperienceNeeded(statisticsComponent.getCurrentExperience()));
        }
        mExperienceBar.setBackground(ColorPaletteV1.BLACK);
        mExperienceBar.setForeground(ColorPaletteV1.BLUE);
        mMainPanel.add(mExperienceBar, gbc);

        add(mJImage);
        add(mMainPanel);
        setBackground(Color.DARK_GRAY);
    }
//    public void setBackground(Color color) {
//        mJImage.setBackground(color);
//    }

    private JPanel createRow1(Entity entity, int rowWidth, int rowHeight) {

        StatisticsComponent statisticsComponent = entity == null ? null : entity.get(StatisticsComponent.class);
        IdentityComponent identityComponent = entity == null? null : entity.get(IdentityComponent.class);


        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setBackground(Color.DARK_GRAY);
        row1.setOpaque(true);
        row1.setPreferredSize(new Dimension(rowWidth, rowHeight));

        if (identityComponent != null) { mNameTag.setText(entity + " (" + statisticsComponent.getUnit() + ")"); }
        mNameTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .6), rowHeight));
        mNameTag.setOpaque(true);
        mNameTag.setBackground(ColorPaletteV1.TRANSPARENT);

        if (entity != null) { mTypeTag.setText( statisticsComponent.getType().iterator().next()); }
        mTypeTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .2), rowHeight));
        mTypeTag.setOpaque(true);
        mTypeTag.setBackground(ColorPaletteV1.TRANSPARENT);

        if (entity != null) { mLevelTag.setText("Lv " + statisticsComponent.getLevel()); }
        mLevelTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .2), rowHeight));
        mLevelTag.setOpaque(true);
        mLevelTag.setBackground(ColorPaletteV1.TRANSPARENT);

        row1.add(mLevelTag);
        row1.add(mNameTag);
        row1.add(mTypeTag);

        return row1;
    }

    public JImage getImage() { return mJImage; }
    public JPanel getMainPanel() { return mMainPanel; }
    public void setColors(Color color) {
        super.setBackground(color);
        mMainPanel.setBackground(color);
        mRow1.setBackground(color);
        mRow2.setBackground(color);
        mJImage.setBackground(color);
        // TODO figrue out why dropdown box does not chagne colors dynamically
//        mDatasheet.setRendererBackground(color);
//        setBackground(color.darker());
    }
}
