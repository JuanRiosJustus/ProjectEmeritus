package main.ui.custom;

import main.game.components.Animation;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.temporary.JImageLabel;
import main.utils.ImageUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TargetFrameTag extends JPanel {

    private JImageLabel mImageLabel = null;
    private JButton mNameLabel = new JButton();
    private int mUnitImageSize = 0;
    public TargetFrameTag(int width, int height) {
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.weightx = 1;
        gbc1.weighty = 1;
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.fill = GridBagConstraints.BOTH;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setBackground(ColorPalette.TRANSPARENT);

        mUnitImageSize = Math.min(width, height);
        mImageLabel = new JImageLabel(mUnitImageSize, mUnitImageSize);
        mImageLabel.setImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        mImageLabel.setPreferredSize(new Dimension(mUnitImageSize, mUnitImageSize));
        mImageLabel.setMaximumSize(new Dimension(mUnitImageSize, mUnitImageSize));
        mImageLabel.setMinimumSize(new Dimension(mUnitImageSize, mUnitImageSize));
//        mImageLabel.set
        mImageLabel.setBackground(ColorPalette.TRANSPARENT);
        add(mImageLabel, gbc1);

        int namePlatWidth = (int) (width - (mUnitImageSize * 1.4));
        mNameLabel.setPreferredSize(new Dimension(namePlatWidth, height));
        mNameLabel.setMinimumSize(new Dimension(namePlatWidth, height));
        mNameLabel.setMaximumSize(new Dimension(namePlatWidth, height));
        mNameLabel.setBorderPainted(false);
        mNameLabel.setFocusPainted(false);
        mNameLabel.setBackground(ColorPalette.TRANSPARENT);
        mNameLabel.setForeground(ColorPalette.WHITE);
        mNameLabel.setFont(FontPool.getInstance().getFont(mNameLabel.getFont().getSize()).deriveFont(Font.BOLD).deriveFont(20f));
        mNameLabel.setVerticalAlignment(SwingConstants.CENTER);
        mNameLabel.setText("Select a Unit");
        gbc1.gridx = 1;
        add(mNameLabel, gbc1);
    }

    public void setImage(Animation animation, String text) {
        BufferedImage mCurrentImage = animation.getFrame(0);
        BufferedImage image = ImageUtils.getResizedImage(mCurrentImage, mUnitImageSize, mUnitImageSize);
        mImageLabel.setImage(image);
        mNameLabel.setText(text);
    }
}
