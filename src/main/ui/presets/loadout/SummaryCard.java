package main.ui.presets.loadout;

import main.game.components.Animation;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stores.pools.ColorPalette;
import main.graphics.temporary.JImage;
import main.ui.components.Datasheet;
import main.ui.components.OutlineLabel;
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

    public SummaryCard(int width, int height, Entity entity, int spriteSizes) {
        update(width, height, entity, spriteSizes);
    }

    public void update(Entity entity, int spriteSize) {
        update(-1, -1, entity, spriteSize);
    }
    public void update(int width, int height, Entity entity, int spriteSizes) {
        removeAll();
        mRow1.removeAll();
        mRow2.removeAll();
        mMainPanel.removeAll();

        mEntity = entity;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(ColorPalette.TRANSPARENT);
        if (width >= 1 && height >= 1) {
            setPreferredSize(new Dimension(width, height));
        }
        // get the image to show for this row

        BufferedImage image;
        Animation animation;
        Statistics statistics = null;
        if (entity != null) {
            animation = entity.get(Animation.class);
            statistics = entity.get(Statistics.class);
            image = animation.toImage();
        } else {
            image = new BufferedImage(
                    spriteSizes,
                    spriteSizes,
                    BufferedImage.TYPE_INT_ARGB
            );
        }

        // The image to show the unit being looked at
        int innerPanelHeight = (int) getPreferredSize().getHeight();
        int innerPanelWidth = (int) getPreferredSize().getWidth();
        mJImage = new JImage(ImageUtils.getResizedImage(image, spriteSizes, spriteSizes), false);
        mJImage.setPreferredSize(new Dimension(spriteSizes, (int) getPreferredSize().getHeight()));
        mJImage.setOpaque(true);
        mJImage.setBackground(Color.DARK_GRAY);
        mJImage.setForeground(Color.BLACK);

        // The data for the unit to be loking at
        mMainPanel.setLayout(new GridBagLayout());
        mMainPanel.setPreferredSize(new Dimension(innerPanelWidth, innerPanelHeight));
        mMainPanel.setBackground(ColorPalette.TRANSPARENT);
        mMainPanel.setOpaque(true);

        int rowHeights = innerPanelHeight / 4;

        GridBagConstraints gbc = SwingUiUtils.createGbc(0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        mRow1 = createRow1(entity, innerPanelWidth, rowHeights);
        mMainPanel.add(mRow1, gbc);

        mRow2.setLayout(new BoxLayout(mRow2, BoxLayout.X_AXIS));
        mRow2.setBackground(ColorPalette.TRANSPARENT);
        mRow2.setOpaque(true);

        mHealthBar.setBackground(ColorPalette.BLACK);
        mHealthBar.setForeground(ColorPalette.DARK_RED_V1);
        if (entity != null) {
            mHealthBar.setResourceValue(0,
                    statistics.getStatCurrent(Statistics.HEALTH), statistics.getStatTotal(Statistics.HEALTH));
        }

        mManaBar.setBackground(ColorPalette.BLACK);
        mManaBar.setForeground(ColorPalette.PURPLE);
        if (entity != null) {
            mManaBar.setResourceValue(0,
                    statistics.getStatCurrent(Statistics.MANA), statistics.getStatTotal(Statistics.MANA));
        }

        mStaminaBar.setBackground(ColorPalette.BLACK);
        mStaminaBar.setForeground(ColorPalette.GOLD);
        if (entity != null) {
            mStaminaBar.setResourceValue(0,
                    statistics.getStatCurrent(Statistics.MANA), statistics.getStatTotal(Statistics.MANA));
        }

        mDatasheet = new Datasheet();
        if (entity != null) {
            mDatasheet.addItem("Statistics");
            List<String> exclude = Arrays.asList("Level", "Experience");
            List<String> keys = statistics.getStatNodeKeys().stream().filter(e -> !exclude.contains(e)).toList();
            for (String key : keys) {
                mDatasheet.addItem(key + ": " + statistics.getStatTotal(key));
            }
        }

        mRow2.add(mHealthBar);
        mRow2.add(mManaBar);
        mRow2.add(mStaminaBar);

        gbc.gridy = 1;
        mMainPanel.add(mRow2, gbc);

        gbc.gridy = 2;
        mMainPanel.add(mDatasheet, gbc);

        gbc.gridy = 3;
        mDatasheet = new Datasheet();
        if (entity != null) {
            mDatasheet.addItem("Abilities");
            Set<String> abilities = statistics.getAbilities();
            for (String key : abilities) {
                mDatasheet.addItem(key);
            }
        }
        mMainPanel.add(mDatasheet, gbc);
        if (entity != null) {
            mExperienceBar.setResourceValue(0,
                    statistics.getStatModified(Statistics.LEVEL), Statistics.getExperienceNeeded(statistics.getStatBase(Statistics.LEVEL)));
        }
        mExperienceBar.setBackground(ColorPalette.BLACK);
        mExperienceBar.setForeground(ColorPalette.BLUE);
        mMainPanel.add(mExperienceBar, gbc);

        add(mJImage);
        add(mMainPanel);
        setBackground(Color.DARK_GRAY);
    }

    private JPanel createRow1(Entity entity, int rowWidth, int rowHeight) {

        Statistics statistics = entity == null ? null : entity.get(Statistics.class);
        Identity identity = entity == null? null : entity.get(Identity.class);


        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setBackground(Color.DARK_GRAY);
        row1.setOpaque(true);
        row1.setPreferredSize(new Dimension(rowWidth, rowHeight));

        if (identity != null) { mNameTag.setText(entity + " (" + identity.getName() + ")"); }
        mNameTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .6), rowHeight));
        mNameTag.setOpaque(true);
        mNameTag.setBackground(ColorPalette.TRANSPARENT);

        if (entity != null) { mTypeTag.setText( statistics.getType().iterator().next()); }
        mTypeTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .2), rowHeight));
        mTypeTag.setOpaque(true);
        mTypeTag.setBackground(ColorPalette.TRANSPARENT);

        if (entity != null) { mLevelTag.setText("Lv " + statistics.getLevel()); }
        mLevelTag.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth()  * .2), rowHeight));
        mLevelTag.setOpaque(true);
        mLevelTag.setBackground(ColorPalette.TRANSPARENT);

        row1.add(mLevelTag);
        row1.add(mNameTag);
        row1.add(mTypeTag);

        return row1;
    }

    public JImage getImage() { return mJImage; }
    public JPanel getMainPanel() { return mMainPanel; }
    public void setColors(Color color) {
        mMainPanel.setBackground(color);
        mRow1.setBackground(color);
        mRow2.setBackground(color.darker());
        mJImage.setBackground(color);
        // TODO figrue out why dropdown box does not chagne colors dynamically
//        mDatasheet.setRendererBackground(color);
        setBackground(color.darker());
    }
}
