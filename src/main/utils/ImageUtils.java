package main.utils;

import main.constants.ColorPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

    public static final int ALPHA_OPAQUE = 1;
    public static final int ALPHA_BIT_MASKED = 2;
    public static final int ALPHA_BLEND = 3;

    public static Image loadImage(String filePath) {
        try {
            URL resource = ImageUtils.class.getResource(filePath);
            if (resource == null) { throw new Exception(); }
            Image imageFromDisk = ImageIO.read(resource);
            BufferedImage compatibleImage = (BufferedImage) createCompatibleImage(
                    imageFromDisk.getWidth(null), imageFromDisk.getHeight(null),
                    ALPHA_BIT_MASKED
            );

            Graphics2D graphics = compatibleImage.createGraphics();
            graphics.drawImage(imageFromDisk, 0, 0, null);

            graphics.dispose();
            return compatibleImage;
        } catch (IOException e) {
//            ELoggerManager.get().log("Could not load image from path: " + filePath);
        } catch (Exception e) {
//            ELoggerManager.get().log(e.getMessage());
        }

        return null;
    }

    public static Image createCompatibleImage(int width, int height, int transparency) {
        GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        return graphicsConfiguration.createCompatibleImage(width, height, transparency);
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaMultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaMultiplied, null);
    }

    public static BufferedImage getResizedImage(BufferedImage src, int newWidth, int newHeight){
        BufferedImage resized = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src.getScaledInstance(newWidth, newHeight, src.getType()), 0, 0, null);
        g.dispose();
        return resized;
    }

    public static BufferedImage[] sway(BufferedImage image, int frames, double swayFactor) {
//        BufferedImage[] animatedFrames = new BufferedImage[frames];
//        AffineTransform at = new AffineTransform();
//
//        for (int i = 0; i < animatedFrames.length; i++) {
//
//        }
//        g2.draw(shape);
//
//        // Transform the Graphics2D.
//        AffineTransform sat = AffineTransform.getTranslateInstance(150, 0);
//        sat.shear(-.5, 0);
//        g2.transform(sat);
//        AffineTransformOp op = new AffineTransformOp();
//
//        //Creating shear transformation
//        Shear shear = new Shear();
        return null;
    }

    public static BufferedImage[] createAnimationViaYStretch(BufferedImage image, int length, double increase) {
        BufferedImage[] animationFrames = new BufferedImage[length];
        double size = 0;
        for (int index = 0; index < length; index++) {
            int newHeight = (int) (image.getHeight() + size);
            BufferedImage newImage = getResizedImage(image, image.getWidth(), newHeight);
            RescaleOp op = new RescaleOp(1f, 0, null);
            animationFrames[index] = (op.filter(newImage, null));
            if (index < animationFrames.length / 2) {
                size += increase;
            } else {
                size -= increase;
            }
        }
        return animationFrames;
    }

    public static BufferedImage[] createShearingAnimation(BufferedImage image, int length, double shear) {
        BufferedImage[] animationFrames = new BufferedImage[length];
        // Make sure image copy is weeee bit smaller so it can fit inside square when it moves
        int height = (int) (image.getHeight() * .9);
        int width = (int) (image.getWidth() * .9);
        BufferedImage copy = ImageUtils.getResizedImage(image, width, height);

        double sizeIncrease = 0;
        double rate = shear / animationFrames.length;
        for (int index = 0; index < animationFrames.length; index++) {
            // Keeping image same height as the rest of the sprites
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), copy.getType());
            Graphics2D g2 = newImage.createGraphics();
            AffineTransform at = new AffineTransform();
            at.translate(sizeIncrease * -90, 0);
            at.shear( sizeIncrease, 0);
            g2.transform(at);
            g2.setColor(ColorPalette.TRANSPARENT);
            g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
            // Try to put copy of the image smack dab in the middle of the sprite
            g2.drawImage(copy, (int)(copy.getWidth() * .1), (int)(copy.getHeight() * .1), null);

            animationFrames[index] = newImage;

            if (index < animationFrames.length * .25) {
                sizeIncrease += rate * 1;
            } else if (index < animationFrames.length * .75) {
                sizeIncrease -= rate * 1;
            } else {
                sizeIncrease += rate * 1;
            }
        }
        return animationFrames;
    }

//    public static BufferedImage[] stretch(BufferedImage image, int frames, double sFactor) {
//        BufferedImage[] animatedFrames = new BufferedImage[frames];
//        animatedFrames[0] = image;
//        double sizeIncrease = 0;
//        for (int index = 1; index < frames; index++) {
//            animatedFrames[index] = getResizedImage(image, image.getWidth(),image.getHeight() + (int)sizeIncrease);
//            if (index < animatedFrames.length / 2) {
//                sizeIncrease += sFactor;
//            } else {
//                sizeIncrease -= sFactor;
//            }
//        }
//        return animatedFrames;
//    }

//    private static boolean isNotCompletelyTransparent(BufferedImage img) {
//        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
//            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
//                int color = img.getRGB(pixelCol, pixelRow);
//                int alpha = (color>>24) & 0xff;
//                // if there is a non alpha color in this image, then it is valid
//                if (alpha != 0) { return true; }
//            }
//        }
//        return false;
//    }

    public static BufferedImage[] createFlickeringAnimation(BufferedImage image, int frames, float scaleFactor) {
        BufferedImage[] animatedFrames = new BufferedImage[frames];
        animatedFrames[0] = image;
        float brightness = 1f;
        RescaleOp op;
        for (int index = 1; index < frames; index++) {
            op = new RescaleOp(brightness, 0, null);
            animatedFrames[index] = (op.filter(image, null));
            if (index < frames / 2) {
                brightness += scaleFactor;
            } else {
                brightness -= scaleFactor;
            }
        }
        return animatedFrames;
    }


    public static boolean isNotCompletelyTransparent(BufferedImage img) {
        for (int pixelRow = 0; pixelRow < img.getHeight(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < img.getWidth(); pixelCol++) {
                int color = img.getRGB(pixelCol, pixelRow);
                int alpha = (color>>24) & 0xff;
                // if there is a non alpha color in this image, then it is valid
                if (alpha != 0) { return true; }
            }
        }
        return false;
    }

    public static BufferedImage empty(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(ColorPalette.TRANSPARENT);
        g.dispose();
        return image;
    }

    public static BufferedImage[] spinify(BufferedImage image, float scaleFactor) {
        BufferedImage[] animatedFrames = new BufferedImage[180];
        animatedFrames[0] = image;
        float brightness = 1f;
        RescaleOp op;
        double angle = 0;
        double delta = 360f / animatedFrames.length;
        boolean enlighten = true;
        int count = 0;
        for (int index = 1; index < animatedFrames.length; index++) {
            BufferedImage newImage = rotateImageByDegrees(image, angle);
            op = new RescaleOp(brightness, 0, null);
            animatedFrames[index] = (op.filter(newImage, null));
            if (enlighten) {
                brightness += scaleFactor;
                count++;
                if (count >= 10) { enlighten = false; }
            } else {
                brightness -= scaleFactor;
                count--;
                if (count <= 0) { enlighten = true; }
            }
            angle += delta;
        }
        return animatedFrames;
    }

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angle), img.getWidth()/2f, img.getHeight()/2f);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(ColorPalette.TRANSPARENT);
        g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }
}
